package ttl.larku.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ttl.larku.domain.ScheduledClass;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public interface ClassRepo extends JpaRepository<ScheduledClass, Integer> {

    public List<ScheduledClass> getByCourseCode(String code);

    @Query("select distinct sc from ScheduledClass sc left join fetch sc.students where " +
            "sc.startDate = :startDate and sc.course.code = :code")
    public List<ScheduledClass> getByCourseCodeAndStartDateForStudents(String code, LocalDate startDate);

    @Query("select distinct sc from ScheduledClass sc where sc.startDate = :startDate and sc.course.code = :code")
    public List<ScheduledClass> getByCourseCodeAndStartDate(String code, String startDate);


}
