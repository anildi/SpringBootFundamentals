package ttl.larku.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import ttl.larku.domain.Track;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
//This will start and *roll back* a Transaction before/after each test.
//Easy way to get DB to initial state for each test.  Initial state
//will come from schema.sql and data.sql when the context is created.
@Transactional
public class TrackRepoServiceTest {

    @Autowired
    ApplicationContext context;
    @Autowired
    private TrackRepoService trackService;

    @BeforeEach
    public void setup() {
//        trackService.clear();
    }

    @Test
    public void testCreateTrack() {
        Track newTrack = trackService.createTrack("You Stepped Out Of A Dream", "Herb Ellis", "Three guitars in Bossa Nova Time",
                "04:19", "1963-02-03");
        newTrack = trackService.createTrack(newTrack);

        Track result = trackService.getTrack(newTrack.getId());

        assertTrue(result.getTitle().contains(newTrack.getTitle()));
        assertEquals(7, trackService.getAllTracks().size());

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

        assertEquals(8, trackService.getAllTracks().size());

        boolean done = trackService.deleteTrack(track1.getId());
        assertTrue(done);

        assertEquals(7, trackService.getAllTracks().size());
    }

    @Test
    public void testDeleteNonExistentTrack() {
        Track track1 = Track.title("The Shadow Of Your Smile").artist("Big John Patton")
                .album("Let 'em Roll").duration("06:15").date("1965-03-04").build();
        track1 = trackService.createTrack(track1);
        Track track2 = Track.title("I'll Remember April").artist("Jim Hall and Ron Carter")
                .album("Alone Together").duration("05:54").date("1972-03-04").build();
        track2 = trackService.createTrack(track2);

        assertEquals(8, trackService.getAllTracks().size());

        boolean done = trackService.deleteTrack(9999);
        assertFalse(done);

        assertEquals(8, trackService.getAllTracks().size());

    }

    @Test
    public void testUpdateTrack() {
        Track track1 = Track.title("The Shadow Of Your Smile").artist("Big John Patton")
                .album("Let 'em Roll").duration("06:15").date("1965-03-04").build();

        track1 = trackService.createTrack(track1);

        assertEquals(7, trackService.getAllTracks().size());

        track1.setTitle("A Shadowy Smile");
        boolean done = trackService.updateTrack(track1);
        assertTrue(done);

        assertEquals(7, trackService.getAllTracks().size());
    }

    @Test
    public void testUpdateNonExistentTrack() {
        Track track1 = Track.title("The Shadow Of Your Smile").artist("Big John Patton")
                .album("Let 'em Roll").duration("06:15").date("1965-03-04").build();

        track1.setId(99999);

        boolean done = trackService.updateTrack(track1);
        assertFalse(done);

        assertEquals(6, trackService.getAllTracks().size());
    }

    @Test
    public void testGetTrackByTitle() {
        List<Track> tracks = trackService.getTracksByTitle("Shadow");

        System.out.println("Tracks");
        tracks.forEach(System.out::println);

        assertEquals(1, tracks.size());
    }
}
