package ttl.larku.service;

import java.util.List;
import java.util.Optional;
import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Course;

public interface CourseService {
   Course createCourse(String code, String title);

   Course createCourse(Course course);

   boolean deleteCourse(int id);

   boolean updateCourse(Course newCourse);

   Optional<Course> getCourseByCode(String code);

   Optional<Course> getCourse(int id);

   List<Course> getAllCourses();

   BaseDAO<Course> getCourseDAO();

   void setCourseDAO(BaseDAO<Course> courseDAO);

   void clear();
}
