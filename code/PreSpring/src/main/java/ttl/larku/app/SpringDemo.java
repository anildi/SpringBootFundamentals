package ttl.larku.app;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ttl.larku.domain.Course;
import ttl.larku.domain.Student;
import ttl.larku.jconfig.LarkUConfig;
import ttl.larku.service.CourseService;
import ttl.larku.service.StudentService;

import javax.swing.Spring;
import java.util.List;

/**
 * @author whynot
 */
public class SpringDemo {

    public static void main(String[] args) {
        SpringDemo springDemo = new SpringDemo();
        //springDemo.go();
        springDemo.goCourse();
    }

    public void go() {
        //ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        ApplicationContext context = new AnnotationConfigApplicationContext(LarkUConfig.class);

        StudentService ss = context.getBean("studentService", StudentService.class);

        StudentService s2 = context.getBean("studentService", StudentService.class);

        List<Student> students = ss.getAllStudents();
        System.out.println("allStudent.size: " + students.size());
        students.forEach(System.out::println);
    }

    public void goCourse() {
        //ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        ApplicationContext context = new AnnotationConfigApplicationContext(LarkUConfig.class);

        CourseService cs = context.getBean("courseService", CourseService.class);

        List<Course> courses = cs.getAllCourses();
        System.out.println("allCourse.size: " + courses.size());
        courses.forEach(System.out::println);
    }
}
