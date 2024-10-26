package ttl.larku.dao;

import java.util.List;
import java.util.Map;

import ttl.larku.domain.Student;

public interface BaseDAO<T> {

    void update(T updateObject);

    void delete(T student);

    T create(T newObject);

    T get(int id);

    List<T> getAll();

}