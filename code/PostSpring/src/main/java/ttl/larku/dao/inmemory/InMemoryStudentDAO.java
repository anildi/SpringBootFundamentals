package ttl.larku.dao.inmemory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Student;

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
    public boolean delete(int id) {
        return students.remove(id) != null;
    }

    @Override
    public boolean delete(Student input) {
        return delete(input.getId());
    }

    @Override
    public Student insert(Student newObject) {
        //Create a new Id
        int newId = nextId.getAndIncrement();
        newObject.setId(newId);
        students.put(newId, newObject);

        newObject.setName(from + newObject.getName());
        return newObject;
    }

    @Override
    public Optional<Student> findById(int id) {
        return Optional.ofNullable(students.get(id));
    }

    @Override
    public List<Student> findAll() {
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
