package ttl.larku.service;

import org.springframework.transaction.annotation.Transactional;
import ttl.larku.controllers.rest.RestResultGeneric;
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

    default RestResultGeneric<Student> updateStudentR(Student student) {
    	throw new UnsupportedOperationException("Needs implementing");
    }

    Student getStudent(int id);

    List<Student> getAllStudents();

    List<Student> getByName(String name);

    void clear();
}
