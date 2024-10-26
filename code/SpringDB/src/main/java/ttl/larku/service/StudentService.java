package ttl.larku.service;

import org.springframework.transaction.annotation.Transactional;
import ttl.larku.controllers.rest.RestResultWrapper;
import ttl.larku.domain.Student;

import java.time.LocalDate;
import java.util.List;

@Transactional
public interface StudentService {
    Student createStudent(String name);

    Student createStudent(String name, String phoneNumber, LocalDate dob, Student.Status status);

    Student createStudent(Student student);

    boolean deleteStudent(int id);

    boolean updateStudent(Student student);

    default RestResultWrapper<Student> updateStudentR(Student student) {
    	throw new UnsupportedOperationException("Needs implementing");
    }

    Student getStudent(int id);

    List<Student> getAllStudents();

    List<Student> getByName(String name);

    //This one is to test for LazyInstantionException on the Service.
    //To make that happen, comment out the @Transactional on this class.
    //This makes the Transaction start and commit in the DAO instead of
    //the service.  So a call to student.getClasses().size() throws
    //the LIE.
    String getStudentNameAndClassSize(int id);

    void clear();
}
