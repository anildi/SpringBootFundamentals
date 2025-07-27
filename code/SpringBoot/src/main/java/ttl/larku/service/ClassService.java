package ttl.larku.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Course;
import ttl.larku.domain.ScheduledClass;

public class ClassService {

   private CourseService courseService;
   private BaseDAO<ScheduledClass> classDAO;


   public ScheduledClass createScheduledClass(String courseCode, LocalDate startDate, LocalDate endDate) {
      Optional<Course> opt = courseService.getCourseByCode(courseCode);
      if (opt.isPresent()) {
         Course course = opt.get();
         ScheduledClass sClass = new ScheduledClass(course, startDate, endDate);
         sClass = classDAO.insert(sClass);
         return sClass;
      }
      throw new IllegalArgumentException("Course not found: " + courseCode);
   }

   public boolean deleteScheduledClass(int id) {
         return classDAO.delete(id);
   }

   public boolean updateScheduledClass(ScheduledClass newClass) {
         return classDAO.update(newClass);
   }

   public List<ScheduledClass> getScheduledClasses(String code, LocalDate startDate, LocalDate endDate) {
      List<ScheduledClass> result = classDAO.findAll().stream()
            .filter(sc -> sc.getCourse().getCode().equals(code)
                  && sc.getStartDate().equals(startDate)
                  && sc.getEndDate().equals(endDate))
            .collect(Collectors.toList());

      return result;
   }

   public List<ScheduledClass> getScheduledClassesByCourseCode(String code) {
      List<ScheduledClass> result = classDAO.findAll().stream()
            .filter(sc -> sc.getCourse().getCode().equals(code))
            .collect(Collectors.toList());

      return result;
   }


   public Optional<ScheduledClass> getScheduledClass(int id) {
      return classDAO.findById(id);
   }

   public List<ScheduledClass> getAllScheduledClasses() {
      return classDAO.findAll();
   }

   public BaseDAO<ScheduledClass> getClassDAO() {
      return classDAO;
   }

   public void setClassDAO(BaseDAO<ScheduledClass> classDAO) {
      this.classDAO = classDAO;
   }

   public CourseService getCourseService() {
      return courseService;
   }

   public void setCourseService(CourseService courseService) {
      this.courseService = courseService;
   }

   public void clear() {
      classDAO.deleteStore();
      classDAO.createStore();
   }
}
