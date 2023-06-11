package ttl.larku.dao.inmemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Student;

public class InMemoryStudentDAO implements BaseDAO<Student> {

    private Map<Integer, Student> students = new HashMap<Integer, Student>();
    private static int nextId = 0;

    public void update(Student updateObject) {
        if (students.containsKey(updateObject.getId())) {
            students.put(updateObject.getId(), updateObject);
        }
    }

    public void delete(Student student) {
        students.remove(student.getId());
    }

    public Student create(Student newObject) {
        //Create a new Id
        int newId = nextId++;
        newObject.setId(newId);
        students.put(newId, newObject);

        return newObject;
    }

    public Student get(int id) {
        return students.get(id);
    }

    public List<Student> getAll() {
        return new ArrayList<Student>(students.values());
    }

    public void deleteStore() {
        students = null;
    }

    public void createStore() {
        students = new HashMap<Integer, Student>();
    }

    public Map<Integer, Student> getStudents() {
        return students;
    }
}
