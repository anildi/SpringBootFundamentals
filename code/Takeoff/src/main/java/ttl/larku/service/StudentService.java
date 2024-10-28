package ttl.larku.service;

import ttl.larku.dao.DAOFactory;
import ttl.larku.dao.StudentDAO;
import ttl.larku.dao.inmemory.InMemoryStudentDAO;
import ttl.larku.dao.jpa.JpaStudentDAO;
import ttl.larku.domain.Student;
import ttl.larku.domain.Student.Status;

import java.util.ArrayList;
import java.util.List;

public class StudentService {

   List<String> stuff = new ArrayList<>();
//   ArrayList<String> stuff = new ArrayList<>();

   //   private InMemoryStudentDAO studentDAO;
//   private JpaStudentDAO studentDAO;
   private StudentDAO studentDAO;

   public StudentService() {
      studentDAO = DAOFactory.studentDAO();
//      studentDAO = new InMemoryStudentDAO();
//      studentDAO = new JpaStudentDAO();
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
      Student student = studentDAO.get(id);
      if (student != null) {
         return studentDAO.delete(student);
      }
      return false;
   }

   public boolean updateStudent(Student newStudent) {
      Student oldStudent = studentDAO.get(newStudent.getId());
      if (oldStudent != null) {
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

   //       public InMemoryStudentDAO getStudentDAO() {
//   public JpaStudentDAO getStudentDAO() {
   public StudentDAO getStudentDAO() {
      return studentDAO;
   }

}
