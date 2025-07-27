package ttl.larku.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ttl.larku.dao.repository.CourseRepo;
import ttl.larku.domain.Course;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class CourseRepoTest {

   @Autowired
   private CourseRepo courseRepo;

   @Test
   public void testNamedQueryGetByCode() {
      Course course = courseRepo.getByCode("MATH-101").orElse(null);

      System.out.println("course: " + course);
      assertNotNull(course);
   }
}
