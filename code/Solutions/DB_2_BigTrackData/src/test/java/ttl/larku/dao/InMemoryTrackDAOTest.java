package ttl.larku.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ttl.larku.dao.inmemory.InMemoryTrackDAO;
import ttl.larku.domain.Track;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class InMemoryTrackDAOTest {


    private InMemoryTrackDAO dao;

    private Track track1, track2;

    @BeforeEach
    public void setup() {
        dao = new InMemoryTrackDAO();
        dao.createStore();

        track1 = Track.title("The Shadow Of Your Smile").artist("Big John Patton")
                .album("Let 'em Roll").duration("06:15").date("1965-01-02").build();
        track2 = Track.title("I'll Remember April").artist("Jim Hall and Ron Carter")
                .album("Alone Together").duration("05:54").date("1972-01-02").build();

        dao.create(track1);
        dao.create(track2);
    }


    @Test
    public void testGetAll() {
        List<Track> customers = dao.getAll();
        assertEquals(2, customers.size());
    }

    @Test
    public void testCreate() {
        Track track = Track.title("What's New").artist("John Coltrane")
                .album("Ballads").duration("03:47").build();

        int newId = dao.create(track).getId();

        Track result = dao.get(newId);

        assertEquals(newId, result.getId());
    }

    @Test
    public void testUpdate() {
        Track track = Track.title("What's New").artist("John Coltrane")
                .album("Ballads").duration("03:47").build();
        int newId = dao.create(track).getId();

        Track result = dao.get(newId);

        assertEquals(newId, result.getId());

        result.setTitle("Who do");
        boolean done = dao.update(result);
        assertTrue(done);

        result = dao.get(result.getId());
        assertEquals("Who do", result.getTitle());
    }

    @Test
    public void testDelete() {
        Track track = Track.title("What's New").artist("John Coltrane")
                .album("Ballads").duration("03:47").build();
        int newId = dao.create(track).getId();

        Track result = dao.get(newId);

        assertEquals(newId, result.getId());

        int beforeSize = dao.getAll().size();

        boolean done = dao.delete(result);
        assertTrue(done);

        result = dao.get(result.getId());

        assertEquals(beforeSize - 1, dao.getAll().size());
        assertNull(result);

    }

    @Test
    public void testGetTrackByExample() {
        Track example = Track.title("April").album("Roll").build();

        List<Track> result = dao.getByExample(example);

        System.out.println("result size: " + result.size());
        result.forEach(System.out::println);

        assertEquals(2, result.size());
    }

    @Test
    public void testGetTrackByExampleForZeroResult() {
        Track example = Track.title("XXX").album("NotThere").build();

        List<Track> result = dao.getByExample(example);

        System.out.println("result size: " + result.size());
        result.forEach(System.out::println);

        assertEquals(0, result.size());
    }
}
