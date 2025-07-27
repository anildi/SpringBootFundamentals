package ttl.larku.dao.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ttl.larku.domain.Course;

@Repository
@Transactional
public interface CourseRepo extends JpaRepository<Course, Integer> {

    //@Query("select c from Course c where c.code = :code")
    //Will use @NamedQuery(Course.getByCode)
    public Optional<Course> getByCode(@Param("code") String code);
}
