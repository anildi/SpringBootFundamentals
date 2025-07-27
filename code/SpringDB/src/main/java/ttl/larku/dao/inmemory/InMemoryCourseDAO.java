package ttl.larku.dao.inmemory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import ttl.larku.dao.CourseDAO;
import ttl.larku.domain.Course;

public class InMemoryCourseDAO implements CourseDAO {

    private Map<Integer, Course> courses = new ConcurrentHashMap<Integer, Course>();
    private AtomicInteger nextId = new AtomicInteger(1);

    @Override
    public boolean update(Course updateObject) {
        return courses.computeIfPresent(updateObject.getId(), (key, oldValue) -> updateObject) != null;
    }

    @Override
    public boolean delete(Course course) {
        return courses.remove(course.getId()) != null;
    }

    @Override
    public Course insert(Course newObject) {
        //Create a new Id
        int newId = nextId.getAndIncrement();
        newObject.setId(newId);
        courses.put(newId, newObject);

        return newObject;
    }

    @Override
    public Course findById(int id) {
        return courses.get(id);
    }

    @Override
    public Course findByCode(String code) {
        var result = courses.entrySet().stream()
              .filter(entry -> entry.getValue().getCode().equals(code))
              .findFirst().orElse(null);
        return result.getValue();
    }

    @Override
    public List<Course> findAll() {
        return new ArrayList<Course>(courses.values());
    }

    @Override
    public void deleteStore() {
        courses = null;
    }

    @Override
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
