package ttl.larku.service;

import org.springframework.transaction.annotation.Transactional;
import ttl.larku.domain.ScheduledClass;

import java.time.LocalDate;
import java.util.List;

@Transactional
public interface ClassService {
    ScheduledClass createScheduledClass(String courseCode, LocalDate startDate, LocalDate endDate);

    boolean deleteScheduledClass(int id);

    boolean updateScheduledClass(ScheduledClass course);

    List<ScheduledClass> getScheduledClassesByCourseCode(String code);

    List<ScheduledClass> getScheduledClassesByCourseCodeAndStartDate(String code, LocalDate startDate);

    ScheduledClass getScheduledClass(int id);

    List<ScheduledClass> getAllScheduledClasses();

    void clear();
}
