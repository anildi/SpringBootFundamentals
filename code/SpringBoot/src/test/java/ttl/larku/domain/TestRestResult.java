package ttl.larku.domain;

import org.junit.jupiter.api.Test;
import ttl.larku.controllers.rest.RestResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author whynot
 */
public class TestRestResult {

    @Test
    public void testOkResult() {
        var input = "This is good";
        RestResult rr = RestResult.ofValue(input);

        assertEquals(RestResult.Status.Ok, rr.getStatus());

        String result = rr.getEntity(String.class);
        assertEquals(input, result);
    }

    @Test
    public void testNotOkResult() {
        var err1 = "This is bad";
        var err2 = "Really really bad";
        var err3 = "OMG!";

        RestResult rr = RestResult.ofError(err1, err2, err3);

        assertEquals(RestResult.Status.Error, rr.getStatus());

        assertEquals(3, rr.getErrors().size());

        assertThrows(UnsupportedOperationException.class, () -> {
            String result = rr.getEntity(String.class);
            System.out.println("result: " + result);
        });
    }
}
