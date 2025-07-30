package ttl.larku.dao.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ttl.larku.domain.Student;

@Repository
public interface StudentRepo extends JpaRepository<Student, Integer> {

   List<Student> findByName(String name);
   List<Student> findByNameContaining(String name);
   List<Student> findByNameContainingIgnoreCase(String name);
}
