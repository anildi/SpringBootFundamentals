package ttl.larku.dao.inmemory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Course;

public class InMemoryCourseDAO implements BaseDAO<Course> {

    private Map<Integer, Course> courses = new ConcurrentHashMap<Integer, Course>();
    private AtomicInteger nextId = new AtomicInteger(1);
    private String from;

    public InMemoryCourseDAO() {
        this("InMem");
    }

    public InMemoryCourseDAO(String from) {
        this.from = from;
    }

    @Override
    public boolean update(Course updateObject) {
        return courses.computeIfPresent(updateObject.getId(), (key, oldValue) -> updateObject) != null;
    }

    @Override
    public boolean delete(int id) {
        return courses.remove(id) != null;
    }

    @Override
    public boolean delete(Course input) {
        return delete(input.getId());
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
    public Optional<Course> findById(int id) {
        return Optional.ofNullable(courses.get(id));
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
