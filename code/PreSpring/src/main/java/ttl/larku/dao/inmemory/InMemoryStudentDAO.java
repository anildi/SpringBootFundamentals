package ttl.larku.dao.inmemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Student;

@Repository
public class InMemoryStudentDAO implements BaseDAO<Student> {

    private Map<Integer, Student> students = new HashMap<Integer, Student>();
    private static int nextId = 0;

    public InMemoryStudentDAO() {
        int stop = 0;
    }

    public boolean update(Student updateObject) {
        return students.replace(updateObject.getId(), updateObject) != null;
    }

    public boolean delete(int id) {
        return students.remove(id) != null;
    }

    public boolean delete(Student student) {
        return delete(student.getId());
    }

    public Student insert(Student newObject) {
        //Create a new Id
        int newId = nextId++;
        newObject.setId(newId);
        students.put(newId, newObject);

        return newObject;
    }

    public Optional<Student> findById(int id) {
        return Optional.ofNullable(students.get(id));
    }

    public List<Student> findAll() {
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
