package ttl.larku.dao;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ttl.larku.domain.Course;
import ttl.larku.domain.ScheduledClass;
import ttl.larku.domain.Student;
import ttl.larku.sql.SqlScriptBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
////        properties = {"ttl.sql.schema-file=/ttl/larku/db/createDB-h2.sql",
//                "ttl.larku.data-file=/ttl/larku/db/populateDB-h2.sql"})
//Populate your DB.  From Most Expensive to least expensive

//This will make recreate the context after every test.
//In conjunction with appropriate 'schema[-XXX].sql' and 'data[-XXX].sql' files
//it will also drop and recreate the DB before each test.
//@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)

//Or you can just re-run the sql files before each test method
//@Sql(scripts = { "/ttl/larku/db/createDB-h2.sql", "/ttl/larku/db/populateDB-h2.sql" }, executionPhase= Sql.ExecutionPhase.BEFORE_TEST_METHOD)

//This next one will roll back the transaction after
//each test, so the database will actually stay the
//same for the next test.
@Transactional
@Tag("dao")
public class CourseDAOTest extends SqlScriptBase {

    private String name1 = "Bloke";
    private String name2 = "Blokess";
    private String newName = "Different Bloke";

    private String code1 = "BOT-101";
    private String code2 = "BOT-202";
    private String title1 = "Intro To Botany";
    private String title2 = "Outtro To Botany";

    private LocalDate startDate1 = LocalDate.parse("2012-10-10");
    private LocalDate startDate2 = LocalDate.parse("2013-10-10");
    private LocalDate endDate1 = LocalDate.parse("2013-05-10");
    private LocalDate endDate2 = LocalDate.parse("2014-05-10");

    private Course course1;
    private Course course2;
    private ScheduledClass class1;
    private ScheduledClass class2;
    private Student student1;
    private Student student2;

    @Resource(name = "courseDAO")
    private BaseDAO<Course> dao;

    @BeforeEach
    public void setup() {
        student1 = new Student(name1);
        student2 = new Student(name2);
        course1 = new Course(code1, title1);
        course2 = new Course(code2, title2);

        class1 = new ScheduledClass(course1, startDate1, endDate1);
        class2 = new ScheduledClass(course2, startDate2, endDate2);
    }

    @Test
    public void testGetAll() {
        List<Course> courses = dao.findAll();
        assertEquals(3, courses.size());
    }

    @Test
    public void testCreate() {
        assertEquals(3, dao.findAll().size());
        int newId = dao.insert(course1).getId();

        Course resultCourse = dao.findById(newId);

        assertEquals(newId, resultCourse.getId());
        assertEquals(4, dao.findAll().size());
    }

    @Test
    public void testDelete() {
        assertThrows(NullPointerException.class, () -> {
            assertEquals(3, dao.findAll().size());
            int id1 = dao.insert(course1).getId();

            Course resultCourse = dao.findById(id1);
            assertEquals(resultCourse.getId(), id1);

            dao.delete(resultCourse);

            resultCourse = dao.findById(id1);

            assertEquals(title1, dao.findById(id1).getTitle());
            assertEquals(3, dao.findAll().size());
        });
    }

    @Test
    public void testUpdate() {
        assertEquals(3, dao.findAll().size());

        int newId = 1;
        Course resultCourse = dao.findById(newId);

        assertEquals(newId, resultCourse.getId());

        resultCourse.setTitle(title2);
        dao.update(resultCourse);

        resultCourse = dao.findById(resultCourse.getId());
        assertEquals(title2, resultCourse.getTitle());
        assertEquals(3, dao.findAll().size());
    }
}
