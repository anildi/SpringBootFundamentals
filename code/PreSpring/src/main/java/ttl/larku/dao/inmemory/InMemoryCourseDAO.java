package ttl.larku.dao.inmemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Course;

public class InMemoryCourseDAO implements BaseDAO<Course> {

    private Map<Integer, Course> courses = new HashMap<Integer, Course>();
    private static int nextId = 0;

    public void update(Course updateObject) {
        if (courses.containsKey(updateObject.getId())) {
            courses.put(updateObject.getId(), updateObject);
        }
    }

    public void delete(Course course) {
        courses.remove(course.getId());
    }

    public Course create(Course newObject) {
        //Create a new Id
        int newId = nextId++;
        newObject.setId(newId);
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
        courses = new HashMap<Integer, Course>();
    }

    public Map<Integer, Course> getCourses() {
        return courses;
    }

    public void setCourses(Map<Integer, Course> courses) {
        this.courses = courses;
    }
}
