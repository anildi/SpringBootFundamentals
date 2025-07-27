package ttl.larku.service;

import jakarta.persistence.TypedQuery;
import ttl.larku.dao.BaseDAO;
import ttl.larku.dao.jpahibernate.JPAClassDAO;
import ttl.larku.domain.Course;
import ttl.larku.domain.ScheduledClass;

import java.time.LocalDate;
import java.util.List;

public class ClassDaoService implements ClassService {

    private CourseService courseService;
    private JPAClassDAO classDAO;


    @Override
    public ScheduledClass createScheduledClass(String courseCode, LocalDate startDate, LocalDate endDate) {
        Course course = courseService.findByCode(courseCode);
        if (course != null) {
            ScheduledClass sClass = new ScheduledClass(course, startDate, endDate);
            sClass = classDAO.insert(sClass);
            return sClass;
        }
        return null;
    }

    @Override
    public boolean deleteScheduledClass(int id) {
        ScheduledClass oldClass = classDAO.findById(id);
        if (oldClass != null) {
            classDAO.delete(oldClass);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateScheduledClass(ScheduledClass newClass) {
        ScheduledClass oldClass = classDAO.findById(newClass.getId());
        if(oldClass != null) {
            classDAO.update(newClass);
            return true;
        }
        return false;
    }

    @Override
    public List<ScheduledClass> getScheduledClassesByCourseCode(String code) {
        List<ScheduledClass> result = classDAO.getByCourseCode(code);

        return result;
    }

    @Override
    public List<ScheduledClass> getScheduledClassesByCourseCodeAndStartDate(String code, LocalDate startDate) {
        List<ScheduledClass> result = classDAO.getByCourseCodeAndStartDateForStudents(code, startDate);
        return result;
    }

    @Override
    public ScheduledClass getScheduledClass(int id) {
        return classDAO.findById(id);
    }

    @Override
    public List<ScheduledClass> getAllScheduledClasses() {
        return classDAO.findAll();
    }

    public BaseDAO<ScheduledClass> getClassDAO() {
        return classDAO;
    }

    public void setClassDAO(JPAClassDAO classDAO) {
        this.classDAO = classDAO;
    }

    public CourseService getCourseService() {
        return courseService;
    }

    public void setCourseService(CourseService courseService) {
        this.courseService = courseService;
    }

    @Override
    public void clear() {
        classDAO.deleteStore();
        classDAO.createStore();
    }
}
