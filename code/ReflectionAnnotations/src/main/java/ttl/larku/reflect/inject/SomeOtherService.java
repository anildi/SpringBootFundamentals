package ttl.larku.reflect.inject;

import java.util.List;
import ttl.larku.domain.Student;
import ttl.larku.service.StudentService;

/**
 * @author whynot
 */
public class SomeOtherService {

    @MyInject
    private StudentService studentService;

    public void doStuff() {
        List<Student> students = studentService.getAllStudents();
        System.out.println("students: ");
        students.forEach(System.out::println);
    }

    @Override
    public String toString() {
        return "SomeController{" +
              "studentService=" + studentService +
              '}';
    }
}
