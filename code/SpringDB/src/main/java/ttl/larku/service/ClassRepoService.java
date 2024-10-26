package ttl.larku.service;

import ttl.larku.dao.repository.ClassRepo;
import ttl.larku.domain.Course;
import ttl.larku.domain.ScheduledClass;

import java.time.LocalDate;
import java.util.List;

public class ClassRepoService implements ClassService {

    private CourseService courseService;
    private ClassRepo classDAO;


    public ScheduledClass createScheduledClass(String courseCode, LocalDate startDate, LocalDate endDate) {
        Course course = courseService.findByCode(courseCode);
        if (course != null) {
            ScheduledClass sClass = new ScheduledClass(course, startDate, endDate);
            sClass = classDAO.save(sClass);
            return sClass;
        }
        return null;
    }

    public boolean deleteScheduledClass(int id) {
        ScheduledClass course = classDAO.findById(id).orElse(null);
        if (course != null) {
            classDAO.delete(course);
            return true;
        }
        return false;
    }

    public boolean updateScheduledClass(ScheduledClass newClass) {
        ScheduledClass oldClass = classDAO.findById(newClass.getId()).orElse(null);
        if (oldClass != null) {
            classDAO.save(newClass);
            return true;
        }
        return false;
    }

    public List<ScheduledClass> getScheduledClassesByCourseCode(String code) {
        List<ScheduledClass> result = classDAO.getByCourseCode(code);
        return result;
    }

    @Override
    public List<ScheduledClass> getScheduledClassesByCourseCodeAndStartDate(String code, LocalDate startDate) {
        List<ScheduledClass> result = classDAO.getByCourseCodeAndStartDateForStudents(code, startDate);
        return result;
    }

    public ScheduledClass getScheduledClass(int id) {
        return classDAO.findById(id).orElse(null);
    }

    public List<ScheduledClass> getAllScheduledClasses() {
        return classDAO.findAll();
    }

    public ClassRepo getClassDAO() {
        return classDAO;
    }

    public void setClassDAO(ClassRepo classDAO) {
        this.classDAO = classDAO;
    }

    public CourseService getCourseService() {
        return courseService;
    }

    public void setCourseService(CourseService courseService) {
        this.courseService = courseService;
    }

    public void clear() {
        classDAO.deleteAll();
    }
}
