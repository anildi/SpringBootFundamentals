package ttl.larku.dao.jpahibernate;

import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Course;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class JPACourseDAO implements BaseDAO<Course> {

    private Map<Integer, Course> courses = new ConcurrentHashMap<>();
    private AtomicInteger nextId = new AtomicInteger(1);

    private String from;

    public JPACourseDAO(String from) {
        this.from = from + ": ";
    }

    public JPACourseDAO() {
        this("JPA");
    }

    public boolean update(Course updateObject) {
        return courses.computeIfPresent(updateObject.getId(), (key, oldValue) -> updateObject) != null;
    }

    public boolean delete(Course course) {
        return courses.remove(course.getId()) != null;
    }

    public Course create(Course newObject) {
        //Create a new Id
        int newId = nextId.getAndIncrement();
        newObject.setId(newId);
        newObject.setCode(from + newObject.getCode());
        courses.put(newId, newObject);

        return newObject;
    }

    public Course get(int id) {
        return courses.get(id);
    }

    public List<Course> getAll() {
        return new ArrayList<Course>(courses.values());
    }

    public void deleteStore() {
        courses = null;
    }

    public void createStore() {
        courses = new ConcurrentHashMap<>();
        nextId = new AtomicInteger(1);
    }

    public Map<Integer, Course> getCourses() {
        return courses;
    }

    public void setCourses(Map<Integer, Course> courses) {
        this.courses = courses;
    }
}
