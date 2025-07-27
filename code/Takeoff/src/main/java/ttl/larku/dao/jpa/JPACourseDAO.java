package ttl.larku.dao.jpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import ttl.larku.domain.Course;

public class JPACourseDAO {

    private Map<Integer, Course> courses = new HashMap<Integer, Course>();
    private static AtomicInteger nextId = new AtomicInteger(0);

    public boolean update(Course updateObject) {
        if (courses.containsKey(updateObject.getId())) {
            courses.put(updateObject.getId(), updateObject);
            return true;
        }
        return false;
    }

    public boolean delete(int id) {
        return courses.remove(id) != null;
    }

    public boolean delete(Course course) {
        return delete(course.getId());
    }

    public Course create(Course newObject) {
        //Create a new Id
        int newId = nextId.getAndIncrement();
        newObject.setId(newId);
        newObject.setName("JPA: " + newObject.getName());
        courses.put(newId, newObject);

        return newObject;
    }

    public Optional<Course> get(int id) {
        return Optional.ofNullable(courses.get(id));
    }

    public List<Course> getAll() {
        return new ArrayList<Course>(courses.values());
    }

    public void deleteStore() {
        courses = null;
    }

    public void createStore() {
        courses = new HashMap<Integer, Course>();
        nextId = new AtomicInteger(0);
    }

    public Map<Integer, Course> getCourses() {
        return courses;
    }

    public void setCourses(Map<Integer, Course> courses) {
        this.courses = courses;
    }
}
