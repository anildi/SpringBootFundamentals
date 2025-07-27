package ttl.larku.dao;


import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import ttl.larku.dao.jpahibernate.JPAStudentDAO;
import ttl.larku.domain.Student;
import ttl.larku.domain.Student.Status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Tag("unit")
public class JPAStudentDAOTest {

    private String name1 = "Bloke";
    private String name2 = "Blokess";
    private String newName = "Karl Jung";
    private String phoneNumber1 = "290 298 4790";
    private String phoneNumber2 = "3838 939 93939";
    private Student student1;
    private Student student2;

    @Autowired
    @Qualifier("jpaStudentDAO")
    private JPAStudentDAO dao;

    @BeforeEach
    public void setup() {
        dao.createStore();

        student1 = new Student(name1, phoneNumber1, Status.FULL_TIME);
        student2 = new Student(name2, phoneNumber2, Status.HIBERNATING);

        dao.insert(student1);
        dao.insert(student2);
    }


    @Test
    public void testGetAll() {
        List<Student> students = dao.findAll();
        assertEquals(2, students.size());
    }

    @Test
    public void testGetOne() {
        Student student = dao.findById(1).orElse(null);
        assertTrue(student.getName().contains(name1));
    }

    @Test
    public void testCreate() {

        int newId = dao.insert(student1).getId();

        Student resultstudent = dao.findById(newId).orElse(null);

        assertEquals(newId, resultstudent.getId());
    }

    @Test
    public void testUpdate() {
        int newId = dao.insert(student1).getId();

        Student resultStudent = dao.findById(newId).orElse(null);

        assertEquals(newId, resultStudent.getId());

        resultStudent.setName(newName);
        dao.update(resultStudent);

        resultStudent = dao.findById(resultStudent.getId()).orElse(null);
        assertEquals(newName, resultStudent.getName());
    }

    @Test
    public void testDelete() {
        int id1 = dao.insert(student1).getId();

        Student resultStudent = dao.findById(id1).orElse(null);
        assertEquals(resultStudent.getId(), id1);

        int beforeSize = dao.findAll().size();

        dao.delete(resultStudent);

        resultStudent = dao.findById(id1).orElse(null);

        assertEquals(beforeSize - 1, dao.findAll().size());
        assertEquals(null, resultStudent);

    }

}
