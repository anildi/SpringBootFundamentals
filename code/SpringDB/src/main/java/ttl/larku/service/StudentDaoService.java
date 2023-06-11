package ttl.larku.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ttl.larku.controllers.rest.RestResultGeneric;
import ttl.larku.dao.BaseDAO;
import ttl.larku.dao.jpahibernate.JPAStudentDAO;
import ttl.larku.domain.Student;
import ttl.larku.domain.Student.Status;
import ttl.larku.domain.StudentVersioned;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;

@Service
//@Transactional
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
		student = studentDAO.create(student);

		return student;
	}

	@Override
	public boolean deleteStudent(int id) {
		Student student = studentDAO.get(id);
		if (student != null) {
			studentDAO.delete(student);
			return true;
		}
		return false;
	}

	public RestResultGeneric<Student> deleteStudentR(int id) {
		Student currStudent = studentDAO.get(id);
		if (currStudent != null) {
			studentDAO.delete(currStudent);
			return RestResultGeneric.ofValue(currStudent);
		}
		return RestResultGeneric.ofError("No Student with id: " + id);
	}

	@Override
	public RestResultGeneric<Student> updateStudentR(Student student) {
		Student currStudent = studentDAO.get(student.getId());
		if (currStudent != null) {
			studentDAO.update(student);
			return RestResultGeneric.ofValue(student);
		}
		return RestResultGeneric.ofError("No Student with id: " + student.getId());
	}

	@Autowired
	private EntityManager entityManager;
	
	public RestResultGeneric<StudentVersioned> updateStudentVersioned(StudentVersioned student) {
        StudentVersioned currStudent = entityManager.find(StudentVersioned.class, student.getId() ,LockModeType.PESSIMISTIC_READ);
        currStudent.setName("Myrtle");
		return RestResultGeneric.ofValue(currStudent);
	}

	@Override
	public boolean updateStudent(Student newStudent) {
		Student oldStudent = studentDAO.get(newStudent.getId());
		if (oldStudent != null) {
			studentDAO.update(newStudent);
			return true;
		}
		return false;
	}

	@Override
	public Student getStudent(int id) {
		return studentDAO.get(id);
	}

	@Override
	public List<Student> getAllStudents() {
		return studentDAO.getAll();
	}

	@Override
	public List<Student> getByName(String name) {
		String lc = name.toLowerCase();
		List<Student> result = studentDAO.getByName(name);
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
