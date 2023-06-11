package ttl.larku.domain;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

public class TrackBuilderTest {

    @Test
    public void testBuilder() {
        Track track1 = Track.title("I'll Remember April").artist("Jim Hall and Ron Carter")
                .album("Alone Together").duration("05:54").date("1972-03-04").build();
        Track track2 = Track.title("What's New").artist("John Coltrane")
                .album("Ballads").duration("03:47").build();


        String date2 = track2.getDate();

        assertNull(date2);

        System.out.println("track1: " + track1 + ", track2: " + track2);
    }

}
