package ttl.larku.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import ttl.larku.domain.Course;
import ttl.larku.domain.ScheduledClass;
import ttl.larku.domain.Student;

public class TestObjectMapper {
   private ObjectMapper mapper = new ObjectMapper();

   @Test
   public void testSerializeStudent() throws JsonProcessingException {
      Course course = new Course("Blah", "Intro to Blah");
      ScheduledClass sc = new ScheduledClass(course, LocalDate.of(2000, 10, 10), LocalDate.of(2001, 10, 10));
      Student student = new Student("Joe", "3 9 93939 3", LocalDate.of(1985, 4, 5), Student.Status.FULL_TIME);
      student.addClass(sc);
      sc.addStudent(student);

      var jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(student);

      System.out.println(jsonString);
   }
}
