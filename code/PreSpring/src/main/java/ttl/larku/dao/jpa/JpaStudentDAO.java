package ttl.larku.dao.jpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Student;

public class JpaStudentDAO implements BaseDAO<Student> {

    private Map<Integer, Student> students = new HashMap<Integer, Student>();
    private static int nextId = 0;

    /* (non-Javadoc)
     * @see ttl.larku.dao.jpa.BaseDAO#update(ttl.larku.domain.Student)
     */
    @Override
    public void update(Student updateObject) {
        if (students.containsKey(updateObject.getId())) {
            students.put(updateObject.getId(), updateObject);
        }
    }

    /* (non-Javadoc)
     * @see ttl.larku.dao.jpa.BaseDAO#delete(ttl.larku.domain.Student)
     */
    @Override
    public void delete(Student student) {
        students.remove(student.getId());
    }

    /* (non-Javadoc)
     * @see ttl.larku.dao.jpa.BaseDAO#create(ttl.larku.domain.Student)
     */
    @Override
    public Student create(Student newObject) {
        //Create a new Id
        int newId = nextId++;
        newObject.setId(newId);
        newObject.setName("Jpa" + newObject.getName());
        students.put(newId, newObject);

        return newObject;
    }

    /* (non-Javadoc)
     * @see ttl.larku.dao.jpa.BaseDAO#get(int)
     */
    @Override
    public Student get(int id) {
        return students.get(id);
    }

    /* (non-Javadoc)
     * @see ttl.larku.dao.jpa.BaseDAO#getAll()
     */
    @Override
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
