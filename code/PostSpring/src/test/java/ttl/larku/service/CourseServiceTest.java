package ttl.larku.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ttl.larku.domain.Course;
import ttl.larku.jconfig.LarkUConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = LarkUConfig.class)
@ActiveProfiles({"development"})
public class CourseServiceTest {

    @Autowired
    private CourseService courseService;

    @BeforeEach
    public void setup() {
        courseService.clear();
    }

    @Test
    public void testCreateCourse() {
        Course course = courseService.createCourse("Math-101", "Intro to Math");

        Course result = courseService.getCourse(course.getId());

        assertTrue(result.getCode().contains("Math-101"));
        assertEquals(1, courseService.getAllCourses().size());
    }

    @Test
    public void testDeleteCourse() {
        Course course1 = courseService.createCourse("Math-101", "Intro to Math");
        Course course2 = courseService.createCourse("Phys-101", "Intro to Physics");

        assertEquals(2, courseService.getAllCourses().size());

        boolean result = courseService.deleteCourse(course1.getId());
        assertTrue(result);

        assertEquals(1, courseService.getAllCourses().size());
        assertTrue(courseService.getAllCourses().get(0).getCode().contains("Phys-101"));
    }

    @Test
    public void testDeleteNonExistentCourse() {
        Course course1 = courseService.createCourse("Math-101", "Intro to Math");
        Course course2 = courseService.createCourse("Phys-101", "Intro to Physics");

        assertEquals(2, courseService.getAllCourses().size());

        //Non existent Id
        boolean result = courseService.deleteCourse(9999);
        assertFalse(result);

        assertEquals(2, courseService.getAllCourses().size());
    }

    @Test
    public void testUpdateCourse() {
        Course course1 = courseService.createCourse("Math-101", "Intro to Math");

        assertEquals(1, courseService.getAllCourses().size());

        course1.setCode("Math-202");
        boolean result = courseService.updateCourse(course1);
        assertTrue(result);

        List<Course> courses = courseService.getAllCourses();

        assertEquals(1, courses.size());
        assertTrue(courses.get(0).getCode().contains("Math-202"));
    }

    @Test
    public void testUpdateNonExistentCourse() {
        Course course1 = courseService.createCourse("Math-101", "Intro to Math");
        assertEquals(1, courseService.getAllCourses().size());

        course1.setCode("Math-202");
        course1.setId(9999);
        boolean result = courseService.updateCourse(course1);
        assertFalse(result);

        List<Course> courses = courseService.getAllCourses();

        assertEquals(1, courses.size());
    }
}
