package ttl.larku.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import ttl.larku.dao.inmemory.InMemoryTrackDAO;
import ttl.larku.domain.Track;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


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
    ApplicationContext context;

    @Autowired
    private TrackService trackService;

    @BeforeEach
    public void setup() {
        trackService.clear();
    }

    @Test
    public void testCreateTrack() {
        Track newTrack = trackService.createTrack("You Stepped Out Of A Dream", "Herb Ellis", "Three guitars in Bossa Nova Time",
                "04:19", "1963-02-03");
        newTrack = trackService.createTrack(newTrack);

        Track result = trackService.getTrack(newTrack.getId());

        assertTrue(result.getTitle().contains(newTrack.getTitle()));
        assertEquals(1, trackService.getAllTracks().size());

        System.out.println("All tracks: ");
        trackService.getAllTracks().forEach(System.out::println);
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

        boolean done = trackService.deleteTrack(track1.getId());
        assertTrue(done);

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

        boolean done = trackService.deleteTrack(9999);
        assertFalse(done);

        assertEquals(2, trackService.getAllTracks().size());

    }

    @Test
    public void testUpdateTrack() {
        Track track1 = Track.title("The Shadow Of Your Smile").artist("Big John Patton")
                .album("Let 'em Roll").duration("06:15").date("1965-03-04").build();

        track1 = trackService.createTrack(track1);

        assertEquals(1, trackService.getAllTracks().size());

        track1.setTitle("A Shadowy Smile");
        boolean done = trackService.updateTrack(track1);
        assertTrue(done);

        assertEquals(1, trackService.getAllTracks().size());
        assertTrue(trackService.getAllTracks().get(0).getTitle().contains("Shadowy"));
    }

    @Test
    public void testUpdateNonExistentTrack() {
        Track track1 = Track.title("The Shadow Of Your Smile").artist("Big John Patton")
                .album("Let 'em Roll").duration("06:15").date("1965-03-04").build();

        track1 = trackService.createTrack(track1);


        assertEquals(1, trackService.getAllTracks().size());

        track1.setTitle("A Shadowy Smile");
        track1.setId(9999);

        boolean done = trackService.updateTrack(track1);
        assertFalse(done);

        assertEquals(1, trackService.getAllTracks().size());
    }

    @Test
    public void testGetTracksByTitle() {
        Track track1 = Track.title("The Shadow Of Your Smile").artist("Big John Patton")
                .album("Let 'em Roll").duration("06:15").date("1965-03-04").build();
        track1 = trackService.createTrack(track1);
        Track track2 = Track.title("I'll Remember April").artist("Jim Hall and Ron Carter")
                .album("Alone Together").duration("05:54").date("1972-03-04").build();
        track2 = trackService.createTrack(track2);

        String titleToSearch = "Shadow";
        List<Track> result = trackService.getTracksByTitle(titleToSearch);

        assertEquals(1, result.size());
        assertEquals("Big John Patton", result.get(0).getArtist());
    }

    @Test
    public void testGetTracksByMap() {
        Track track1 = Track.title("The Shadow Of Your Smile").artist("Big John Patton")
                .album("Let 'em Roll").duration("06:15").date("1965-03-04").build();
        track1 = trackService.createTrack(track1);
        Track track2 = Track.title("I'll Remember April").artist("Jim Hall and Ron Carter")
                .album("Alone Together").duration("05:54").date("1972-03-04").build();
        track2 = trackService.createTrack(track2);

        Track track3 = trackService.createTrack(Track.title("My Funny Valentine").artist("Johnny Smith")
                .album("Moonlight in Vermont").duration("02:48").build());

        Map<String, String> propsToSearchFor = Map.of("title", "Shadow", "album", "Alone");
        List<Track> result = trackService.getTracksBy(propsToSearchFor);

        assertEquals(2, result.size());
    }

    @Test
    public void testGetTracksByExample() {
        Track track1 = Track.title("The Shadow Of Your Smile").artist("Big John Patton")
                .album("Let 'em Roll").duration("06:15").date("1965-03-04").build();
        track1 = trackService.createTrack(track1);
        Track track2 = Track.title("I'll Remember April").artist("Jim Hall and Ron Carter")
                .album("Alone Together").duration("05:54").date("1972-03-04").build();
        track2 = trackService.createTrack(track2);

        Track track3 = trackService.createTrack(Track.title("My Funny Valentine").artist("Johnny Smith")
                .album("Moonlight in Vermont").duration("02:48").build());

        //Map<String, String> propsToSearchFor = Map.of("title", "Shadow", "album", "Alone");

        Track example = Track.title("Shadow").album("Alone").build();
        List<Track> result = trackService.getTracksBy(example);

        assertEquals(2, result.size());
    }
}
