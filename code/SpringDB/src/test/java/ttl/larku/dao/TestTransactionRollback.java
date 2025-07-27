package ttl.larku.dao;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ttl.larku.domain.Student;
import ttl.larku.service.StudentService;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class TestTransactionRollback {

   @Autowired
   private StudentService studentService;

   static class MyException extends Exception{

   }

//   @Test
   @Transactional
   @Rollback(false)
   public void testTransactionRollsBackWithRuntimeException() throws MyException {
      Student student = new Student("Joe", "383 9339", LocalDate.of(2000, 10, 10), Student.Status.PART_TIME);

      Student newStudent = studentService.createStudent(student);

      assertTrue(newStudent.getId() > 0);

//      throw new MyException();
      throw new RuntimeException();
   }
}
