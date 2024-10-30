package ttl.larku.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import net.bytebuddy.agent.VirtualMachine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ttl.larku.domain.Student;
import ttl.larku.service.StudentService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class StudentControllerUnitTest {

   @Mock
   private StudentService studentService;

   @Mock
   private URICreator uriCreator;

   @InjectMocks
   private StudentController studentController;

   List<Student> fakeStudents = List.of(
         new Student("Sam", "383 93", Student.Status.FULL_TIME),
         new Student("Manoj", "383 93", Student.Status.FULL_TIME)

   );

   @Test
   public void testGetAllStudents() {
      Mockito.when(studentService.getAllStudents()).thenReturn(fakeStudents);

      List<Student> students = studentController.getStudents();
      assertEquals(2, students.size());

      Mockito.verify(studentService).getAllStudents();
   }

   @Test
   public void testAddStudent() throws URISyntaxException {
      Student s = fakeStudents.get(0);
      Mockito.when(studentService.createStudent(s))
            .thenReturn(s);

      URI newStudentURI = new URI("http://localhost:8080/0");

      Mockito.when(uriCreator.getURI(0)).thenReturn(newStudentURI);

      ResponseEntity<Student> re = studentController.insertStudent(s);

      assertEquals(HttpStatus.CREATED, re.getStatusCode());

      Mockito.verify(studentService).createStudent(s);
   }
}
