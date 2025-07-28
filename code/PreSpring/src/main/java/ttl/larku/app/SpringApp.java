package ttl.larku.app;

import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ttl.larku.domain.Course;
import ttl.larku.domain.Student;
import ttl.larku.jconfig.LarkUConfig;
import ttl.larku.service.CourseService;
import ttl.larku.service.StudentService;

public class SpringApp {

   public static void main(String[] args) {
      SpringApp sa = new SpringApp();
//      sa.go();
      sa.goCourse();
   }

   public void go() {
//      ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
      ApplicationContext context = new AnnotationConfigApplicationContext(LarkUConfig.class);

      StudentService ss = context.getBean("studentService", StudentService.class);
      StudentService ss2 = context.getBean("studentService", StudentService.class);

      List<Student> students = ss.getAllStudents();

      System.out.println("students: " + students);
   }

   public void goCourse() {
//      ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
      ApplicationContext context = new AnnotationConfigApplicationContext(LarkUConfig.class);

      CourseService ss = context.getBean("courseService", CourseService.class);

      List<Course> result = ss.getAllCourses();

      System.out.println("courses: " + result);
   }

}
