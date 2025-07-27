package ttl.larku.search;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.JoinType;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import ttl.larku.dao.repository.StudentRepo;
import ttl.larku.domain.Student;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class TestStudentRepoSearchBySpecificationDirectly {

   @PersistenceContext
   private EntityManager em;

   @Autowired
   private StudentRepo studentRepo;

   @Test
   public void testByNameWithSearchSpecification() {
      Specification<Student> byName = (root, query, builder) -> {
         if (Long.class != query.getResultType() && long.class != query.getResultType()) {
            root.fetch("classes", JoinType.LEFT);
         }
         return builder.like(root.get("name"), "%Manoj%");
      };

      List<Student> result = studentRepo.findAll(byName);
      out.println("result: " + result);

      assertEquals(1, result.size());
   }

   @Test
   public void testByStatusWithSearchSpecification() {
      Specification<Student> byStatus = (root, query, builder) -> {
         if (Long.class != query.getResultType() && long.class != query.getResultType()) {
            root.fetch("classes", JoinType.LEFT);
         }
         return builder.equal(root.get("status"), Student.Status.HIBERNATING);
      };

      List<Student> result = studentRepo.findAll(byStatus);
      out.println("result: " + result);
      assertEquals(1, result.size());
   }

   @Test
   public void testByClassesByEndDateWithSearchSpecification() {
      Specification<Student> byEndDate = (root, query, builder) -> {
         if (Long.class != query.getResultType() && long.class != query.getResultType()) {
            root.fetch("classes", JoinType.LEFT);
         }
         return builder.equal(root.get("classes").get("endDate"), LocalDate.of(2023, 8, 10));
      };

      List<Student> result = studentRepo.findAll(byEndDate);
      out.println("result: " + result);
      assertEquals(1, result.size());
   }

   @Test
   public void testSearchWithMultipleSearchSpecifications() {
      Specification<Student> byName = (root, query, builder) -> {
         if (Long.class != query.getResultType() && long.class != query.getResultType()) {
            root.fetch("classes", JoinType.LEFT);
         }
         return builder.like(root.get("name"), "%Manoj%");
      };

      Specification<Student> byStatus = (root, query, builder) -> {
         if (Long.class != query.getResultType() && long.class != query.getResultType()) {
            root.fetch("classes", JoinType.LEFT);
         }
         return builder.equal(root.get("status"), Student.Status.HIBERNATING);
      };

      List<Student> result = studentRepo.findAll(byName.or(byStatus));
      out.println("result: " + result.size());
      result.forEach(out::println);

      assertEquals(2, result.size());
   }
}
