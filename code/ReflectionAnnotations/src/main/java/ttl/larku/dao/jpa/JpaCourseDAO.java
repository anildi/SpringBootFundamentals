package ttl.larku.dao.jpa;

import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Course;
import ttl.larku.reflect.inject.MyInject;
import ttl.otherpack.OtherImportantService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class JpaCourseDAO implements BaseDAO<Course>{

	private Map<Integer, Course> courses = new ConcurrentHashMap<>();
	private AtomicInteger nextId = new AtomicInteger(1);

	@MyInject
	private OtherImportantService otherService;

	public boolean update(Course updateObject) {
		return courses.computeIfPresent(updateObject.getId(), (k, oldValue) -> updateObject) != null;
	}

	public boolean delete(Course course) {
		return courses.remove(course.getId()) != null;
	}

	public Course create(Course newObject) {
		//Create a new Id
		int newId = nextId.getAndIncrement();
		newObject.setId(newId);
		newObject.setCode("Jpa" + newObject.getCode());
		courses.put(newId, newObject);
		
		return newObject;
	}

	public Course get(int id) {
		return courses.get(id);
	}

	public List<Course> getAll() {
		System.out.println(otherService.doStuff("things"));
		return new ArrayList<Course>(courses.values());
	}
	
	public void deleteStore() {
		courses = null;
	}
	
	public void createStore() {
		courses = new ConcurrentHashMap<>();
		nextId.set(1);
	}
	
	public Map<Integer, Course> getCourses() {
		return courses;
	}

	public void setCourses(Map<Integer, Course> courses) {
		this.courses = courses;
	}
}
