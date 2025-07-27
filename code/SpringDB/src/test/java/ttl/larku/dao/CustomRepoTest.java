package ttl.larku.dao;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;
import ttl.larku.dao.repository.CustomRepo;
import ttl.larku.domain.Course;
import ttl.larku.sql.SqlScriptBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@Tag("dao")
public class CustomRepoTest extends SqlScriptBase {

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
     * This is testing the presence of @Nullable in the interface.
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
