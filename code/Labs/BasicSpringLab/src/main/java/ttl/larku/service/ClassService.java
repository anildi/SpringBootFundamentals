package ttl.larku.service;

import java.util.ArrayList;
import java.util.List;

import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Course;
import ttl.larku.domain.ScheduledClass;

public class ClassService {

    private CourseService courseService;
    private BaseDAO<ScheduledClass> classDAO;


    public ScheduledClass createScheduledClass(String courseCode, String startDate, String endDate) {
        Course course = courseService.getCourseByCode(courseCode);
        if (course != null) {
            ScheduledClass sClass = new ScheduledClass(course, startDate, endDate);
            sClass = classDAO.create(sClass);
            return sClass;
        }
        return null;
    }

    public void deleteScheduledClass(int id) {
        ScheduledClass course = classDAO.get(id);
        if (course != null) {
            classDAO.delete(course);
        }
    }

    public void updateScheduledClass(ScheduledClass course) {
        classDAO.update(course);
    }

    public List<ScheduledClass> getScheduledClassesByCourseCode(String code) {
        List<ScheduledClass> result = new ArrayList<ScheduledClass>();
        for (ScheduledClass sc : classDAO.getAll()) {
            if (sc.getCourse().getCode().equals(code)) {
                result.add(sc);
            }
        }

        return result;
    }

    public ScheduledClass getScheduledClass(int id) {
        return classDAO.get(id);
    }

    public List<ScheduledClass> getAllScheduledClasses() {
        return classDAO.getAll();
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
