package ttl.larku.dao;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ttl.larku.dao.inmemory.InMemoryStudentDAO;
import ttl.larku.domain.Student;
import ttl.larku.domain.Student.Status;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
public class InMemoryStudentDAOTest {

    private String name1 = "Bloke";
    private String name2 = "Blokess";
    private String newName = "Karl Jung";
    private String phoneNumber1 = "290 298 4790";
    private String phoneNumber2 = "3838 939 93939";
    private Student student1;
    private Student student2;

    private InMemoryStudentDAO dao;

    @BeforeEach
    public void setup() {
        dao = new InMemoryStudentDAO();
        dao.createStore();

        student1 = new Student(name1, phoneNumber1, LocalDate.of(1999, 10, 10), Status.FULL_TIME);
        student2 = new Student(name2, phoneNumber2, LocalDate.of(2010, 6, 8), Status.HIBERNATING);

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
        Student student = dao.findById(1);
        assertTrue(student.getName().contains(name1));
    }

    @Test
    public void testCreate() {

        int newId = dao.insert(student1).getId();

        Student resultstudent = dao.findById(newId);

        assertEquals(newId, resultstudent.getId());
    }

    @Test
    public void testUpdate() {
        int newId = dao.insert(student1).getId();

        Student resultStudent = dao.findById(newId);

        assertEquals(newId, resultStudent.getId());

        resultStudent.setName(newName);
        dao.update(resultStudent);

        resultStudent = dao.findById(resultStudent.getId());
        assertEquals(newName, resultStudent.getName());
    }

    @Test
    public void testDelete() {
        int id1 = dao.insert(student1).getId();

        Student resultStudent = dao.findById(id1);
        assertEquals(resultStudent.getId(), id1);

        int beforeSize = dao.findAll().size();

        dao.delete(resultStudent);

        resultStudent = dao.findById(id1);

        assertEquals(beforeSize - 1, dao.findAll().size());
        assertEquals(null, resultStudent);

    }

}
