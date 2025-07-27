package ttl.larku.dao;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ttl.larku.dao.inmemory.InMemoryStudentDAO;
import ttl.larku.domain.Student;
import ttl.larku.domain.Student.Status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class StudentDAOTest {

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
        dao.deleteStore();
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
    public void testCreate() {

        int newId = dao.insert(student1).getId();

        Optional<Student> opt = dao.findById(newId);
        assertTrue(opt.isPresent());

        Student resultstudent = opt.get();
        assertEquals(newId, resultstudent.getId());
    }

    @Test
    public void testUpdate() {
        int newId = dao.insert(student1).getId();

        Optional<Student> opt = dao.findById(newId);
        assertTrue(opt.isPresent());

        Student resultStudent = opt.get();

        assertEquals(newId, resultStudent.getId());

        resultStudent.setName(newName);
        dao.update(resultStudent);

        resultStudent = dao.findById(resultStudent.getId()).orElse(null);
        assertEquals(newName, resultStudent.getName());
    }

    @Test
    public void testDelete() {
        int id1 = dao.insert(student1).getId();

        Optional<Student> opt = dao.findById(id1);
        assertTrue(opt.isPresent());

        Student resultStudent = opt.get();
        assertEquals(resultStudent.getId(), id1);

        int beforeSize = dao.findAll().size();

        dao.delete(resultStudent);

        resultStudent = dao.findById(id1).orElse(null);

        assertEquals(beforeSize - 1, dao.findAll().size());
        assertEquals(null, resultStudent);

    }

}
