package ttl.larku.search;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import ttl.larku.domain.Student;

public class JpaTrackSearchSpec implements Specification<Student> {
   public JpaSearchPredicateBuilder<Student> predFunction;
   public final JpaSearchSpecSupport.SearchType searchType;
   public final Object value;
   public final String propName;

   public final static JpaTrackSearchSpec ALL = new JpaTrackSearchSpec(JpaSearchSpecSupport.SearchType.All, "", "");

   public JpaTrackSearchSpec(JpaSearchSpecSupport.SearchType searchType, String propName, Object value) {
      this.searchType = searchType;
      this.propName = propName;
      this.value = value;
   }

   public JpaTrackSearchSpec(JpaSearchSpecSupport.SearchType searchType, String propName, Object value,
                             JpaSearchPredicateBuilder<Student> predFunction) {
      this.searchType = searchType;
      this.propName = propName;
      this.value = value;
      this.predFunction = predFunction;
   }


   /**
    * This will be called by
    *
    * @param root            must not be {@literal null}.
    * @param query           must not be {@literal null}.
    * @param criteriaBuilder must not be {@literal null}.
    * @return
    */
   @Override
   public Predicate toPredicate(Root<Student> root, CriteriaQuery<?> query,
                                CriteriaBuilder criteriaBuilder) {
      if (searchType.needsComparable && !(value instanceof Comparable)) {
         throw new RuntimeException(searchType + " can only be applied to Comparables");
      }
      //CHANGE ME
      //If it's not a count, then join with any related Things you want to Fetch, to
      //avoid LazyInstantionExceptions
      if (Long.class != query.getResultType() && long.class != query.getResultType()) {
         root.fetch("classes", JoinType.LEFT).fetch("course", JoinType.LEFT);
      }

      if(searchType == JpaSearchSpecSupport.SearchType.Custom) {
         if(predFunction == null) {
            throw new RuntimeException(searchType + ": SearchType is Custom but Predicate Builder function is null");
         }
         return predFunction.makeIt(criteriaBuilder, root, propName, value);
      }
      return searchType.makeIt(criteriaBuilder, root, propName, value);
   }
}
