package ttl.larku.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ttl.larku.dao.repository.SimpleStudentRepo;
import ttl.larku.dao.repository.StudentRepo;
import ttl.larku.domain.Student;
import ttl.larku.domain.StudentCourseCodeSummary;
import ttl.larku.sql.SqlScriptBase;


//@DataJpaTest
@SpringBootTest
@Tag("dao")
@Transactional
public class StudentRepoPagingTest extends SqlScriptBase {

   private String name1 = "Bloke";
   private String name2 = "Blokess";
   private String newName = "Different Bloke";
   private Student student1;
   private Student student2;

   @Autowired
   private StudentRepo studentRepo;

   @Autowired
   private SimpleStudentRepo simple;

   @Autowired
   private ApplicationContext context;

   @BeforeEach
   public void setup() {

      student1 = new Student(name1);
      student2 = new Student(name2);
   }


   /**
    * Test Paging.
    */
   @Test
   public void testPaging() {
      // first add a bunch of student so we have something to page through
      //Our Transaction will get rolled back at the end, so no harm done.
      for (int i = 0; i < 50; i++) {
         Student s = new Student("Fake #" + i);
         studentRepo.save(s);
      }

      int currPage = 0;
      int size = 20;
      int totalElements = 0;
      //Set up sorting criteria
      Sort sort = Sort.by("name").descending();
      //Use the paging variation of the findAll method.
      Page<Student> page = null;
      do {
         page = studentRepo.findAll(PageRequest.of(currPage++, size, sort));
         totalElements += page.getNumberOfElements();
         System.out.println("Number: " + page.getNumber() + ", numElements: " + page.getNumberOfElements());
         page.forEach(System.out::println);
      } while (page.hasNext());

//      assertEquals(54, totalElements);
//      assertEquals(2, page.getNumber());
   }

   /**
    * An example of using a Pageable with a Projection
    */
   @Test
   public void testProjectionStudentClassCourseCode() {
      // first add a bunch of student so we have something to page through
      for (int i = 0; i < 50; i++) {
         Student s = new Student("Fake #" + i);
         studentRepo.save(s);
      }

      int currPage = 0;
      int size = 20;
      Sort sort = Sort.by("name").descending();
      Page<StudentCourseCodeSummary> page = studentRepo.findPageCourseCodeBy(PageRequest.of(currPage++, size, sort));
      System.out.println("Number: " + page.getNumber() + ", numElements: " + page.getNumberOfElements());
      dumpPage(page);
      while (page.hasNext()) {
         page = studentRepo.findPageCourseCodeBy(PageRequest.of(currPage++, size, sort));
         System.out.println("Number: " + page.getNumber() + ", numElements: " + page.getNumberOfElements());
         dumpPage(page);
      }

   }

   public void dumpPage(Page<StudentCourseCodeSummary> page) {
      page.forEach(sp -> {
         System.out.println(sp.getId() + ": " + sp.getName());
         sp.getClasses().forEach(s -> System.out.println("     " + s.getCourse() + ", " + s.getStartDate()));
      });

   }
}
