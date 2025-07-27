package ttl.larku.dao;

import java.util.List;
import java.util.function.Predicate;
import org.springframework.lang.Nullable;

import static java.util.stream.Collectors.toList;

/**
 * @param <T>
 * @author anil
 */
public interface BaseDAO<T> {

    public boolean update(T updateObject);

    public boolean delete(T deleteObject);

    public T insert(T newObject);

    @Nullable
    public T findById(int id);

    public List<T> findAll();

    public void deleteStore();

    public void createStore();

    default public List<T> findBy(Predicate<T> pred) {
        List<T> x = findAll();
        List<T> result = findAll().stream()
                .filter(pred)
                .collect(toList());
        return result;
    }

    default boolean exists(int id) {
        throw new UnsupportedOperationException("Needs implementing");
    }
}
