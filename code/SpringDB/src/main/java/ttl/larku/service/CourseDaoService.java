package ttl.larku.service;

import java.util.List;
import ttl.larku.dao.BaseDAO;
import ttl.larku.dao.CourseDAO;
import ttl.larku.domain.Course;

public class CourseDaoService implements CourseService {

//    private JPACourseDAO courseDAO;
//    private BaseDAO<Course> courseDAO;
    private CourseDAO courseDAO;

    @Override
    public Course createCourse(String code, String title) {
        Course course = new Course(code, title);
        course = courseDAO.insert(course);

        return course;
    }

    @Override
    public Course createCourse(Course course) {
        course = courseDAO.insert(course);

        return course;
    }

    @Override
    public boolean deleteCourse(int id) {
        Course course = courseDAO.findById(id);
        if (course != null) {
            courseDAO.delete(course);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateCourse(Course newCourse) {
        Course oldCourse = courseDAO.findById(newCourse.getId());
        if(oldCourse != null) {
            courseDAO.update(newCourse);
            return true;
        }
        return false;
    }

    @Override
    public Course findByCode(String code) {
        Course course = courseDAO.findByCode(code);
        return course;
    }

    @Override
    public Course getCourse(int id) {
        return courseDAO.findById(id);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseDAO.findAll();
    }

    public BaseDAO<Course> getCourseDAO() {
        return courseDAO;
    }

//    public void setCourseDAO(BaseDAO<Course> courseDAO) {
    public void setCourseDAO(CourseDAO courseDAO) {
        this.courseDAO = courseDAO;
    }

    @Override
    public void clear() {
        courseDAO.deleteStore();
        courseDAO.createStore();
    }
}
