package ttl.larku.service;

import org.springframework.transaction.annotation.Transactional;
import ttl.larku.domain.Course;

import java.util.List;

@Transactional
public interface CourseService {
    Course createCourse(String code, String title);

    Course createCourse(Course course);

    boolean deleteCourse(int id);

    boolean updateCourse(Course course);

    Course findByCode(String code);

    Course getCourse(int id);

    List<Course> getAllCourses();

    void clear();
}
