package ttl.larku.app;

import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ttl.larku.LarkUConfig;
import ttl.larku.domain.Course;
import ttl.larku.domain.Student;
import ttl.larku.service.CourseService;
import ttl.larku.service.StudentService;

public class SpringApplication {

   public static void main(String[] args) {
      SpringApplication springApplication = new SpringApplication();
//      springApplication.go();
      springApplication.goCourse();
   }

   public void go() {
      //ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

      ApplicationContext context = new AnnotationConfigApplicationContext(LarkUConfig.class);

      StudentService studentService = context.getBean("studentService", StudentService.class);
      StudentService studentService2 = context.getBean("studentService", StudentService.class);

      List<Student> students = studentService.getAllStudents();
      System.out.println("All Students: " + students.size());
      students.forEach(System.out::println);
   }

   public void goCourse() {
      //ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

      ApplicationContext context = new AnnotationConfigApplicationContext(LarkUConfig.class);

      CourseService courseService = context.getBean("courseService", CourseService.class);

      List<Course> courses = courseService.getAllCourses();
      System.out.println("All Courses: " + courses.size());
      courses.forEach(System.out::println);
   }
}
