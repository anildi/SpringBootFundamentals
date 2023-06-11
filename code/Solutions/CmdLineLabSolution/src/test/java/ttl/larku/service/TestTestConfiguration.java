package ttl.larku.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ttl.larku.dao.BaseDAO;
import ttl.larku.dao.inmemory.InMemoryTrackDAO;
import ttl.larku.domain.Track;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(SpringExtension.class)
/**
 * Alternative to the approaches shown in TrackServiceTest.
 * Here, we create an @TestConfiguration static inner class,
 * with just the beans we need for the test.  This configuration
 * will be used in addition to any other configuration you
 * provide (none, in this case).  Also creates a context
 * with 10 beans.
 *
 * This technique can also be useful to override a particular
 * bean from the main configuration, just for this test.
 * To do this, you load the main configuration, e.g. with
 * @SpringBootTest(classes = {TrackerConfig.class})
 * and then create an overriding bean definition in this class.
 * You *have* to give your @Bean method a different name from the
 * original, (or you have to set the spring.main.allow-bean-defintion-overriding property)
 * and You *have* to mark your bean as @Primary to avoid
 * injection errors.
 */
//@SpringBootTest //(classes = {TrackerConfig.class, TestTestConfiguration.TestBeanProducer.class}, properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(properties = {"spring.main.allow-bean-definition-overriding=true"})
public class TestTestConfiguration {

    @TestConfiguration
    public static class TestBeanProducer {
        @Bean
        //@Primary will make this bean override any
        //other beans of the same type for injection
        //purposes.
        @Primary
        public BaseDAO<Track> trackDAO() {
            BaseDAO<Track> bd = new InMemoryTrackDAO();
            bd.create(Track.title("Who hoo").build());

            return bd;
        }

//        @Bean TrackService trackService() {
//            return new TrackService(trackDAO());
//        }
    }

    @Autowired
    private TrackService trackService;

    @Autowired
    ApplicationContext context;

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

        System.out.println("All tracks:");
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
