package ttl.larku.service.reg.integcustomcontext.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ttl.larku.domain.Course;
import ttl.larku.service.CourseService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//@AutoConfigureMockMvc
//@SpringBootTest(classes = {LarkUConfig.class, LarkUTestDataConfig.class, StudentService.class, ConnectionService.class, ConnectionServiceProperties.class})
//@SpringBootTest(classes = {MyTestConfig.class})
@Tag("integration")
public class CourseServiceTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private ListableBeanFactory context;

    @BeforeEach
    public void setup() {
        courseService.clear();
//        for(String name: context.getBeanDefinitionNames()) {
//            System.out.println(name);
//        }
//        System.out.println(getClass().getName() + ":" + context.getBeanDefinitionCount() + " beans");
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

        courseService.deleteCourse(course1.getId());

        assertEquals(1, courseService.getAllCourses().size());
        assertTrue(courseService.getAllCourses().get(0).getCode().contains("Phys-101"));
    }

    @Test
    public void testDeleteNonExistentCourse() {
        Course course1 = courseService.createCourse("Math-101", "Intro to Math");
        Course course2 = courseService.createCourse("Phys-101", "Intro to Physics");

        assertEquals(2, courseService.getAllCourses().size());

        //Non existent Id
        courseService.deleteCourse(9999);

        assertEquals(2, courseService.getAllCourses().size());
    }

    @Test
    public void testUpdateCourse() {
        Course course1 = courseService.createCourse("Math-101", "Intro to Math");

        assertEquals(1, courseService.getAllCourses().size());

        course1.setCode("Math-202");
        courseService.updateCourse(course1);

        List<Course> courses = courseService.getAllCourses();

        assertEquals(1, courses.size());
        assertTrue(courses.get(0).getCode().contains("Math-202"));
    }

    @Test
    public void testGetByCode() {
        Course course1 = courseService.createCourse("Math-101", "Intro to Math");

        assertEquals(1, courseService.getAllCourses().size());

        Course math101 = courseService.getCourseByCode("Math-101");

        assertNotNull(math101);
    }

    @Test
    public void testGetByNonExistentCode() {
        Course course1 = courseService.createCourse("Math-101", "Intro to Math");

        assertEquals(1, courseService.getAllCourses().size());

        Course math101 = courseService.getCourseByCode("Not There");

        assertNull(math101);
    }
}
