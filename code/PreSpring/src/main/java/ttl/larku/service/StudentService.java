package ttl.larku.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import ttl.larku.dao.BaseDAO;
import ttl.larku.dao.inmemory.InMemoryStudentDAO;
import ttl.larku.domain.Student;
import ttl.larku.domain.Student.Status;

public class StudentService {

    List<String> stuff = new ArrayList<>();

    //private InMemoryStudentDAO studentDAO;
    private BaseDAO<Student> studentDAO;

    public StudentService() {
        studentDAO = new InMemoryStudentDAO();
    }

    public Student createStudent(String name, String phoneNumber, Status status) {
        Student student = new Student(name, phoneNumber, status);
        student = studentDAO.insert(student);

        return student;
    }

    public Student createStudent(Student student) {
        student = studentDAO.insert(student);

        return student;
    }

    public boolean deleteStudent(int id) {
        return studentDAO.delete(id);
    }

    public boolean updateStudent(Student student) {
        return studentDAO.update(student);
    }

    public Optional<Student> getStudent(int id) {
        return studentDAO.findById(id);
    }

    public List<Student> getAllStudents() {
        return studentDAO.findAll();
    }

    public BaseDAO<Student> getStudentDAO() {
        return studentDAO;
    }

    public void setStudentDAO(BaseDAO<Student> studentDAO) {
        this.studentDAO = studentDAO;
    }
}
