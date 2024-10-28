package ttl.larku.dao;

import java.util.List;
import ttl.larku.domain.Student;

public interface StudentDAO {
   boolean update(Student updateObject);

   boolean delete(Student student);

   Student create(Student newObject);

   Student get(int id);

   List<Student> getAll();
}
