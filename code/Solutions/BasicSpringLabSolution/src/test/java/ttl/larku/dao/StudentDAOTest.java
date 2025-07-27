package ttl.larku.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ttl.larku.domain.Student;
import ttl.larku.jconfig.LarkUConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {LarkUConfig.class})
@ImportResource("classpath:larkUContext.xml")
public class StudentDAOTest {

    private String name1 = "Bloke";
    private String name2 = "Blokess";
    private String newName = "Different Bloke";
    private Student student1;
    private Student student2;


    @Autowired
    private BaseDAO<Student> dao;

    @BeforeEach
    public void setup() {
        student1 = new Student(name1);
        student2 = new Student(name2);

        //dao = new InMemoryStudentDAO();
        dao.deleteStore();
        dao.createStore();
    }

    @Test
    public void testCreate() {

        int newId = dao.create(student1).getId();

        Student resultstudent = dao.get(newId);

        assertEquals(newId, resultstudent.getId());
    }

    @Test
    public void testUpdate() {
        student1 = dao.create(student1);
        int newId = student1.getId();

        Student resultstudent = dao.get(newId);

        assertEquals(newId, resultstudent.getId());

        student1.setName(newName);
        dao.update(student1);

        resultstudent = dao.get(student1.getId());
        assertEquals(newName, resultstudent.getName());
    }

    @Test
    public void testDelete() {
        student1 = dao.create(student1);
        student2 = dao.create(student2);

        int id1 = student1.getId();
        int id2 = student1.getId();

        assertEquals(2, dao.getAll().size());

        dao.delete(student2);

        assertEquals(1, dao.getAll().size());
        assertEquals(name1, dao.get(id1).getName());
    }

}
