package ttl.larku.search;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;


public class JpaPartialTrackSearchSpec implements Specification<Object []> {

   public final JpaSearchSpecSupport.SearchType searchType;
   public final Object value;
   public final String propName;

   public JpaPartialTrackSearchSpec(JpaSearchSpecSupport.SearchType searchType, String propName, Object value) {
      this.searchType = searchType;
      this.propName = propName;
      this.value = value;
   }

   @Override
   public Predicate toPredicate(Root<Object []> root, CriteriaQuery<?> query,
                                CriteriaBuilder criteriaBuilder) {
      if (searchType.needsComparable && !(value instanceof Comparable)) {
         throw new RuntimeException(searchType + " can only be applied to Comparables");
      }

      return searchType.makeIt(criteriaBuilder, root, propName, value);
   }
}
