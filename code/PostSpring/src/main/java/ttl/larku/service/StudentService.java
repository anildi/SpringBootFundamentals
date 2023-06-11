package ttl.larku.service;

import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Student;

import java.time.LocalDate;
import java.util.List;

public class StudentService {

    private BaseDAO<Student> studentDAO;

    public StudentService() {
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
        student = studentDAO.create(student);

        return student;
    }

    public boolean deleteStudent(int id) {
        Student student = studentDAO.get(id);
        if (student != null) {
            return studentDAO.delete(student);
        }
        return false;
    }

    public boolean updateStudent(Student newStudent) {
        Student oldStudent = studentDAO.get(newStudent.getId());
        if(oldStudent != null) {
            return studentDAO.update(newStudent);
        }
        return false;
    }

    public Student getStudent(int id) {
        return studentDAO.get(id);
    }

    public List<Student> getAllStudents() {
        return studentDAO.getAll();
    }

    public BaseDAO<Student> getStudentDAO() {
        return studentDAO;
    }

    public void setStudentDAO(BaseDAO<Student> studentDAO) {
        this.studentDAO = studentDAO;
    }

    public void clear() {
        studentDAO.deleteStore();
        studentDAO.createStore();
    }

    public CourseService getCs() {
        return cs;
    }

    public void setCs(CourseService cs) {
        this.cs = cs;
    }
}
