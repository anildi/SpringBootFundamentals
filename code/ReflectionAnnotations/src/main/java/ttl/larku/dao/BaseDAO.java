package ttl.larku.dao;

import java.util.List;

public interface BaseDAO<T> {

	boolean update(T updateObject);

	boolean delete(T student);

	T create(T newObject);

	T get(int id);

	List<T> getAll();
}
