package ttl.larku.dao;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ttl.larku.dao.repository.CustomRepo;
import ttl.larku.domain.Course;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
//Populate your DB.  From Most Expensive to least expensive

//This will make recreate the context after every test.
//In conjunction with appropriate 'schema[-XXX].sql' and 'data[-XXX].sql' files
//it will also drop and recreate the DB before each test.
//@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)

//Or you can just re-run the sql files before each test method
@Sql(scripts = { "/schema-h2.sql", "/data-h2.sql" }, executionPhase= Sql.ExecutionPhase.BEFORE_TEST_METHOD)

//This next one will roll back the transaction after
//each test, so the database will actually stay the
//same for the next test.
//@Transactional
@Tag("dao")
public class CustomRepoTest {

    @Autowired
    private CustomRepo customRepo;

    @Test
    public void testFindAll() {
        List<Course> courses = customRepo.findAll();

        assertEquals(3, courses.size());
    }

    @Test
    public void testOptional() {
        Optional<Course> oCourse = customRepo.findById(1);


        assertNotNull(oCourse.orElse(null));

        //test for null optional
        oCourse = customRepo.findById(1000);

        assertNull(oCourse.orElse(null));
    }

    /**
     * The is testing the presence of @Nullable in the interface.
     * If a Null is returned on an @Nullable call, then the
     * null is returned.  If the call is not annotated with @Nullable,
     * an EmptyAccessException should be thrown.  See the next
     * Test.
     *
     * For this test to succeed a package-info.java file has to exist
     * with the following lines in it
     *
     * @org.springframework.lang.NonNullApi package ttl.larku.dao.repository;
     *
     * This is what kicks in Spring Data's null checking mechanism.
     */
    @Test
    public void testForNullReturn() {
        Course course = customRepo.findNullableById(1);
        assertNotNull(course);

        course = customRepo.findNullableById(1000);
        assertNull(course);
    }

    /**
     * The is testing the absence of @Nullable in the interface.
     * If a Null is returned, an EmptyResultDataAccessException
     * should be thrown.
     *
     * For this test to succeed a package-info.java file has to exist
     * with the following lines in it:
     *
     * @org.springframework.lang.NonNullApi package ttl.larku.dao.repository;
     * This is what kicks in Spring Data's null checking mechanism.
     */
    @Test
    public void testForException() {
        assertThrows(EmptyResultDataAccessException.class, () -> {
            Course course = customRepo.findExceptionById(1000);
            assertNull(course);
        });
    }

    @Test
    public void testForNoException() {
        Course course = customRepo.findExceptionById(1);
        assertNotNull(course);
    }
}
