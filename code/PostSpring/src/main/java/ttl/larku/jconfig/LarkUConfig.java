package ttl.larku.jconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import ttl.larku.dao.BaseDAO;
import ttl.larku.dao.inmemory.InMemoryCourseDAO;
import ttl.larku.dao.inmemory.InMemoryStudentDAO;
import ttl.larku.dao.jpahibernate.JPACourseDAO;
import ttl.larku.dao.jpahibernate.JPAStudentDAO;
import ttl.larku.domain.Course;
import ttl.larku.domain.Student;
import ttl.larku.service.CourseService;
import ttl.larku.service.StudentService;

@Configuration
@ComponentScan({"ttl.larku.service", "ttl.larku.dao"})
//@PropertySource({"classpath:/larkUContext.properties"})
public class LarkUConfig {

    @Autowired
    private Environment env;

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
    public StudentService studentService() {
        StudentService ss = new StudentService();
        ss.setStudentDAO(studentDAO());

        return ss;
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
    public BaseDAO<Student> inMemoryStudentDAO() {
        return new InMemoryStudentDAO();
    }

    @Bean
    public BaseDAO<Student> jpaStudentDAO() {
        return new JPAStudentDAO();
    }



    @Bean
    public CourseService courseService() {
        CourseService cc = new CourseService();
        cc.setCourseDAO(courseDAO());

        return cc;
    }


    @Bean
    public BaseDAO<Course> inMemoryCourseDAO() {
        return new InMemoryCourseDAO("Mem");
    }

    @Bean
    public BaseDAO<Course> jpaCourseDAO() {
        return new JPACourseDAO("JPA");
    }
}