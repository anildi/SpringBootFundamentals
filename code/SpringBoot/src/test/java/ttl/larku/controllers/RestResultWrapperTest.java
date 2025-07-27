package ttl.larku.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import ttl.larku.controllers.rest.RestResultWrapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RestResultWrapperTest {

   @Test
   public void testRestResultWrapperWithProblemDetails() {
      var pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY,
            "Bad Bad Bad Stuff");

      var rr = RestResultWrapper.ofError(pd);

      assertEquals(RestResultWrapper.Status.Error, rr.getStatus());

      System.out.println("rr: " + rr);
   }
}
