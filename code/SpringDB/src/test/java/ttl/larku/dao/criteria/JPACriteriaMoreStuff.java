package ttl.larku.dao.criteria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.query.ResultListTransformer;
import org.hibernate.query.TupleTransformer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ttl.larku.annotations.MyTestContainers;
import ttl.larku.domain.Course_;
import ttl.larku.domain.ScheduledClass_;
import ttl.larku.domain.Student;
import ttl.larku.domain.Student_;
import ttl.larku.sql.SqlScriptBase;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@Tag("expensive")
public class JPACriteriaMoreStuff extends SqlScriptBase {

   @Autowired
   private EntityManager em;

   @Test
   @Transactional
   public void testGetPiecesOfAStudentAsATuple() {

      //Get the builder
      CriteriaBuilder builder = em.getCriteriaBuilder();
      //Create a query that will return Students
      CriteriaQuery<Tuple> cq = builder.createQuery(Tuple.class);

      //Student is also going to be the (only) root entity we will
      //be searching from.  This need not always be the same as the
      //type returned from the query.  This is the 'From' clause.
      Root<Student> queryRoot = cq.from(Student.class);
      //no where clauses so we just do a select.
//      cq.select(builder.tuple(queryRoot.get(Student_.NAME), queryRoot.get(Student_.STATUS)));
      cq.select(builder.tuple(queryRoot.get(Student_.NAME), queryRoot.get(Student_.STATUS)));


      //And now run it
      TypedQuery<Tuple> runnableQuery = em.createQuery(cq);
      List<Tuple> students = runnableQuery.getResultList();
      assertEquals(4, students.size());

      for (Tuple t : students) {
         out.println("t: " + t.get(0, String.class) + ", " + t.get(1, Student.Status.class));
      }
   }


   record NameAndStatus(String name, Student.Status status) {
   }

   @Test
   @Transactional
   public void testGetPiecesOfAStudentAsADTO() {
      //Get the builder
      CriteriaBuilder builder = em.getCriteriaBuilder();
      //Create a query that will return Students
      CriteriaQuery<NameAndStatus> cq = builder.createQuery(NameAndStatus.class);

      //Student is also going to be the (only) root entity we will
      //be searching from.  This need not always be the same as the
      //type returned from the query.  This is the 'From' clause.
      Root<Student> queryRoot = cq.from(Student.class);
      //no where clauses so we just do a select.
//      cq.select(builder.tuple(queryRoot.get(Student_.NAME), queryRoot.get(Student_.STATUS)));
      cq.select(builder.construct(NameAndStatus.class, queryRoot.get(Student_.NAME), queryRoot.get(Student_.STATUS)));


      //And now run it
      TypedQuery<NameAndStatus> runnableQuery = em.createQuery(cq);
      List<NameAndStatus> students = runnableQuery.getResultList();
      assertEquals(4, students.size());

      for (NameAndStatus t : students) {
         out.println("t: " + t);
      }
   }

   record NameAndStatusAndCourseCode(String name, Student.Status status,
                                     String courseCode) {
   }

   @Test
   @Transactional
   public void testGetPiecesInADTOWithAJoin() {
      //Get the builder
      CriteriaBuilder builder = em.getCriteriaBuilder();
      //Create a query that will return Students
      CriteriaQuery<NameAndStatusAndCourseCode> cq = builder.createQuery(NameAndStatusAndCourseCode.class);

      //Student is also going to be the (only) root entity we will
      //be searching from.  This need not always be the same as the
      //type returned from the query.  This is the 'From' clause.
      Root<Student> queryRoot = cq.from(Student.class);

      //no where clauses so we just do a select.
//      cq.select(builder.tuple(queryRoot.get(Student_.NAME), queryRoot.get(Student_.STATUS)));
      cq.select(builder.construct(NameAndStatusAndCourseCode.class,
            queryRoot.get(Student_.NAME), queryRoot.get(Student_.STATUS),
            queryRoot.get(Student_.CLASSES).get(ScheduledClass_.COURSE).get(Course_.CODE)));


      //And now run it
      TypedQuery<NameAndStatusAndCourseCode> runnableQuery = em.createQuery(cq);
      List<NameAndStatusAndCourseCode> students = runnableQuery.getResultList();

      for (NameAndStatusAndCourseCode t : students) {
         out.println("t: " + t);
      }

      assertEquals(3, students.size());
   }

   record NameAndStatusAndCourseCodes(int id, String name, Student.Status status,
                                      List<String> courseCodes) {
      public NameAndStatusAndCourseCodes(int id, String name, Student.Status status) {
         this(id, name, status, new ArrayList<>());
      }
   }

   @Test
   @Transactional
   public void testGetPiecesInADTOWithACollectionProperty() {
      //Get the builder
      CriteriaBuilder builder = em.getCriteriaBuilder();
      //Create a query that will return Students
      CriteriaQuery<Object[]> cq = builder.createQuery(Object[].class);

      //Student is also going to be the (only) root entity we will
      //be searching from.  This need not always be the same as the
      //type returned from the query.  This is the 'From' clause.
      Root<Student> queryRoot = cq.from(Student.class);

      //no where clauses so we just do a select.
//      cq.select(builder.tuple(queryRoot.get(Student_.NAME), queryRoot.get(Student_.STATUS)));
      cq.select(builder.array(queryRoot.get(Student_.ID), queryRoot.get(Student_.NAME),
            queryRoot.get(Student_.STATUS),
            queryRoot.get(Student_.CLASSES).get(ScheduledClass_.COURSE).get(Course_.CODE)));


      //And now run it
      TypedQuery<Object[]> runnableQuery = em.createQuery(cq);
      List<Object[]> students = runnableQuery.getResultList();

      Map<Integer, NameAndStatusAndCourseCodes> result = new HashMap<>();


      for (Object[] row : students) {
         int id = (int) row[0];
         var currObj = result.get(id);
         if (currObj == null) {
            String name = (String) row[1];
            Student.Status status = (Student.Status) row[2];
            currObj = new NameAndStatusAndCourseCodes(id, name, status);
            result.put(id, currObj);
         }
         String code = (String) row[3];
         currObj.courseCodes.add(code);
      }

      result.forEach((k, v) -> out.println("k: " + k + ", v: " + v));

      assertEquals(2, result.size());
   }


   @Test
   public void testGetDTOWithCollectionUsingHibernateTricks() {
      //Get the builder
      CriteriaBuilder builder = em.getCriteriaBuilder();
      //Create a query that will return Students
      CriteriaQuery<Object[]> cq = builder.createQuery(Object[].class);

      //Student is also going to be the (only) root entity we will
      //be searching from.  This need not always be the same as the
      //type returned from the query.  This is the 'From' clause.
      Root<Student> queryRoot = cq.from(Student.class);

      //no where clauses so we just do a select.
//      cq.select(builder.tuple(queryRoot.get(Student_.NAME), queryRoot.get(Student_.STATUS)));
      cq.select(builder.array(queryRoot.get(Student_.ID), queryRoot.get(Student_.NAME),
            queryRoot.get(Student_.STATUS),
            queryRoot.get(Student_.CLASSES).get(ScheduledClass_.COURSE).get(Course_.CODE)));


      //Here we use a Hibernate specific mechanism to "unwrap"
      //the query, so we can do the transformation to the DTO
      //as the query is being run, rather than at the end.
      //But it sure is a lot more work.
      var transformer = new StudentToNameAndStatusDTO();
      TypedQuery<NameAndStatusAndCourseCodes> query =
            em.createQuery(cq)
                  .unwrap(org.hibernate.query.Query.class)
                  .setTupleTransformer(transformer)
                  .setResultListTransformer(transformer);

      List<NameAndStatusAndCourseCodes> result = query.getResultList();

      result.forEach(out::println);

      assertEquals(2, result.size());

   }

   class StudentToNameAndStatusDTO implements TupleTransformer<NameAndStatusAndCourseCodes>,
         ResultListTransformer<NameAndStatusAndCourseCodes> {

      Map<Integer, NameAndStatusAndCourseCodes> cWithP = new HashMap<>();

      @Override
      public NameAndStatusAndCourseCodes transformTuple(Object[] row, String[] aliases) {

         int id = (int) row[0];
         String name = (String) row[1];
         Student.Status status = (Student.Status) row[2];
         String code = (String) row[3];

         NameAndStatusAndCourseCodes twan = cWithP.computeIfAbsent(id, i -> {
            var tmp = new NameAndStatusAndCourseCodes(id, name, status);
            return tmp;
         });
         twan.courseCodes().add(code);

         return twan;
      }

      @Override
      public List<NameAndStatusAndCourseCodes> transformList(List<NameAndStatusAndCourseCodes> resultList) {
         return new ArrayList<>(cWithP.values());
      }
   }
}
