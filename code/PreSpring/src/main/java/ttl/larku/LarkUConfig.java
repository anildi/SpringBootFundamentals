//package ttl.larku.jconfig;
package ttl.larku;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ttl.larku.dao.BaseDAO;
import ttl.larku.dao.inmemory.InMemoryStudentDAO;
import ttl.larku.domain.Student;
import ttl.larku.service.StudentService;

@Configuration
//@ComponentScan({"ttl.larku.service", "ttl.larku.dao"})
@ComponentScan
public class LarkUConfig {

   @Bean
   public BaseDAO<Student> studentDAO() {
      var studentDAO = new InMemoryStudentDAO();
      return studentDAO;
   }

   @Bean
   public StudentService studentService() {
      StudentService studentService = new StudentService();

      var studentDAO = studentDAO();

      studentService.setStudentDAO(studentDAO);

      return studentService;
   }
}
