package ttl.larku.app;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ttl.larku.domain.Student;
import ttl.larku.service.StudentService;

import javax.swing.Spring;
import java.util.List;

/**
 * @author whynot
 */
public class SpringDemo {

    public static void main(String[] args) {
        SpringDemo springDemo = new SpringDemo();
        springDemo.go();
    }

    public void go() {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        StudentService ss = context.getBean("studentService", StudentService.class);

        List<Student> students = ss.getAllStudents();
        System.out.println("allStudent.size: " + students.size());
        students.forEach(System.out::println);
    }
}
