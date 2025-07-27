package ttl.larku.dao.jpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Course;

public class JPACourseDAO implements BaseDAO<Course> {

    private Map<Integer, Course> courses = new HashMap<Integer, Course>();
    private static int nextId = 0;

    public boolean update(Course updateObject) {
        return courses.replace(updateObject.getId(), updateObject) != null;
    }

    public boolean delete(int id) {
        return courses.remove(id) != null;
    }

    public boolean delete(Course course) {
        return delete(course.getId());
    }

    public Course insert(Course newObject) {
        //Create a new Id
        int newId = nextId++;
        newObject.setId(newId);
        courses.put(newId, newObject);

        return newObject;
    }

    public Optional<Course> findById(int id) {
        return Optional.ofNullable(courses.get(id));
    }

    public List<Course> findAll() {
        return new ArrayList<Course>(courses.values());
    }

    public void deleteStore() {
        courses = null;
    }

    public void createStore() {
        courses = new HashMap<Integer, Course>();
    }

    public Map<Integer, Course> getCourses() {
        return courses;
    }

    public void setCourses(Map<Integer, Course> courses) {
        this.courses = courses;
    }
}
