package ttl.larku.app;

import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ttl.larku.domain.Course;
import ttl.larku.domain.Student;
import ttl.larku.service.CourseService;
import ttl.larku.service.StudentService;

public class RegistrationApp {

   public static void main(String[] args) {
      //Setting a profile on an Application Context.
      AnnotationConfigApplicationContext appContext = new AnnotationConfigApplicationContext();
      appContext.getEnvironment().setActiveProfiles("development");
      appContext.scan("ttl.larku");
      appContext.refresh();

      basic(appContext);
   }

   public static void basic(ApplicationContext appContext) {

      StudentService ss = appContext.getBean("studentService", StudentService.class);

      StudentService ss2 = appContext.getBean("studentService", StudentService.class);

      List<Student> students = ss.getAllStudents();
      System.out.println("student: " + students.size());
      students.forEach(System.out::println);

      CourseService cs = appContext.getBean("courseService", CourseService.class);

      List<Course> courses = cs.getAllCourses();
      System.out.println("courses: " + courses.size());
      courses.forEach(System.out::println);

   }

   public static void initStudents(StudentService ss) {
      ss.createStudent("Manoj", "282 939 9944", Student.Status.FULL_TIME);
      ss.createStudent("Charlene", "282 898 2145", Student.Status.FULL_TIME);
      ss.createStudent("Firoze", "228 678 8765", Student.Status.HIBERNATING);
      ss.createStudent("Joe", "3838 678 3838", Student.Status.PART_TIME);
   }

   public static void initCourses(CourseService cs) {
      cs.createCourse("MATH-101", "Intro To Math");
      cs.createCourse("PHY-101", "Intro To Physics");
      cs.createCourse("PHY-102", "Yet more Physics");
   }

}
