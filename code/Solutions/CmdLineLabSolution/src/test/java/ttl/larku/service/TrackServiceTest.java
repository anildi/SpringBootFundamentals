package ttl.larku.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ttl.larku.dao.inmemory.InMemoryTrackDAO;
import ttl.larku.domain.Track;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(SpringExtension.class)
//Different ways of creating the Context.  From Most Expensive to
// Not So Expensive.
/**********************
 //This is the most expensive way to make the context.  The entire
 //Spring Context will be created. About 104 beans, at this writing.
 **********************/
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)

/**********************
 //This next approach will just create the beans in the given
 //configuration classes. //About 13 beans at this writing
 ***********************/
//@SpringBootTest(classes = {TrackerConfig.class})

/*****************************
 * Finally, we can simply pull in the exact classes we need
 * for the test.  This will still pull in some other beans.
 * About 10 beans at this writing.
 **********/
@SpringBootTest(classes = {TrackService.class, InMemoryTrackDAO.class})
public class TrackServiceTest {

    @Autowired
    private TrackService trackService;

    @BeforeEach
    public void setup() {
        trackService.clear();
    }

    @Test
    public void testCreateTrack() {
        Track newTrack = trackService.createTrack("You Stepped Out Of A Dream", "Herb Ellis", "Three guitars in Bossa Nova Time",
                "04:19", "1963");
        newTrack = trackService.createTrack(newTrack);

        Track result = trackService.getTrack(newTrack.getId());

        assertTrue(result.getTitle().contains(newTrack.getTitle()));
        assertEquals(1, trackService.getAllTracks().size());
    }

    @Test
    public void testDeleteTrack() {
        Track track1 = Track.title("The Shadow Of Your Smile").artist("Big John Patton")
                .album("Let 'em Roll").duration("06:15").date("1965-03-04").build();
        track1 = trackService.createTrack(track1);
        Track track2 = Track.title("I'll Remember April").artist("Jim Hall and Ron Carter")
                .album("Alone Together").duration("05:54").date("1972-03-04").build();
        track2 = trackService.createTrack(track2);

        assertEquals(2, trackService.getAllTracks().size());

        trackService.deleteTrack(track1.getId());

        assertEquals(1, trackService.getAllTracks().size());
        assertTrue(trackService.getAllTracks().get(0).getTitle().contains("April"));
    }

    @Test
    public void testDeleteNonExistentTrack() {
        Track track1 = Track.title("The Shadow Of Your Smile").artist("Big John Patton")
                .album("Let 'em Roll").duration("06:15").date("1965-03-04").build();
        track1 = trackService.createTrack(track1);
        Track track2 = Track.title("I'll Remember April").artist("Jim Hall and Ron Carter")
                .album("Alone Together").duration("05:54").date("1972-03-04").build();
        track2 = trackService.createTrack(track2);

        assertEquals(2, trackService.getAllTracks().size());

        trackService.deleteTrack(9999);

        assertEquals(2, trackService.getAllTracks().size());
    }

    @Test
    public void testUpdateTrack() {
        Track track1 = Track.title("The Shadow Of Your Smile").artist("Big John Patton")
                .album("Let 'em Roll").duration("06:15").date("1965-03-04").build();

        track1 = trackService.createTrack(track1);

        assertEquals(1, trackService.getAllTracks().size());

        track1.setTitle("A Shadowy Smile");
        trackService.updateTrack(track1);

        assertEquals(1, trackService.getAllTracks().size());
        assertTrue(trackService.getAllTracks().get(0).getTitle().contains("Shadowy"));
    }
}
