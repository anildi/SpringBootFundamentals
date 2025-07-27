package ttl.larku.search;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@FunctionalInterface
public interface JpaSearchPredicateBuilder<T> {
   public Predicate makeIt(CriteriaBuilder cb, Root<T> root,
                           String propName, Object value);
}
