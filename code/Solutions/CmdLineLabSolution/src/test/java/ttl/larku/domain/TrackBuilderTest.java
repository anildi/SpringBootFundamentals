package ttl.larku.domain;


import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNull;

public class TrackBuilderTest {

    @Test
    public void testBuilder() {
        Track track1 = Track.title("I'll Remember April").artist("Jim Hall and Ron Carter")
                .album("Alone Together").duration("PT05M54S").date("1972-03-04").build();
        Track track2 = Track.title("What's New").artist("John Coltrane")
                .album("Ballads").duration("PT3M47S").build();


        LocalDate date2 = track2.getDate();

        assertNull(date2);

        System.out.println("track1: " + track1 + ", track2: " + track2);
    }

}
