package ttl.larku.dao;

import org.junit.jupiter.api.Test;
import ttl.larku.dao.inmemory.StudentDAO;
import ttl.larku.domain.Student;

import static org.junit.jupiter.api.Assertions.assertSame;


public class DAOFactoryTest {

   @Test
   public void testFactoryCreatesSingletons() {
      StudentDAO dao1 = DAOFactory.studentDAO();
      StudentDAO dao2 = DAOFactory.studentDAO();

//      assertTrue(dao1 == dao2);
      assertSame(dao1, dao2);
   }
}
