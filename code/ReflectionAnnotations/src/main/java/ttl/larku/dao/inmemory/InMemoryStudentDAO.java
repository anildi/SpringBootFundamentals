package ttl.larku.dao.inmemory;

import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryStudentDAO implements BaseDAO<Student>{

	private Map<Integer, Student> students = new ConcurrentHashMap<>();
	private static AtomicInteger nextId = new AtomicInteger(1);

	@Override
	public boolean update(Student updateObject) {
		return students.computeIfPresent(updateObject.getId(), (k, oldValue) -> updateObject) != null;
	}

	public boolean delete(Student student) {
		return students.remove(student.getId()) == null;
	}

	public Student create(Student newObject) {
		//Create a new Id
		int newId = nextId.getAndIncrement();
		newObject.setId(newId);
		newObject.setName("InMem" + newObject.getName());
		students.put(newId, newObject);
		
		return newObject;
	}

	public Student get(int id) {
		return students.get(id);
	}

	public List<Student> getAll() {
		return new ArrayList<Student>(students.values());
	}

	public void deleteStore() {
		students = null;
		nextId.set(0);
	}

	public void createStore() {
		students = new ConcurrentHashMap<>();
		nextId.set(1);
	}

	public Map<Integer, Student> getStudents() {
		return students;
	}
}
