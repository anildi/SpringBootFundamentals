package ttl.larku.dao.inmemory;

import java.util.List;
import java.util.Optional;
import ttl.larku.domain.Student;

public interface StudentDAO {
   boolean update(Student updateObject);

   boolean delete(int id);

   boolean delete(Student student);

   Student create(Student newObject);

   Optional<Student> get(int id);

   List<Student> getAll();
}
