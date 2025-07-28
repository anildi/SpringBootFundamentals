package ttl.larku.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import ttl.larku.dao.DAOFactory;
import ttl.larku.dao.inmemory.InMemoryStudentDAO;
import ttl.larku.dao.inmemory.StudentDAO;
import ttl.larku.dao.jpa.JPAStudentDAO;
import ttl.larku.domain.Student;
import ttl.larku.domain.Student.Status;

public class StudentService {

   List<String> stuff = new ArrayList<>();
//   ArrayList<String> stuff = new ArrayList<>();

//    private InMemoryStudentDAO studentDAO;

   //private JPAStudentDAO studentDAO;
   private StudentDAO studentDAO;

   public StudentService() {
//      studentDAO = new InMemoryStudentDAO();
//      studentDAO = new JPAStudentDAO();
//      studentDAO = DAOFactory.studentDAO();
   }

   public Student createStudent(String name, String phoneNumber, Status status) {
      Student student = new Student(name, phoneNumber, status);
      student = createStudent(student);
      return student;
   }

   public Student createStudent(Student student) {
      student = studentDAO.create(student);

      return student;
   }

   public boolean deleteStudent(int id) {
      return studentDAO.delete(id);
   }

   public boolean updateStudent(Student newStudent) {
      return studentDAO.update(newStudent);
   }

   public Optional<Student> getStudent(int id) {
      return studentDAO.get(id);
   }

   public List<Student> getAllStudents() {
      return studentDAO.getAll();
   }

   //    public InMemoryStudentDAO getStudentDAO() {
//    public JPAStudentDAO getStudentDAO() {
   public StudentDAO getStudentDAO() {
      return studentDAO;
   }

   public void setStudentDAO(StudentDAO studentDAO) {
      this.studentDAO = studentDAO;
   }

}
