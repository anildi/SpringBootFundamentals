package ttl.larku.reflect.inject;

import ttl.larku.domain.Student;
import ttl.larku.service.StudentService;

import java.util.List;

/**
 * @author whynot
 */
public class SomeController {

    @MyInject
    private StudentService studentService;

    public void doStuff() {
        List<Student> students = studentService.getAllStudents();
        System.out.println("students: ");
        students.forEach(System.out::println);
    }
}
