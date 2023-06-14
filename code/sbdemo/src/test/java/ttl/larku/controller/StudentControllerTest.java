package ttl.larku.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ttl.larku.domain.Student;
import ttl.larku.service.StudentService;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author whynot
 */
@ExtendWith(MockitoExtension.class)
public class StudentControllerTest {

    @InjectMocks
    private StudentController studentController;

    @Mock
    private StudentService studentService;

    @Mock
    private UriCreator uriCreator;

    @Test
    public void testCreateStudent() throws URISyntaxException {
        Student student = new Student("Joe", "282 93  939393",
                LocalDate.of(1987, 10, 10), Student.Status.FULL_TIME);

        Mockito.when(studentService.createStudent(student)).thenReturn(student);
        Mockito.when(uriCreator.getUri(student.getId()))
                .thenReturn(new URI("localhost:8080/student/" + student.getId()));

        ResponseEntity<?> result = studentController.addStudent(student);

        assertEquals(201, result.getStatusCodeValue());

        Mockito.verify(studentService).createStudent(student);
        Mockito.verify(uriCreator).getUri(student.getId());
    }
}
