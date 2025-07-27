package ttl.larku.search;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Path;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ttl.larku.dao.repository.StudentRepo;
import ttl.larku.domain.Student;
import ttl.larku.sql.SqlScriptBase;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class TestStudentRepoSearchByJpaSpecification extends SqlScriptBase {

   @PersistenceContext
   private EntityManager em;

   @Autowired
   private StudentRepo studentRepo;

   @Test
   public void testSearchWithSearchSpecification() {
      JpaTrackSearchSpec spec = new JpaTrackSearchSpec(JpaSearchSpecSupport.SearchType.ContainsString,
            "name", "Manoj");

      List<Student> result = studentRepo.findAll(spec);
      out.println("result: " + result.size());
      result.forEach(out::println);

      assertEquals(1, result.size());
   }

   @Test
   public void testSearchWithMultipleSearchSpecifications() {
      JpaTrackSearchSpec nameSpec = new JpaTrackSearchSpec(JpaSearchSpecSupport.SearchType.ContainsString,
            "name", "Manoj");

      JpaTrackSearchSpec statusSpec = new JpaTrackSearchSpec(JpaSearchSpecSupport.SearchType.Equal,
            "status", Student.Status.PART_TIME);

      List<Student> result = studentRepo.findAll(nameSpec.or(statusSpec));
      out.println("result: " + result.size());
      result.forEach(out::println);

      assertEquals(3, result.size());
   }

   @Test
   public void testSearchForCollectionAttributeForStudentsWithCustomSpec() {
      JpaTrackSearchSpec startDateSpec = new JpaTrackSearchSpec(JpaSearchSpecSupport.SearchType.Custom,
            "endDate", LocalDate.of(2023, 8, 10),
            (builder, root, propName, value) -> {
               Path<LocalDate> path = root.get("classes").get(propName);
               return builder.equal(path, value);
            });

      List<Student> result = studentRepo.findAll(startDateSpec);
      out.println("result: " + result.size());
      result.forEach(out::println);

      assertEquals(1, result.size());
   }

   @Test
   public void testSearchWithNestPropertiesForStudentsTheEasierWay() {
      JpaTrackSearchSpec artistSpec = new JpaTrackSearchSpec(JpaSearchSpecSupport.SearchType.Equal,
            "classes.endDate",LocalDate.of(2023, 8, 10));

      List<Student> result = studentRepo.findAll(artistSpec);
      out.println("result: " + result.size());
      result.forEach(out::println);

      assertEquals(1, result.size());
   }

   @Test
   public void testSearchForDobGreaterThan() {
      JpaTrackSearchSpec dobSpec = new JpaTrackSearchSpec(JpaSearchSpecSupport.SearchType.Greater,
            "dob", LocalDate.of(1960, 1, 1));

      List<Student> result = studentRepo.findAll(dobSpec);
      out.println("result: " + result.size());
      result.forEach(out::println);

      assertEquals(3, result.size());
   }

   @Test
   public void testForSize() {
      JpaTrackSearchSpec classesSizeSpec = new JpaTrackSearchSpec(JpaSearchSpecSupport.SearchType.Size,
            "classes", "2");

      List<Student> result = studentRepo.findAll(classesSizeSpec);
      out.println("result: " + result.size());
      result.forEach(out::println);
      assertEquals(1, result.size());
   }

   @Test
   public void testForNull() {
      JpaTrackSearchSpec dobIsNullSpec = new JpaTrackSearchSpec(JpaSearchSpecSupport.SearchType.Null,
            "dob", "");

      List<Student> result = studentRepo.findAll(dobIsNullSpec);
//      result.forEach(out::println);
      out.println("result: " + result.size());
      assertEquals(0, result.size());
   }
}
