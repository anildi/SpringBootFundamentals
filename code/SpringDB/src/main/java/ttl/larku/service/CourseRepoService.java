package ttl.larku.service;

import org.springframework.beans.factory.annotation.Autowired;
import ttl.larku.dao.repository.CourseRepo;
import ttl.larku.domain.Course;

import java.util.List;

public class CourseRepoService implements CourseService {

    @Autowired
    private CourseRepo courseRepo;

    public Course createCourse(String code, String title) {
        Course course = new Course(code, title);
        course = courseRepo.save(course);

        return course;
    }

    public Course createCourse(Course course) {
        course = courseRepo.save(course);

        return course;
    }

    public boolean deleteCourse(int id) {
        Course course = courseRepo.findById(id).orElse(null);
        if (course != null) {
            courseRepo.delete(course);
            return true;
        }
        return false;
    }

    public boolean updateCourse(Course newCourse) {
        Course oldCourse = courseRepo.findById(newCourse.getId()).orElse(null);
        if(oldCourse != null) {
            courseRepo.save(newCourse);
            return true;
        }
        return false;
    }

    public Course findByCode(String code) {
        Course course = courseRepo.getByCode(code).orElse(null);
        return course;
    }

    public Course getCourse(int id) {
        return courseRepo.findById(id).orElse(null);
    }

    public List<Course> getAllCourses() {
        return courseRepo.findAll();
    }

    public CourseRepo getCourseRepo() {
        return courseRepo;
    }

    public void setCourseRepo(CourseRepo courseRepo) {
        this.courseRepo = courseRepo;
    }

    public void clear() {
        courseRepo.deleteAll();
        //courseDAO.createStore();
    }
}
