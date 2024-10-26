package ttl.larku.dao.inmemory;

import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Course;
import ttl.larku.reflect.inject.MyInject;
import ttl.otherpack.OtherImportantService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryCourseDAO implements BaseDAO<Course>{

	private Map<Integer, Course> courses = new ConcurrentHashMap<>();
	private static AtomicInteger nextId = new AtomicInteger(1);

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
		newObject.setCode("InMem" + newObject.getCode());
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
	
	void deleteStore() {
		courses = null;
		nextId.set(0);
	}
	
	public void createStore() {
		courses = new ConcurrentHashMap<Integer, Course>();
		nextId.set(1);
	}
	
	public Map<Integer, Course> getCourses() {
		return courses;
	}

	public void setCourses(Map<Integer, Course> courses) {
		this.courses = courses;
	}
}
