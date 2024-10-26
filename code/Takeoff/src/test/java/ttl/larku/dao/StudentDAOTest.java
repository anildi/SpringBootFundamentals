package ttl.larku.dao;

import org.junit.Before;
import org.junit.Test;
import ttl.larku.dao.inmemory.InMemoryStudentDAO;
import ttl.larku.domain.Student;
import ttl.larku.domain.Student.Status;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class StudentDAOTest {

    private String name1 = "Bloke";
    private String name2 = "Blokess";
    private String newName = "Karl Jung";
    private String phoneNumber1 = "290 298 4790";
    private String phoneNumber2 = "3838 939 93939";
    private Student student1;
    private Student student2;

    private InMemoryStudentDAO dao;

    @Before
    public void setup() {
        dao = new InMemoryStudentDAO();
        dao.deleteStore();
        dao.createStore();

        student1 = new Student(name1, phoneNumber1, Status.FULL_TIME);
        student2 = new Student(name2, phoneNumber2, Status.HIBERNATING);

        dao.create(student1);
        dao.create(student2);
    }


    @Test
    public void testGetAll() {
        List<Student> students = dao.getAll();
        assertEquals(2, students.size());
    }

    @Test
    public void testCreate() {

        int newId = dao.create(student1).getId();

        Student resultstudent = dao.get(newId);

        assertEquals(newId, resultstudent.getId());
    }

    @Test
    public void testUpdate() {
        int newId = dao.create(student1).getId();

        Student resultStudent = dao.get(newId);

        assertEquals(newId, resultStudent.getId());

        resultStudent.setName(newName);
        dao.update(resultStudent);

        resultStudent = dao.get(resultStudent.getId());
        assertEquals(newName, resultStudent.getName());
    }

    @Test
    public void testDelete() {
        int id1 = dao.create(student1).getId();

        Student resultStudent = dao.get(id1);
        assertEquals(resultStudent.getId(), id1);

        int beforeSize = dao.getAll().size();

        dao.delete(resultStudent);

        resultStudent = dao.get(id1);

        assertEquals(beforeSize - 1, dao.getAll().size());
        assertEquals(null, resultStudent);

    }

}
