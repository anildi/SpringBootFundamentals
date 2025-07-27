package ttl.larku.service;

import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Course;

@Service("courseService")
@Profile("! networkrating")
public class DefaultCourseService implements CourseService {

    private BaseDAO<Course> courseDAO;

    public DefaultCourseService(BaseDAO<Course> courseDAO) {
        this.courseDAO = courseDAO;
    }

    @Override
    public Course createCourse(String code, String title) {
        Course course = new Course(code, title);
        course = createCourse(course);

        return course;
    }

    @Override
    public Course createCourse(Course course) {
        course = courseDAO.insert(course);

        return course;
    }

    @Override
    public boolean deleteCourse(int id) {
            return courseDAO.delete(id);
    }

    @Override
    public boolean updateCourse(Course newCourse) {
            return courseDAO.update(newCourse);
    }

    @Override
    public Optional<Course> getCourseByCode(String code) {
        List<Course> courses = courseDAO.findBy(c -> c.getCode().contains(code));
        return !courses.isEmpty() ? Optional.of(courses.get(0)) : Optional.empty();
    }

    @Override
    public Optional<Course> getCourse(int id) {
        return courseDAO.findById(id);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseDAO.findAll();
    }

    @Override
    public BaseDAO<Course> getCourseDAO() {
        return courseDAO;
    }

    @Override
    public void setCourseDAO(BaseDAO<Course> courseDAO) {
        this.courseDAO = courseDAO;
    }

    @Override
    public void clear() {
        courseDAO.deleteStore();
        courseDAO.createStore();
    }
}
