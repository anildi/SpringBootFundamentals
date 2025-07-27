package ttl.larku.dao;

import java.util.List;
import java.util.Optional;

public interface BaseDAO<T> {

    boolean update(T updateObject);

    boolean delete(int id);

    boolean delete(T student);

    T insert(T newObject);

    Optional<T> findById(int id);

    List<T> findAll();

}