package ttl.larku.play;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * @author whynot
 */
public class TestLocalDate {

    @Test
    public void testLocalDate() {
        LocalDate then = LocalDate.parse("2000-10-10");
        long numYears = then.until(LocalDate.now(), ChronoUnit.YEARS);
        System.out.println("numYears: " + numYears);
    }
}
