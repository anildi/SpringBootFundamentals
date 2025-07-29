package ttl.larku.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Student;

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
      student = studentDAO.insert(student);

      return student;
   }

   public boolean deleteStudent(int id) {
      return studentDAO.delete(id);
   }

   public boolean updateStudent(Student newStudent) {
      return studentDAO.update(newStudent);
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
