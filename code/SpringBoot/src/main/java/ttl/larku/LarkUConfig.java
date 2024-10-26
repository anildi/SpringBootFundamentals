package ttl.larku;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ttl.larku.dao.BaseDAO;
import ttl.larku.dao.jpahibernate.JPAClassDAO;
import ttl.larku.dao.jpahibernate.JPACourseDAO;
import ttl.larku.dao.jpahibernate.JPAStudentDAO;
import ttl.larku.domain.Course;
import ttl.larku.domain.ScheduledClass;
import ttl.larku.domain.Student;
import ttl.larku.service.ClassService;
import ttl.larku.service.CourseService;
import ttl.larku.service.RegistrationService;
import ttl.larku.service.StudentService;
import ttl.larku.service.props.ServiceThatWeDontOwn;

@Configuration
@PropertySource({"classpath:/larkUContext.properties"})
public class LarkUConfig {

    private LarkUTestDataConfig testDataProducer = new LarkUTestDataConfig();

    @Bean
    @Profile("development")
    public BaseDAO<Student> studentDAO() {
        return inMemoryStudentDAO();
    }

    @Bean(name = "studentDAO")
    @Profile("production")
    public BaseDAO<Student> studentDAOJpa() {
        return jpaStudentDAO();
    }

    @Bean
    @Profile("development")
    public BaseDAO<Course> courseDAO() {
        return inMemoryCourseDAO();
    }

    @Bean(name = "courseDAO")
    @Profile("production")
    public BaseDAO<Course> courseDAOJPA() {
        return jpaCourseDAO();
    }

    @Bean
    @Profile("development")
    public BaseDAO<ScheduledClass> classDAO() {
        return inMemoryClassDAO();
    }

    @Bean(name = "classDAO")
    @Profile("production")
    public BaseDAO<ScheduledClass> classDAOJPA() {
        return jpaClassDAO();
    }

    @Bean
    public CourseService courseService() {
        CourseService cc = new CourseService();
        cc.setCourseDAO(courseDAO());

        return cc;
    }

    @Bean
    public ClassService classService() {
        ClassService cs = new ClassService();
        cs.setClassDAO(classDAO());
        cs.setCourseService(courseService());
        return cs;
    }

    //This works okay when running from the IDE, but blows up with a
    //CircularReference exception when running from the command line.
    //Injecting the StudentService as an argument seems to work in
    //all cases.
//    @Autowired
//    private StudentService studentService;


    //Injection with an argument.  This approach works when running from
    //the IDE and the command line.  As opposed to @Autowired at the field
    //level, as above.
    @Bean
    public RegistrationService registrationService(StudentService studentService) {
        RegistrationService rs = new RegistrationService();
        rs.setStudentService(studentService);
        rs.setCourseService(courseService());
        rs.setClassService(classService());

        return rs;
    }

    public BaseDAO<Student> inMemoryStudentDAO() {
        BaseDAO<Student> bs = testDataProducer.studentDAOWithInitData();
        return bs;
    }

    public BaseDAO<Student> jpaStudentDAO() {
        return new JPAStudentDAO();
    }

    public BaseDAO<Course> inMemoryCourseDAO() {
        return testDataProducer.courseDAOWithInitData();
//        return new InMemoryCourseDAO();
    }

    public BaseDAO<Course> jpaCourseDAO() {
        return new JPACourseDAO();
    }

    public BaseDAO<ScheduledClass> inMemoryClassDAO() {
        return testDataProducer.classDAOWithInitData();
//        return new InMemoryClassDAO();
    }

    public BaseDAO<ScheduledClass> jpaClassDAO() {
        return new JPAClassDAO();
    }

    /**
     * You can use @ConfigurationProperties on an @Bean method.
     * Can be  useful when you want to initialize classes you don't
     * own.
     *
     * @return
     */

    @Bean
    @ConfigurationProperties("ttl.stwdo.config")
    public ServiceThatWeDontOwn serviceThatWeDontOwn() {
        return new ServiceThatWeDontOwn();
    }

    /**
     * For doing JSR 303 type validations.
     * e.g. @NotNull, @Size, etc.  Look in Student for
     * an example of annotation usage.  Look in
     * StudentRestController for examples of how to use
     * this to do validation.
     * @return
     */
    @Bean
    public Validator validator() {
       return new LocalValidatorFactoryBean();
    }
}
