package ttl.larku.service.reg.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
public class TestAppContext {

   @Autowired
   private ApplicationContext context;

   @Test
   public void testContext() {
      int stop = 0;
   }
}
