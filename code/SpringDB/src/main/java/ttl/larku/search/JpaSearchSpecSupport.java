package ttl.larku.search;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Collection;

/**
 * @author whynot
 */
public class JpaSearchSpecSupport<T> {

    public enum Operation {
        And,
        Or
    }

    //Very basic comparison operators.
    //Put these symbols before the property in the map
    //e.g. e.title=Autumn in Vermont compares title with equality
    //e.g. c.album=Remember compares album name with contains
    //Note, the non string properties will only work if invoked
    //from a Java Test.  In particular, we are NOT doing any conversions
    // on the values coming in over the web.

    //e.  equal
    //!.  not equal
    //l.  less than
    //L.  Less than or equal
    //g.  Greater than
    //G.  Greater than or equal
    //c.  contains
    //C.  contains ignore case
    //x.  Custom

    public enum SearchType {
        Equal(false, "e") {
            @Override
            public <T> Predicate makeIt(CriteriaBuilder cb, Root<T> root, String propName, Object value) {
                var path = makePropertyPath(root, propName, Object.class);
                //return cb.equal(root.get(propName), value);
                return cb.equal(path, value);
            }
        },
        NotEqual(false, "!") {
            @Override
            public <T> Predicate makeIt(CriteriaBuilder cb, Root<T> root, String propName, Object value) {
                var path = makePropertyPath(root, propName, Object.class);
                return cb.notEqual(path, value);
//                return cb.notEqual(root.get(propName), value);
            }
        },
        Greater(true, "g") {
            @SuppressWarnings({"unchecked", "rawtypes"})
            public <T> Predicate makeIt(CriteriaBuilder cb, Root<T> root, String propName, Object value) {
                var path = makePropertyPath(root, propName, Comparable.class);
                return cb.greaterThan(path, (Comparable) value);
//                return cb.greaterThan(root.get(propName), (Comparable) value);
            }
        },
        GreaterEqual(true, "G") {
            @Override
            @SuppressWarnings({"unchecked", "rawtypes"})
            public <T> Predicate makeIt(CriteriaBuilder cb, Root<T> root, String propName, Object value) {
                var path = makePropertyPath(root, propName, Comparable.class);
                return cb.greaterThanOrEqualTo(path, (Comparable) value);
//                return cb.greaterThanOrEqualTo(root.get(propName), (Comparable) value);
            }
        },
        Less(true, "l"){
            @Override
            @SuppressWarnings({"unchecked", "rawtypes"})
            public <T> Predicate makeIt(CriteriaBuilder cb, Root<T> root, String propName, Object value) {
                var path = makePropertyPath(root, propName, Comparable.class);
                return cb.lessThan(path, (Comparable) value);
//                return cb.lessThan(root.get(propName), (Comparable) value);
            }
        },
        LessEqual(true, "L"){
            @Override
            @SuppressWarnings({"unchecked", "rawtypes"})
            public <T> Predicate makeIt(CriteriaBuilder cb, Root<T> root, String propName, Object value) {
                var path = makePropertyPath(root, propName, Comparable.class);
                return cb.lessThanOrEqualTo(path, (Comparable) value);
//                return cb.lessThanOrEqualTo(root.get(propName), (Comparable) value);
            }
        },
        ContainsString(false, "c") {
            @Override
            public <T> Predicate makeIt(CriteriaBuilder cb, Root<T> root, String propName, Object value) {
                var path = makePropertyPath(root, propName, String.class);
                return cb.like(path, "%" + value.toString() + "%");
//                return cb.like(root.get(propName), "%" + value.toString() + "%");
            }
        },
        ContainsStringIC(false, "C") {
            @Override
            public <T> Predicate makeIt(CriteriaBuilder cb, Root<T> root, String propName, Object value) {
                var path = makePropertyPath(root, propName, String.class);
                return cb.like(cb.lower(path), "%" + value.toString().toLowerCase() + "%");
//                return cb.like(cb.lower(root.get(propName)), "%" + value.toString().toLowerCase() + "%");
            }
        },
        Size(false, "s") {  //size equals
            @Override
            public <T> Predicate makeIt(CriteriaBuilder cb, Root<T> root, String propName, Object value) {
                var path = makePropertyPath(root, propName, Collection.class);
                return cb.equal(cb.size(path), Integer.parseInt(value.toString()));
            }
        },
        Null(false, "n") {  //Null
            @Override
            public <T> Predicate makeIt(CriteriaBuilder cb, Root<T> root, String propName, Object value) {
                var path = makePropertyPath(root, propName, Object.class);
                return cb.isNull(path);
            }
        },
        NotNull(false, "N") {  //Not Null
            @Override
            public <T> Predicate makeIt(CriteriaBuilder cb, Root<T> root, String propName, Object value) {
                var path = makePropertyPath(root, propName, Object.class);
                return cb.isNotNull(path);
            }
        },
        All(false, "") {
            @Override
            public <T> Predicate makeIt(CriteriaBuilder cb, Root<T> root, String propName, Object value) {
//                var path = makePropertyPath(root, propName, String.class);
                return cb.conjunction();
            }
        },
        Custom(false, "x");


        public <T, X> Path<X> makePropertyPath(Root<T> root, String propName, Class<X> clazz) {
            var propParts = propName.split("\\.");
            Path<X> path = root.get(propParts[0]);
            for (int i = 1; i < propParts.length; i++) {
                path = path.get(propParts[i]);
            }

            return path;
        }

        public final String symbol;
        public final boolean needsComparable;
        SearchType(boolean needsComparable, String symbol) {
            this.needsComparable = needsComparable;
            this.symbol = symbol;
        }

        public static SearchType getBySymbol(String symbol) {
            for(SearchType st : SearchType.values()) {
                if(st.symbol.equals(symbol)) {
                    return st;
                }
            }
            return null;
        }

        public <T> Predicate makeIt(CriteriaBuilder cb, Root<T> root, String propName, Object value) {
            return cb.or();   //always false
        }
    }

    public final SearchType searchType;
    public final T value;
    public final String propName;


    public JpaSearchSpecSupport(SearchType searchType, String propName, T value) {
        this.searchType = searchType;
        this.propName = propName;
        this.value = value;
    }

    public Predicate makePredicate(CriteriaBuilder cb, Root<T> root) {
        if (searchType.needsComparable && !(value instanceof Comparable)) {
            throw new RuntimeException(searchType + " can only be applied to Comparables");
        }
        return searchType.makeIt(cb, root, propName, value);
    }

    @Override
    public String toString() {
        return "SearchSpec{" +
                "searchType=" + searchType +
                ", propName='" + propName + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
