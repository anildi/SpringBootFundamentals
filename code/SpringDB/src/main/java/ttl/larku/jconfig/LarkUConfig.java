package ttl.larku.jconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import ttl.larku.dao.BaseDAO;
import ttl.larku.dao.CourseDAO;
import ttl.larku.dao.inmemory.InMemoryClassDAO;
import ttl.larku.dao.inmemory.InMemoryCourseDAO;
import ttl.larku.dao.inmemory.InMemoryStudentDAO;
import ttl.larku.dao.jpahibernate.JPAClassDAO;
import ttl.larku.dao.jpahibernate.JPACourseDAO;
import ttl.larku.dao.jpahibernate.JPAStudentDAO;
import ttl.larku.dao.repository.ClassRepo;
import ttl.larku.dao.repository.CourseRepo;
import ttl.larku.domain.DummyTimerConfigFromProps;
import ttl.larku.domain.ScheduledClass;
import ttl.larku.domain.Student;
import ttl.larku.service.ClassDaoService;
import ttl.larku.service.ClassRepoService;
import ttl.larku.service.ClassService;
import ttl.larku.service.CourseDaoService;
import ttl.larku.service.CourseRepoService;
import ttl.larku.service.CourseService;
import ttl.larku.service.RegistrationService;
import ttl.larku.service.StudentDaoService;
import ttl.larku.service.StudentRepoService;
import ttl.larku.service.props.ServiceThatWeDontOwn;

@Configuration
@PropertySource({"classpath:/larkUContext.properties"})
//@EnableAspectJAutoProxy
public class LarkUConfig {

    @Bean
    @Profile("development")
    public BaseDAO<Student> studentDAO() {
        return inMemoryStudentDAO();
//        return jpaStudentDAO();
    }

    @Bean(name = "studentDAO")
    @Profile("production")
    public BaseDAO<Student> studentDAOJpa() {
        return jpaStudentDAO();
    }

    @Bean
    @Profile("development")
    public CourseDAO courseDAO() {
        return inMemoryCourseDAO();
//        return jpaCourseDAO();
    }

    @Bean(name = "courseDAO")
    @Profile("production")
    public CourseDAO courseDAOJPA() {
        return jpaCourseDAO();
    }

    @Bean
    @Profile("development")
    public BaseDAO<ScheduledClass> classDAO() {
        return inMemoryClassDAO();
//        return jpaClassDAO();
    }

    @Bean(name = "classDAO")
    @Profile("production")
    public BaseDAO<ScheduledClass> classDAOJPA() {
        return jpaClassDAO();
    }

    @Bean
    @Profile("!rating")
    public CourseService courseService() {
        CourseDaoService cc = new CourseDaoService();
        cc.setCourseDAO(courseDAO());

        return cc;
    }

    @Bean
    public CourseRepoService courseRepoService(CourseRepo courseRepo) {
        CourseRepoService cc = new CourseRepoService();
        cc.setCourseRepo(courseRepo);
        return cc;
    }

    @Bean
    public ClassService classService(CourseService courseService) {
        ClassDaoService cs = new ClassDaoService();
//        cs.setClassDAO(classDAO());
        cs.setClassDAO(jpaClassDAO());
        cs.setCourseService(courseService);
        return cs;
    }

//    @Autowired
//    private ClassRepo classRepo;

    @Bean
    public ClassRepoService classRepoService(ClassRepo classRepo, CourseRepoService courseRepoService) {
        ClassRepoService cs = new ClassRepoService();
        cs.setClassDAO(classRepo);
        cs.setCourseService(courseRepoService);
//        cs.setCourseService(courseService());
        return cs;
    }

//    @Autowired
//    private StudentDaoService studentDaoService;

    @Bean
    @Primary
    public RegistrationService registrationService(StudentDaoService studentDaoService,
                                                   CourseService courseService,
                                                   ClassService classService) {
        RegistrationService rs = new RegistrationService();
        rs.setStudentService(studentDaoService);
        rs.setCourseService(courseService);
        rs.setClassService(classService);

        return rs;
    }

//    @Autowired
//    private StudentRepoService studentRepoService;

    @Bean
    public RegistrationService registrationRepoService(StudentRepoService studentRepoService,
                                                       CourseRepoService courseRepoService,
                                                       ClassRepoService classRepoService) {
        RegistrationService rs = new RegistrationService();
        rs.setStudentService(studentRepoService);
        rs.setCourseService(courseRepoService);
        rs.setClassService(classRepoService);

        return rs;
    }

    @Bean
    public InMemoryStudentDAO inMemoryStudentDAO() {
        return new InMemoryStudentDAO();
    }

    @Bean
    public JPAStudentDAO jpaStudentDAO() {
        return new JPAStudentDAO();
    }

    @Bean
    public InMemoryCourseDAO inMemoryCourseDAO() {
        return new InMemoryCourseDAO();
    }

    @Bean
    public JPACourseDAO jpaCourseDAO() {
        return new JPACourseDAO();
    }

    @Bean
    public InMemoryClassDAO inMemoryClassDAO() {
        return new InMemoryClassDAO();
    }

    @Bean
    public JPAClassDAO jpaClassDAO() {
        return new JPAClassDAO();
    }


    /**
     * You can use @ConfigurationProperties on an @Bean method.
     * Can be  useful when you want to initialize classes you don't
     * own.
     * @return
     */

    @Bean
    @ConfigurationProperties("ttl.timer.config")
    public DummyTimerConfigFromProps dummyTimerConfigFromProps() {
        return new DummyTimerConfigFromProps();
    }

    @Bean
    @ConfigurationProperties("ttl.stwdo.config")
    public ServiceThatWeDontOwn serviceThatWeDontOwn() {
        return new ServiceThatWeDontOwn();
    }
}