package ttl.larku.dao.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ttl.larku.domain.Student;

/**
 * @author whynot
 */
public interface StudentRepo extends JpaRepository<Student, Integer> {
}
