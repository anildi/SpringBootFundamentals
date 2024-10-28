package ttl.larku.app;

import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ttl.larku.domain.Student;
import ttl.larku.jconfig.LarkUConfig;
import ttl.larku.service.StudentService;

public class SpringApplication {

   public static void main(String[] args) {
      SpringApplication springApplication = new SpringApplication();
      springApplication.go();
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
}
