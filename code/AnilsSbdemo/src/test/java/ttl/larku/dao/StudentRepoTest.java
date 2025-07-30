package ttl.larku.dao;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ttl.larku.dao.repository.StudentRepo;
import ttl.larku.domain.Student;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class StudentRepoTest {

   @Autowired
   private StudentRepo studentRepo;

   @Test
   public void testGetAllStudents() {
      //List<Student> result = studentRepo.findAll();
      //List<Student> result = studentRepo.findByName("Manoj-h2");
      List<Student> result = studentRepo.findByNameContainingIgnoreCase("manoj");

      System.out.println("result: " + result);

//      assertEquals(4, result.size());
   }
}
