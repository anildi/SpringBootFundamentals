package ttl.larku.service;

import ttl.larku.dao.inmemory.InMemoryCourseDAO;
import ttl.larku.domain.Course;

import java.util.List;

public class CourseService {

    private InMemoryCourseDAO courseDAO;

    public CourseService() {
        courseDAO = new InMemoryCourseDAO();
    }

    public Course createCourse(String code, String title) {
        Course course = new Course(code, title);
        course = courseDAO.create(course);

        return course;
    }

    public Course createCourse(Course course) {
        course = courseDAO.create(course);

        return course;
    }

    public void deleteCourse(int id) {
        Course course = courseDAO.get(id);
        if (course != null) {
            courseDAO.delete(course);
        }
    }

    public void updateCourse(Course course) {
        courseDAO.update(course);
    }

    public Course getCourseByCode(String code) {
        List<Course> courses = courseDAO.getAll();
        for (Course course : courses) {
            if (course.getCode().equals(code)) {
                return course;
            }
        }
        return null;
    }

    public Course getCourse(int id) {
        return courseDAO.get(id);
    }

    public List<Course> getAllCourses() {
        return courseDAO.getAll();
    }

    public InMemoryCourseDAO getCourseDAO() {
        return courseDAO;
    }

    public void setCourseDAO(InMemoryCourseDAO courseDAO) {
        this.courseDAO = courseDAO;
    }
}
