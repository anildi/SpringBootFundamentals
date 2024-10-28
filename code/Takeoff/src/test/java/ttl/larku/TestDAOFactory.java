package ttl.larku;

import org.junit.jupiter.api.Test;
import ttl.larku.dao.DAOFactory;
import ttl.larku.dao.StudentDAO;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

public class TestDAOFactory {

   @Test
   public void testDAOFactory() {
      StudentDAO dao1 = DAOFactory.studentDAO();
      StudentDAO dao2 = DAOFactory.studentDAO();

      assertSame(dao1, dao2);
   }
}
