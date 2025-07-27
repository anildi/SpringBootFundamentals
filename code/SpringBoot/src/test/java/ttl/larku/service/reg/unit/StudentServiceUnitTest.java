package ttl.larku.service.reg.unit;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationEventPublisher;
import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Student;
import ttl.larku.domain.Student.Status;
import ttl.larku.service.StudentService;
import ttl.larku.service.props.ServiceThatWeDontOwn;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


/**
 * A straight ahead Unit test.  Only Mockito, no Spring
 */
//@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
//Can use @MockitoSettings to turn on LENIENT mode.
//Then Mockito won't get upset with unused Mocks.
//Probably better to leave it off and get rid of
//unused mocks.
@MockitoSettings(strictness = Strictness.LENIENT)
@Tag("unit")
public class StudentServiceUnitTest {

    private String name1 = "Bloke";
    private String name2 = "Blokess";
    private String newName = "Karl Jung";
    private String phoneNumber1 = "290 298 4790";
    private String phoneNumber2 = "3838 939 93939";

    @Mock
    private BaseDAO<Student> studentDAO;

    @Mock
    private ServiceThatWeDontOwn ctwdo;

    @Mock
    private List<Student> mockList;

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private StudentService studentService;

    @BeforeEach
    public void setup() {
        studentService.clear();
    }

    @Test
    public void testCreateStudent() {
        Student s = new Student(name1, phoneNumber1, Status.FULL_TIME);

        Mockito.when(studentDAO.insert(s)).thenReturn(s);
        Mockito.doNothing().when(publisher).publishEvent(any());

        Student newStudent = studentService.createStudent(name1, phoneNumber1, Status.FULL_TIME);

        Mockito.verify(studentDAO, atMost(1)).insert(s);
        Mockito.verify(publisher).publishEvent(any());
    }

    @Test
    public void testDeleteStudent() {
        Student student1 = new Student(name1, phoneNumber1, Status.FULL_TIME);
        student1.setId(1);

        //Set up Mocks
        Mockito.when(studentDAO.delete(student1.getId())).thenReturn(true);

        //Call and JUnit asserts
        boolean result = studentService.deleteStudent(student1.getId());
        assertTrue(result);

        //Mockito verification
        Mockito.verify(studentDAO).delete(student1.getId());
    }

    @Test
    public void testDeleteNonExistentStudent() {

        //Non existent Id
        boolean result = studentService.deleteStudent(9999);
        assertFalse(result);

        Mockito.verify(studentDAO).delete(9999);
    }

    @Test
    public void testUpdateStudent() {
        Student student1 = new Student(name1, phoneNumber1, Status.FULL_TIME);
        student1.setId(1);

        //Set up Mocks
//        Mockito.when(studentDAO.findById(1)).thenReturn(student1);
        Mockito.when(studentDAO.update(student1)).thenReturn(true);

        //Call and Junit assertions
        boolean result = studentService.updateStudent(student1);
        assertTrue(result);

        //Mockito Verification
        Mockito.verify(studentDAO, atMostOnce()).update(student1);
    }

    @Test
    public void testUpdateNonExistentStudent() {
        Student student1 = new Student(name1, phoneNumber1, Status.FULL_TIME);
        student1.setId(9999);

        boolean result = studentService.updateStudent(student1);
        assertFalse(result);

        Mockito.verify(studentDAO).update(student1);
    }

    @Test
    public void testGetByName() {
        Mockito.when(studentDAO.findBy(any())).thenReturn(mockList);

        List<Student> johnnies = studentService.getByName("Johny");

        Mockito.verify(studentDAO).findBy(any());
    }
}
