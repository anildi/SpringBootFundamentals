package ttl.larku.dao.inmemory;

import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryStudentDAO implements BaseDAO<Student> {

    private Map<Integer, Student> students = new ConcurrentHashMap<>();
    private AtomicInteger nextId = new AtomicInteger(1);
    private String from;

    public InMemoryStudentDAO(String from) {
        this.from = from + ": ";
    }

    public InMemoryStudentDAO() {
        this("InMem");
    }

    @Override
    public boolean update(Student updateObject) {
        return students.computeIfPresent(updateObject.getId(), (key, oldValue) -> updateObject) != null;
    }

    @Override
    public boolean delete(Student student) {
        return students.remove(student.getId()) != null;
    }

    @Override
    public Student create(Student newObject) {
        //Create a new Id
        int newId = nextId.getAndIncrement();
        newObject.setId(newId);
        students.put(newId, newObject);

        newObject.setName(from + newObject.getName());
        return newObject;
    }

    @Override
    public Student get(int id) {
        return students.get(id);
    }

    @Override
    public List<Student> getAll() {
        return new ArrayList<Student>(students.values());
    }

    @Override
    public void deleteStore() {
        students = null;
    }

    @Override
    public void createStore() {
        students = new ConcurrentHashMap<>();
        nextId = new AtomicInteger(1);
    }

    public Map<Integer, Student> getStudents() {
        return students;
    }

    public void setStudents(Map<Integer, Student> students) {
        this.students = students;
    }
}
