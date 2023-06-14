package ttl.larku.service;

import org.springframework.stereotype.Service;
import ttl.larku.dao.BaseDAO;
import ttl.larku.dao.repo.StudentRepo;
import ttl.larku.domain.Student;

import java.time.LocalDate;
import java.util.List;

@Service
public class StudentRepoService {

    private StudentRepo studentDAO;

    //Ctor Injection!
    public StudentRepoService(StudentRepo studentDAO) {
        this.studentDAO = studentDAO;
    }

    private CourseService cs;

    public Student createStudent(String name, String phoneNumber, Student.Status status) {
        Student student = new Student(name, phoneNumber, status);
        student = createStudent(student);

        return student;
    }

    public Student createStudent(String name, String phoneNumber, LocalDate dob, Student.Status status) {
        Student student = new Student(name, phoneNumber, dob, status);
        student = createStudent(student);

        return student;
    }

    public Student createStudent(Student student) {
        student = studentDAO.save(student);

        return student;
    }

    public boolean deleteStudent(int id) {
        Student student = studentDAO.findById(id).orElse(null);
        if (student != null) {
            studentDAO.delete(student);
            return true;
        }
        return false;
    }

    public boolean updateStudent(Student newStudent) {
        Student oldStudent = studentDAO.findById(newStudent.getId()).orElse(null);
        if(oldStudent != null) {
            studentDAO.save(newStudent);
            return true;
        }
        return false;
    }

    public Student getStudent(int id) {
        return studentDAO.findById(id).orElse(null);
    }

    public List<Student> getAllStudents() {
        return studentDAO.findAll();
    }

//    public BaseDAO<Student> getStudentDAO() {
//        return studentDAO;
//    }
//
//    public void setStudentDAO(BaseDAO<Student> studentDAO) {
//        this.studentDAO = studentDAO;
//    }

    public void clear() {
//        studentDAO.deleteStore();
//        studentDAO.createStore();
    }

    public CourseService getCs() {
        return cs;
    }

    public void setCs(CourseService cs) {
        this.cs = cs;
    }
}
