package ttl.larku.service;

import java.util.List;
import java.util.Optional;
import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Course;

public class CourseService {

   private BaseDAO<Course> courseDAO;

   public CourseService() {

   }

   public CourseService(BaseDAO<Course> courseDAO) {
      this.courseDAO = courseDAO;
   }

   public Course createCourse(String code, String title) {
      Course course = new Course(code, title);
      course = courseDAO.insert(course);

      return course;
   }

   public Course createCourse(Course course) {
      course = courseDAO.insert(course);

      return course;
   }

   public boolean deleteCourse(int id) {
      return courseDAO.delete(id);
   }

   public boolean updateCourse(Course newCourse) {
      return courseDAO.update(newCourse);
   }

   public Course getCourseByCode(String code) {
      List<Course> courses = courseDAO.findBy(c -> c.getCode().equals(code));
      return courses.size() > 0 ? courses.get(0) : null;
   }

   public Optional<Course> getCourse(int id) {
      return courseDAO.findById(id);
   }

   public List<Course> getAllCourses() {
      return courseDAO.findAll();
   }

   public BaseDAO<Course> getCourseDAO() {
      return courseDAO;
   }

   public void setCourseDAO(BaseDAO<Course> courseDAO) {
      this.courseDAO = courseDAO;
   }

   public void clear() {
      courseDAO.deleteStore();
      courseDAO.createStore();
   }
}
