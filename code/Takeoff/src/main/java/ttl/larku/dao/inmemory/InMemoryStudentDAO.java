package ttl.larku.dao.inmemory;

import ttl.larku.dao.StudentDAO;
import ttl.larku.domain.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryStudentDAO implements StudentDAO {

    private Map<Integer, Student> students = new HashMap<Integer, Student>();
    private static AtomicInteger nextId = new AtomicInteger(0);

    @Override
    public boolean update(Student updateObject) {
        if (students.containsKey(updateObject.getId())) {
            students.put(updateObject.getId(), updateObject);
            return true;
        }
        return false;
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
