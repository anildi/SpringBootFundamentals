package ttl.larku.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ttl.larku.domain.Course;
import ttl.larku.domain.ScheduledClass;
import ttl.larku.domain.Student;
import ttl.larku.jconfig.LarkUConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {LarkUConfig.class})
public class ClassDAOTest {

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

    @Autowired
    private BaseDAO<ScheduledClass> dao;

    @BeforeEach
    public void setup() {
        student1 = new Student(name1);
        student2 = new Student(name2);
        course1 = new Course(code1, title1);
        course2 = new Course(code2, title2);

        class1 = new ScheduledClass(course1, startDate1, endDate1);
        class2 = new ScheduledClass(course2, startDate2, endDate2);

        //dao = new InMemoryClassDAO();
        dao.deleteStore();
        dao.createStore();
    }

    @Test
    public void testCreate() {

        int newId = dao.create(class1).getId();

        ScheduledClass result = dao.get(newId);

        assertEquals(newId, result.getId());
    }

    @Test
    public void testUpdate() {
        class1 = dao.create(class1);
        int newId = class1.getId();

        ScheduledClass result = dao.get(newId);

        assertEquals(newId, result.getId());

        result.setCourse(course2);
        dao.update(result);

        result = dao.get(newId);
        assertEquals(title2, result.getCourse().getTitle());
    }

    @Test
    public void testDelete() {
        class1 = dao.create(class1);
        class2 = dao.create(class2);
        int id1 = class1.getId();
        int id2 = class2.getId();

        assertEquals(2, dao.getAll().size());

        dao.delete(class2);

        assertEquals(1, dao.getAll().size());
        assertEquals(course1, dao.get(id1).getCourse());
    }

}
