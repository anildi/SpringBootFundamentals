package ttl.larku.dao;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.transaction.annotation.Transactional;
import ttl.larku.dao.jpahibernate.JPATrackDAO;
import ttl.larku.domain.Track;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class JPATrackDAOTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private JPATrackDAO trackDAO;
    //private BaseDAO<Track> trackDAO;

    private Track track1, track2;

    /**
     * A Technique to run sql scripts just once per class.
     * This was to solve a tricky situation.  This class
     * might use a cached context if some test before it
     * used the same configuration as this one.
     * <p>
     * Whether a context gets created for this
     * depends on whether a previous test created a context
     * which got cached and is now being used here
     * If no context is created for this test,
     * no DDL scripts are run.  So this test gets whatever
     * was in put into the database by the previous test for the
     * same context.
     * Tests in this class can fail in
     * strange ways that depend on which other tests are run.
     * This trick makes sure that this test starts with the
     * data it is expecting.  The @Transactional then takes
     * care of rolling back after each test.
     * We are also using the strange fact that Spring auto wiring
     * works on this static method to inject a ScriptFileProperties
     * bean, which gets properties set with @ConfigurationProperties.
     * This allows us to use properties to decide which scripts to
     * run.  The properties are in larkUContext.properties.
     * @param dataSource
     * @throws SQLException
     */
    @BeforeAll
    public static void runSqlScriptsOnce(@Autowired DataSource dataSource,
                                         @Autowired ScriptFileProperties props) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource(props.getSchemaFile()));
            ScriptUtils.executeSqlScript(conn, new ClassPathResource(props.getDataFile()));
        }
    }

    @BeforeEach
    public void setup() {
//        System.err.println(context.getBeanDefinitionCount() + " beans");
    }


    @Test
    public void testGetAll() {
        List<Track> tracks = trackDAO.getAll();
        assertTrue(tracks.size() > 0);
    }

    @Test
    public void testCreate() {
        Track track = Track.title("What's New").artist("John Coltrane")
                .album("Ballads").duration("03:47").build();

        int newId = trackDAO.create(track).getId();

        Track result = trackDAO.get(newId);

        assertEquals(newId, result.getId());
    }

    @Test
    public void testUpdate() {
        Track track = Track.title("What's New").artist("John Coltrane")
                .album("Ballads").duration("03:47").build();
        int newId = trackDAO.create(track).getId();

        Track result = trackDAO.get(newId);

        assertEquals(newId, result.getId());

        result.setTitle("Who do");
        trackDAO.update(result);

        result = trackDAO.get(result.getId());
        assertEquals("Who do", result.getTitle());
    }

    @Test
    @Transactional
    public void testDelete() {
        Track track = Track.title("What's New").artist("John Coltrane")
                .album("Ballads").duration("03:47").build();
        int newId = trackDAO.create(track).getId();

        Track result = trackDAO.get(newId);

        assertEquals(newId, result.getId());

        int beforeSize = trackDAO.getAll().size();

        trackDAO.delete(result);

        result = trackDAO.get(result.getId());

        assertEquals(beforeSize - 1, trackDAO.getAll().size());
        assertNull(result);

    }

    @Test
    public void testsGetByTitle() {
        List<Track> result = trackDAO.getTracksByTitle("Shadow");

        System.out.println("Result:");
        result.forEach(System.out::println);

        assertEquals(1, result.size());
    }

    @Test
    public void testsGetByTitleBadWithGoodInput() {
        String goodInput = "h2-Leave It to Me";
        List<Track> result = trackDAO.getTracksByTitleInjectable(goodInput);
        System.out.println("Good Result:");
        result.forEach(System.out::println);

        assertEquals(1, result.size());
    }

    /**
     * Here we are going to use a nasty input to search
     * for everything using Injection
     */
    @Test
    public void testGetByTitleBadWithNastyInput() {

        //String badInput = "_%' or artist like '%John";
        String badInput = "h2-Leave It to Me' or title like '%_%";
//        String badInput = "h2-Leave It to Me";
        List<Track> result = trackDAO.getTracksByTitleInjectable(badInput);
        System.out.println("Bad Result :");
        result.forEach(System.out::println);

        assertEquals(6, result.size());
    }

    @Test
    public void testsGetByCriteriaWithLike() {
        Track track = Track.album("Bossa").artist("Hall")
                .title("Shadow").build();
//        Track track = Track.album("_%' or t.title like '%_").build();

        List<Track> result = trackDAO.getByCriteriaWithLike(track);

        System.out.println("Result:");
        result.forEach(System.out::println);

        assertEquals(3, result.size());
    }

    @Test
    public void testsGetByCriteriaWithEqualWithGoodInput() {
        Track track = Track.title("h2-Leave It to Me").build();
//        Track track = Track.title("h2-Leave It to Me' or title like '%_%").build();
//        Track track = Track.album("_%' or t.title like '%_").build();

        List<Track> result = trackDAO.getByCriteriaWithEquals(track);

        System.out.println("Result:");
        result.forEach(System.out::println);

        assertEquals(1, result.size());
    }

    @Test
    public void testsGetByCriteriaWithEqualInjectionDoesNotWorkWith() {
//        Track track = Track.title("h2-Leave It to Me").build();
        Track track = Track.title("h2-Leave It to Me' or title like '%_%").build();
//        Track track = Track.album("_%' or t.title like '%_").build();

        List<Track> result = trackDAO.getByCriteriaWithEquals(track);

        System.out.println("Result:");
        result.forEach(System.out::println);

        //Nothing found
        assertEquals(0, result.size());
    }


    @Test
    public void testsGetByExampleBad() {
        //Track track = Track.album("Bossa%' or '1'='1' '")
        Track track = Track.title("h2-Leave It to Me").build();

        List<Track> result = trackDAO.getByExampleBadWithEqual(track);

        System.out.println("Result:");
        result.forEach(System.out::println);

        assertEquals(1, result.size());
    }

    /**
     * A simple example of SQL injection.  Our intent with the
     * in our DAO method is to only allow you to search for
     * a specific title.  The input below will return all Tracks.
     */
    @Test
    public void testsGetByExampleBadWithNastyInput() {
        Track track = Track.title("h2-Leave It to Me' or title like '%_%").build();

        List<Track> result = trackDAO.getByExampleBadWithEqual(track);

        System.out.println("Result:");
        result.forEach(System.out::println);

        assertEquals(6, result.size());
    }
}
