package ttl.larku.service.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ttl.larku.dao.jpahibernate.JPACourseDAO;
import ttl.larku.domain.Course;
import ttl.larku.service.CourseDaoService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.never;


@ExtendWith(MockitoExtension.class)
@Tag("unit")
public class CourseServiceUnitTest {

    @InjectMocks
    private CourseDaoService courseService;

    @Mock
    private JPACourseDAO courseDAO;

    @Mock
    private List<Course> allCourses;

    @BeforeEach
    public void setup() {
    }

    @Test
    public void testCreateCourse() {
        Course c1 = new Course("Math-101", "Intro to Math");
        c1.setId(1);

        Mockito.when(courseDAO.create(c1)).thenReturn(c1);
        Mockito.when(courseDAO.get(1)).thenReturn(c1);

        Course course = courseService.createCourse("Math-101", "Intro to Math");

        Course result = courseService.getCourse(course.getId());

        assertTrue(result.getCode().contains("Math-101"));

        Mockito.verify(courseDAO).create(c1);
        Mockito.verify(courseDAO).get(1);
    }

    @Test
    public void testDeleteCourse() {
        Course c1 = new Course("Math-101", "Intro to Math");
        c1.setId(1);

        Mockito.when(courseDAO.get(1)).thenReturn(c1);
        Mockito.when(courseDAO.delete(c1)).thenReturn(true);

        boolean result = courseService.deleteCourse(1);
        assertTrue(result);

        Mockito.verify(courseDAO, atLeastOnce()).get(1);
        Mockito.verify(courseDAO, atLeastOnce()).delete(c1);
    }

    @Test
    public void testDeleteNonExistentCourse() {
        //Non existent Id
        Mockito.when(courseDAO.get(9999)).thenReturn(null);

        boolean result = courseService.deleteCourse(9999);
        assertFalse(result);

        Mockito.verify(courseDAO).get(9999);
        Mockito.verify(courseDAO, never()).delete(any());

    }

    @Test
    public void testUpdateCourse() {
        Course c1 = new Course("Math-101", "Intro to Math");
        c1.setId(1);

        //Set up Mocks
        Mockito.when(courseDAO.get(1)).thenReturn(c1);
        Mockito.when(courseDAO.update(c1)).thenReturn(true);

        //Call and JUnit assertions
        boolean result = courseService.updateCourse(c1);
        assertTrue(result);

        //Mockito Verification
        Mockito.verify(courseDAO, atMostOnce()).get(1);
        Mockito.verify(courseDAO, atMostOnce()).update(c1);

    }

    @Test
    public void testUpdateNonExistentCourse() {
        //Non existent Id
        Course c1 = new Course("MATH-101", "Intro to Math");
        c1.setId(9999);

        //Set up Mocks
        Mockito.when(courseDAO.get(9999)).thenReturn(null);

        //Call and JUnit assertions
        boolean result = courseService.updateCourse(c1);
        assertFalse(result);

        //Mockito verification
        Mockito.verify(courseDAO).get(9999);
        Mockito.verify(courseDAO, never()).delete(any());

    }

    @Test
    public void testGetByCourseCode() {
        Course c1 = new Course("MATH-101", "Intro to Math");
        c1.setId(1);
        Mockito.when(courseDAO.findByCode(any())).thenAnswer(inv -> c1);

        Course c = courseService.findByCode("MATH-101");
        assertNotNull(c);
        assertEquals("MATH-101", c.getCode());

        Mockito.verify(courseDAO).findByCode(any());
    }

    private void assertNotNull(Course c) {
    }

    @Test
    public void testGetByNonExistentCourseCode() {
        Mockito.when(courseDAO.findByCode(any())).thenAnswer(inv -> null);

        Course c = courseService.findByCode("NOT THERE");
        assertNull(c);

        Mockito.verify(courseDAO).findByCode(any());
    }
}
