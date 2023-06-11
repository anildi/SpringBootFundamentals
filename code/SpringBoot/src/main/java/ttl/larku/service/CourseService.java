package ttl.larku.service;

import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Course;

import java.util.List;

public class CourseService {

    private BaseDAO<Course> courseDAO;

    public Course createCourse(String code, String title) {
        Course course = new Course(code, title);
        course = createCourse(course);

        return course;
    }

    public Course createCourse(Course course) {
        course = courseDAO.create(course);

        return course;
    }

    public boolean deleteCourse(int id) {
        Course course = courseDAO.get(id);
        if (course != null) {
            courseDAO.delete(course);
            return true;
        }
        return false;
    }

    public boolean updateCourse(Course newCourse) {
        Course oldCourse = courseDAO.get(newCourse.getId());
        if(oldCourse != null) {
            courseDAO.update(newCourse);
            return true;
        }
        return false;
    }

    public Course getCourseByCode(String code) {
        List<Course> courses = courseDAO.findBy(c -> c.getCode().equals(code));
        return courses.size() > 0 ? courses.get(0) : null;
    }

    public Course getCourse(int id) {
        return courseDAO.get(id);
    }

    public List<Course> getAllCourses() {
        return courseDAO.getAll();
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
