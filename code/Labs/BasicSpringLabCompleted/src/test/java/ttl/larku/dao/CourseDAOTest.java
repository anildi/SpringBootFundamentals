package ttl.larku.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ttl.larku.domain.Course;
import ttl.larku.domain.ScheduledClass;
import ttl.larku.domain.Student;
import ttl.larku.jconfig.LarkUConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {LarkUConfig.class})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class CourseDAOTest {

    private String name1 = "Bloke";
    private String name2 = "Blokess";
    private String newName = "Different Bloke";

    private String code1 = "BOT-101";
    private String code2 = "BOT-202";
    private String title1 = "Intro To Botany";
    private String title2 = "Outtro To Botany";

    private String startDate1 = "10/10/2012";
    private String startDate2 = "10/10/2013";
    private String endDate1 = "05/10/2013";
    private String endDate2 = "05/10/2014";

    private Course course1;
    private Course course2;
    private ScheduledClass class1;
    private ScheduledClass class2;
    private Student student1;
    private Student student2;

    //TODO - Dependency Injection Using Annotations.
    @Autowired
    private BaseDAO<Course> dao;

    @BeforeEach
    public void setup() {
        student1 = new Student(name1);
        student2 = new Student(name2);
        course1 = new Course(code1, title1);
        course2 = new Course(code2, title2);

        class1 = new ScheduledClass(course1, startDate1, endDate1);
        class2 = new ScheduledClass(course2, startDate2, endDate2);

        //dao = new InMemoryCourseDAO();
        //dao.deleteStore();
        //dao.createStore();
    }


    @Test
    public void testCreate() {
        int newId = dao.create(course1).getId();

        Course resultCourse = dao.get(newId);

        assertEquals(newId, resultCourse.getId());
    }

    @Test
    public void testDelete() {
        course1 = dao.create(course1);
        course2 = dao.create(course2);

        int id1 = course1.getId();
        int id2 = course2.getId();

        assertEquals(2, dao.getAll().size());

        dao.delete(course2);

        assertEquals(1, dao.getAll().size());
        assertEquals(title1, dao.get(id1).getTitle());
    }

    @Test
    public void testUpdate() {
        course1 = dao.create(course1);
        int newId = course1.getId();

        Course resultCourse = dao.get(newId);

        assertEquals(newId, resultCourse.getId());

        course1.setTitle(title2);
        dao.update(course1);

        resultCourse = dao.get(course1.getId());
        assertEquals(title2, resultCourse.getTitle());
    }


}
