package ttl.larku.service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Student;
import ttl.larku.domain.Student.Status;
import ttl.larku.domain.StudentCreatedEvent;
import ttl.larku.service.props.ServiceThatWeDontOwn;

@Service
public class StudentService {

   private BaseDAO<Student> studentDAO;

   private ApplicationEventPublisher publisher;

   private ServiceThatWeDontOwn ctwdo;


   //Autowiring happening here
   public StudentService(BaseDAO<Student> studentDAO,
                         ApplicationEventPublisher publisher,
                         ServiceThatWeDontOwn ctwdo) {
      this.studentDAO = studentDAO;
      this.publisher = publisher;
      this.ctwdo = ctwdo;

   }


   public Student createStudent(String name) {
      Student student = new Student(name);
      student = studentDAO.insert(student);

      return student;
   }

   public Student createStudent(String name, String phoneNumber, Status status) {
      return createStudent(new Student(name, phoneNumber, status));
   }

   public Student createStudent(Student student) {
      student = studentDAO.insert(student);

      System.out.println("Going to publish in " + Thread.currentThread());
      publisher.publishEvent(new StudentCreatedEvent(this, student));

      return student;
   }

   public boolean deleteStudent(int id) {
      return studentDAO.delete(id);
   }

   public boolean updateStudent(Student newStudent) {
      return studentDAO.update(newStudent);
   }

   public Optional<Student> getStudent(int id) {
      Duration timeout = ctwdo.getTimeout();
      System.out.println("Timout is: " + timeout);
      return studentDAO.findById(id);
   }

   public List<Student> getAllStudents() {
      return studentDAO.findAll();
   }

   public List<Student> getByName(String name) {
      List<Student> result = studentDAO.findBy(s -> s.getName().contains(name));

      return result;
   }

   public BaseDAO<Student> getStudentDAO() {
      return studentDAO;
   }

   public void setStudentDAO(BaseDAO<Student> studentDAO) {
      this.studentDAO = studentDAO;
   }

   public void clear() {
      studentDAO.createStore();
   }

}
