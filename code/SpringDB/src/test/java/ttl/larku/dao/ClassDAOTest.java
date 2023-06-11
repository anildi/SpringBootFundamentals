package ttl.larku.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ttl.larku.dao.jpahibernate.JPAClassDAO;
import ttl.larku.domain.Course;
import ttl.larku.domain.ScheduledClass;
import ttl.larku.sql.SqlScriptBase;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
//Populate your DB.  From Most Expensive to least expensive

//This will make recreate the context after every test.
//In conjunction with appropriate 'schema[-XXX].sql' and 'data[-XXX].sql' files
//it will also drop and recreate the DB before each test.
//@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)

//Or you can just re-run the sql files before each test method
//@Sql(scripts = { "/schema-h2.sql", "/data-h2.sql" }, executionPhase= Sql.ExecutionPhase.BEFORE_TEST_METHOD)

//This next one will roll back the transaction after
//each test, so the database will actually stay the
//same for the next test.
//@Transactional
@Tag("dao")
public class ClassDAOTest extends SqlScriptBase {

    private String name1 = "Bloke";
    private String name2 = "Blokess";
    private String newName = "Different Bloke";

    private String code1 = "BOT-101";
    private String code2 = "BOT-202";
    private String title1 = "Intro To Botany";
    private String title2 = "Advanced Basket Weaving";

    private LocalDate startDate1 = LocalDate.parse("2012-10-10");
    private LocalDate startDate2 = LocalDate.parse("2013-10-10");
    private LocalDate endDate1 = LocalDate.parse("2013-05-10");
    private LocalDate endDate2 = LocalDate.parse("2014-05-10");

    private Course course1;
    private Course course2;
    private ScheduledClass class1;
    private ScheduledClass class2;

    @Resource(name = "classDAO")
    //private BaseDAO<ScheduledClass> dao;
    private JPAClassDAO dao;

    @Resource(name = "courseDAO")
    private BaseDAO<Course> courseDAO;

    @BeforeEach
    public void setup() {
        course1 = new Course(code1, title1);
        course1 = courseDAO.create(course1);
        course2 = new Course(code2, title2);
        course2 = courseDAO.create(course2);

        class1 = new ScheduledClass(course1, startDate1, endDate1);
        class2 = new ScheduledClass(course2, startDate2, endDate2);
    }

    @Test
    public void testGetAll() {
        List<ScheduledClass> classes = dao.getAll();
//        classes.forEach(System.out::println);
        assertEquals(3, classes.size());
    }

    @Test
    public void testFindOne() {
        ScheduledClass classes = dao.get(1);
//        classes.forEach(System.out::println);
        assertEquals(1, classes.getId());
    }

    @Test
    public void testGetByCourseCodeAndStartDate() {
        List<ScheduledClass> classes = dao.getByCourseCodeAndStartDate("BOT-202", LocalDate.parse("2012-10-10"));
        classes.forEach(System.out::println);
        assertEquals(1, classes.size());
    }

    @Test
    public void testGetByCourseCodeAndStartDateforStudents() {
        List<ScheduledClass> classes = dao.getByCourseCodeAndStartDateForStudents("BOT-202", LocalDate.parse("2012-10-10"));
        classes.forEach(System.out::println);
        assertEquals(1, classes.size());
    }

    @Test
    public void testGetByCourseCode() {
        List<ScheduledClass> classes = dao.getByCourseCode("BOT-202");
        classes.forEach(System.out::println);
        assertEquals(1, classes.size());
    }

    @Test
    public void testCreate() {

        int newId = dao.create(class1).getId();

        ScheduledClass result = dao.get(newId);

        assertEquals(newId, result.getId());
    }

    @Test
    public void testUpdate() {
        int newId = dao.create(class1).getId();

        ScheduledClass result = dao.get(newId);

        assertEquals(newId, result.getId());

        result.setCourse(course2);
        dao.update(result);

        result = dao.get(newId);
        assertEquals(title2, result.getCourse().getTitle());
    }

    @Test
    public void testDelete() {
        int id1 = dao.create(class1).getId();

        ScheduledClass resultClass = dao.get(id1);
        assertEquals(resultClass.getId(), id1);

        dao.delete(resultClass);

        resultClass = dao.get(id1);

        assertEquals(null, resultClass);
    }
}
