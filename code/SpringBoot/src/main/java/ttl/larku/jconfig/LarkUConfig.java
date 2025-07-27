package ttl.larku.jconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ttl.larku.dao.BaseDAO;
import ttl.larku.dao.inmemory.InMemoryClassDAO;
import ttl.larku.dao.inmemory.InMemoryCourseDAO;
import ttl.larku.dao.inmemory.InMemoryStudentDAO;
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
   public ClassService classService(CourseService courseService) {
      ClassService cs = new ClassService();
      cs.setClassDAO(classDAO());
      cs.setCourseService(courseService);
      return cs;
   }


   @Bean
   public RegistrationService registrationService(StudentService studentService,
                                                  CourseService courseService,
                                                  ClassService classService) {
      RegistrationService rs = new RegistrationService();
      rs.setStudentService(studentService);
      rs.setCourseService(courseService);
      rs.setClassService(classService);

      return rs;
   }

   @Bean
   public InMemoryStudentDAO inMemoryStudentDAO() {
      InMemoryStudentDAO bs = testDataProducer.studentDAOWithInitData();
      return bs;
   }

   @Bean
   public JPAStudentDAO jpaStudentDAO() {
      JPAStudentDAO bs = testDataProducer.studentJPADAOWithInitData();
      return bs;
   }

   @Bean
   public InMemoryCourseDAO inMemoryCourseDAO() {
      InMemoryCourseDAO dao = new InMemoryCourseDAO();
      testDataProducer.initCourseDAO(dao);
      return dao;
//        return new InMemoryCourseDAO();
   }

   @Bean
   public JPACourseDAO jpaCourseDAO() {
      var dao = new JPACourseDAO();
      testDataProducer.initCourseDAO(dao);
      return dao;
   }

   @Bean
   public InMemoryClassDAO inMemoryClassDAO() {
      return testDataProducer.classDAOWithInitData();
//        return new InMemoryClassDAO();
   }

   @Bean
   public BaseDAO<ScheduledClass> jpaClassDAO() {
      var dao = new JPAClassDAO();
      testDataProducer.initClassDAO(dao);
      return dao;
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
    *
    * @return
    */
   @Bean
   public Validator validator() {
      return new LocalValidatorFactoryBean();
   }
}
