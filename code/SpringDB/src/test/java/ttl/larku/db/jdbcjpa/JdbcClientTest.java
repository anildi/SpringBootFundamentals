package ttl.larku.db.jdbcjpa;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import ttl.larku.domain.Student;

@SpringBootTest
public class JdbcClientTest {

   @Autowired
   private JdbcClient jdbcClient;

   @Test
   public void testGetAllStudentsWithJDBC() {
      var result = jdbcClient.sql("select * from Student")
            .query(Student.class)
            .list();

      System.out.println("result: " + result);
   }

}
