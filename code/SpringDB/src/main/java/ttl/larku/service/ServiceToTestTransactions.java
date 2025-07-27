package ttl.larku.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Student;

@Service
public class ServiceToTestTransactions {

   @Autowired
   private BaseDAO<Student> studentDAO;

   @Transactional
   public Student addStudentGood() {
      Student student = new Student(LocalTime.now().toString(), "383 9339", LocalDate.of(2000, 10, 10), Student.Status.PART_TIME);

      Student newStudent = studentDAO.insert(student);

      System.out.println(newStudent);

//      throw new MyException();
//      throw new RuntimeException();
      return newStudent;
   }


   @Transactional
   public Student addStudentApplicationException() throws Exception {
      Student student = new Student(LocalTime.now().toString(), "383 9339", LocalDate.of(2000, 10, 10), Student.Status.PART_TIME);

      Student newStudent = studentDAO.insert(student);

      System.out.println(newStudent);

      throw new Exception("Application Exception from STT");
//      throw new RuntimeException();
   }

   @Transactional
   public Student addStudentRuntimeException() {
      Student student = new Student(LocalTime.now().toString(), "383 9339", LocalDate.of(2000, 10, 10), Student.Status.PART_TIME);

      Student newStudent = studentDAO.insert(student);

      System.out.println(newStudent);

//      throw new Exception("Application Exception from STT");
      throw new RuntimeException("Runtime Exception from STT");
   }

   public class MyApplicationExceptionIWantToRollBackFor extends Exception {

   }

   @Transactional(rollbackFor = {MyApplicationExceptionIWantToRollBackFor.class})
   public Student addStudentRollBackForSpecifiedApplicationExceptions() throws MyApplicationExceptionIWantToRollBackFor {
      Student student = new Student(LocalTime.now().toString(), "383 9339", LocalDate.of(2000, 10, 10), Student.Status.PART_TIME);

      Student newStudent = studentDAO.insert(student);

      System.out.println(newStudent);

      throw new MyApplicationExceptionIWantToRollBackFor();
   }

   @PersistenceUnit
   private EntityManagerFactory factory;

   @Transactional(propagation = Propagation.NOT_SUPPORTED)
   public Student addStudentWithOurOwnTransaction() throws MyApplicationExceptionIWantToRollBackFor {
      try (EntityManager em = factory.createEntityManager()) {
         em.getTransaction().begin();

         Student newStudent = new Student(LocalTime.now().toString(), "383 9339", LocalDate.of(2000, 10, 10), Student.Status.PART_TIME);

         //Student newStudent = studentDAO.insert(student);
         em.persist(newStudent);

         System.out.println(newStudent);

         if (ThreadLocalRandom.current().nextInt(10) > 5) {
            em.getTransaction().rollback();
            throw new MyApplicationExceptionIWantToRollBackFor();
         } else {
            em.getTransaction().commit();
         }

         return newStudent;
      }
   }

   @PersistenceContext
   private EntityManager theirEntityManager;

   @Transactional
   public Student addStudentWithContainerEntityManager() throws MyApplicationExceptionIWantToRollBackFor {

      TransactionInterceptor.currentTransactionStatus().setRollbackOnly();

      Student newStudent = new Student(LocalTime.now().toString(), "383 9339", LocalDate.of(2000, 10, 10), Student.Status.PART_TIME);

      newStudent = studentDAO.insert(newStudent);

      System.out.println(newStudent);

      return newStudent;
   }
}
