package ttl.larku.service;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ttl.larku.domain.Student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = LarkUConfig.class)
@SpringBootTest
public class StudentServiceTest {

    private String name1 = "Bloke";
    private String name2 = "Blokess";
    private String newName = "Karl Jung";
    private String phoneNumber1 = "290 298 4790";
    private String phoneNumber2 = "3838 939 93939";

    @Autowired
    private StudentService studentService;

    @BeforeEach
    public void setup() {
        //studentService = new StudentService();
        //studentService = applicationContext.getBean("studentService", StudentService.class);
        studentService.clear();
    }

    @Test
    public void testCreateStudent() {
        Student newStudent = studentService.createStudent(name1, phoneNumber1, Student.Status.FULL_TIME);

        Optional<Student> opt = studentService.getStudent(newStudent.getId());
        assertTrue(opt.isPresent());
        Student result = opt.get();

        assertTrue(result.getName().contains(name1));
        assertEquals(1, studentService.getAllStudents().size());
    }

    @Test
    public void testDeleteStudent() {
        Student student1 = studentService.createStudent(name1, phoneNumber1, Student.Status.FULL_TIME);
        Student student2 = new Student(name1, phoneNumber1, Student.Status.FULL_TIME);
        student2 = studentService.createStudent(student2);

        assertEquals(2, studentService.getAllStudents().size());

        boolean result = studentService.deleteStudent(student1.getId());
        assertTrue(result);

        assertEquals(1, studentService.getAllStudents().size());
        assertTrue(studentService.getAllStudents().get(0).getName().contains(name1));
    }

    @Test
    public void testDeleteNonExistentStudent() {
        Student student1 = studentService.createStudent(name1, phoneNumber1, Student.Status.FULL_TIME);
        Student student2 = new Student(name1, phoneNumber1, Student.Status.FULL_TIME);
        student2 = studentService.createStudent(student2);

        assertEquals(2, studentService.getAllStudents().size());

        //Non existent Id
        studentService.deleteStudent(9999);

        assertEquals(2, studentService.getAllStudents().size());
    }

    @Test
    public void testUpdateStudent() {
        Student student1 = studentService.createStudent(name1, phoneNumber1, Student.Status.FULL_TIME);

        assertEquals(1, studentService.getAllStudents().size());

        student1.setName(name2);
        studentService.updateStudent(student1);

        assertEquals(1, studentService.getAllStudents().size());
        assertTrue(studentService.getAllStudents().get(0).getName().contains(name2));
    }
}
