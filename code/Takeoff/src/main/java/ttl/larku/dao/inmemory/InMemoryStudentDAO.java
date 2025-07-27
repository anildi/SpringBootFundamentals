package ttl.larku.dao.inmemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import ttl.larku.domain.Student;

public class InMemoryStudentDAO {

    private Map<Integer, Student> students = new HashMap<Integer, Student>();
    private static AtomicInteger nextId = new AtomicInteger(0);

    public boolean update(Student updateObject) {
        if (students.containsKey(updateObject.getId())) {
            students.put(updateObject.getId(), updateObject);
            return true;
        }
        return false;
    }

    public boolean delete(int id) {
        return students.remove(id) != null;
    }

    public boolean delete(Student student) {
        return delete(student.getId());
    }


    public Student create(Student newObject) {
        //Create a new Id
        int newId = nextId.getAndIncrement();
        newObject.setId(newId);
        students.put(newId, newObject);

        return newObject;
    }

    public Optional<Student> get(int id) {
        return Optional.ofNullable(students.get(id));
    }

    public List<Student> getAll() {
        return new ArrayList<Student>(students.values());
    }

    public void deleteStore() {
        students = null;
    }

    public void createStore() {
        students = new HashMap<Integer, Student>();
        nextId = new AtomicInteger(0);
    }

    public Map<Integer, Student> getStudents() {
        return students;
    }
}
