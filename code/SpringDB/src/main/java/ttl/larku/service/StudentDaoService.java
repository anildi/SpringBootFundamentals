package ttl.larku.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttl.larku.controllers.rest.RestResultWrapper;
import ttl.larku.dao.BaseDAO;
import ttl.larku.dao.jpahibernate.JPAStudentDAO;
import ttl.larku.domain.Student;
import ttl.larku.domain.Student.Status;
import ttl.larku.domain.StudentVersioned;

@Service
@Transactional
@Primary
public class StudentDaoService implements StudentService {

	@Autowired
	private JPAStudentDAO studentDAO;

	@Override
	public Student createStudent(String name) {
		Student student = new Student(name);
		return createStudent(student);
	}

	@Override
	public Student createStudent(String name, String phoneNumber, LocalDate dob, Status status) {
		return createStudent(new Student(name, phoneNumber, dob, status));
	}

	@Override
	public Student createStudent(Student student) {
		student = studentDAO.insert(student);

		return student;
	}

	@Override
	public boolean deleteStudent(int id) {
		Student student = studentDAO.findById(id);
		if (student != null) {
			studentDAO.delete(student);
			return true;
		}
		return false;
	}

	public RestResultWrapper<Student> deleteStudentR(int id) {
		Student currStudent = studentDAO.findById(id);
		if (currStudent != null) {
			studentDAO.delete(currStudent);
			return RestResultWrapper.ofValue(currStudent);
		}
		return RestResultWrapper.ofError("No Student with id: " + id);
	}

	@Override
	public RestResultWrapper<Student> updateStudentR(Student student) {
		boolean exists = studentDAO.exists(student.getId());
		if (exists) {
			studentDAO.update(student);
			return RestResultWrapper.ofValue(student);
		}
		return RestResultWrapper.ofError("No Student with id: " + student.getId());
	}

	@Autowired
	private EntityManager entityManager;
	
	public RestResultWrapper<StudentVersioned> updateStudentVersioned(StudentVersioned student) {
        StudentVersioned currStudent = entityManager.find(StudentVersioned.class, student.getId() ,LockModeType.PESSIMISTIC_READ);
        currStudent.setName("Myrtle");
		return RestResultWrapper.ofValue(currStudent);
	}

	@Override
	public boolean updateStudent(Student newStudent) {
		Student oldStudent = studentDAO.findById(newStudent.getId());
		if (oldStudent != null) {
			studentDAO.update(newStudent);
			return true;
		}
		return false;
	}

	@Override
	public Student getStudent(int id) {
		return studentDAO.findById(id);
	}

	@Override
	public List<Student> getAllStudents() {
		return studentDAO.findAll();
	}

	@Override
	public List<Student> getByName(String name) {
		String lc = name.toLowerCase();
		List<Student> result = studentDAO.getByName(name);
		return result;
	}

	//This one is to test for LazyInstantionException on the Service.
	//To make that happen, comment out the @Transactional on StudentService.
	//This makes the Transaction start and commit in the DAO instead of
	//the service.  So a call to student.getClasses().size() throws
	//the LIE.
	@Override
	public String getStudentNameAndClassSize(int id) {
		Student student = studentDAO.findById(id);
		String result = "No Student with id: " + id;
		if(student != null) {
			result = student.getName() + ": " + student.getClasses().size();
		}

		return result;
	}

	public BaseDAO<Student> getStudentDAO() {
		return studentDAO;
	}

	public void setStudentDAO(JPAStudentDAO studentDAO) {
		this.studentDAO = studentDAO;
	}

	@Override
	public void clear() {
		studentDAO.deleteStore();
		studentDAO.createStore();
	}

}
