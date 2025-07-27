//package ttl.larku.dao.specification;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jakarta.persistence.criteria.CriteriaBuilder;
//import jakarta.persistence.criteria.CriteriaQuery;
//import jakarta.persistence.criteria.JoinType;
//import jakarta.persistence.criteria.Path;
//import jakarta.persistence.criteria.Predicate;
//import jakarta.persistence.criteria.Root;
//import java.util.ArrayList;
//import java.util.List;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import ttl.larku.dao.repository.StudentRepo;
//import ttl.larku.domain.Student;
//
//import static java.lang.System.out;
//
//@SpringBootTest
//public class TestStudentRepoSearchCriteriaApi {
//
//   @PersistenceContext
//   private EntityManager em;
//
//   @Autowired
//   private StudentRepo studentRepo;
//
//   @Test
//   public void testGetExampleWithCriteria() {
//      //Student example = TrackEntity.album("Let Them Roll").build();
//      Student example = TrackEntity.artist("Herb Ellis").build();
//
//      List<Student> result = studentRepo.getByExampleWithCriteria(example);
//      out.println("result: " + result.size());
//      result.forEach(out::println);
//   }
//
//   @Test
//   public void testSearchByNestedProperty() {
//      //TrackEntity example = TrackEntity.album("Let Them Roll").build();
//      Student example = TrackEntity.artist("Herb Ellis").build();
//
//      //var nestedProp = "artists.name";
//      var nestedProp = "title";
//
//      List<Student> result = searchByNestedProp(nestedProp, "Remember");
//      out.println("result: " + result.size());
//      result.forEach(out::println);
//   }
//
//
//   public List<Student> searchByNestedProp(String nestedProp, String valueToTest) {
//
//      //Get the builder
//      CriteriaBuilder builder = em.getCriteriaBuilder();
//      //Create a query that will return Tracks
//      CriteriaQuery<Student> cq = builder.createQuery(Student.class);
//
//      //Tracks is also going to be the (only) root entity we will
//      //be searching from.  This need not always be the same as the
//      //type returned from the query.  This is the 'From' clause.
//      Root<Student> queryRoot = cq.from(Student.class);
//      //We are going to also get the artists
//      queryRoot.fetch("artists", JoinType.LEFT); //.fetch("course", JoinType.LEFT);
//
//      //This is the 'select' part of the query.
//      // We are going to be selecting Tracks.
//      cq.select(queryRoot).distinct(true);
//
//      //Build up a List of javax.persistence.criteria.Predicate objects,
//      //based on what is not null in the example Student.
//      List<Predicate> preds = new ArrayList<>();
//      //Here is how you can go into a collection valued attribute
//      //e.g. artists.name
//      var propParts = nestedProp.split("\\.");
//      Path<String> path = queryRoot.get(propParts[0]);
//      for (int i = 1; i < propParts.length; i++) {
//         path = path.get(propParts[i]);
//      }
//
//      preds.add(builder.like(builder.lower(path), "%" + valueToTest.toLowerCase() + "%"));
////      preds.add(builder.like(builder.lower(queryRoot.get("artists").get("name")), "%" + valueToTest.toLowerCase() + "%"));
//
//      //Now 'or' them together.
//      Predicate finalPred = builder.or(preds.toArray(new Predicate[0]));
//
//      //And set them as the where clause of our query.
//      cq.where(finalPred);
//
//      List<Student> result = em.createQuery(cq).getResultList();
//
//      return result;
//   }
//}
