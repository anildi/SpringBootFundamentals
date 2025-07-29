package ttl.larku.reflect.inject;

import ttl.larku.domain.Course;
import ttl.larku.service.CourseService;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author whynot
 */
public class InjectAppComplex {

    public static void main(String[] args) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        SomeController sc = BeanFactoryComplex.getBean(SomeController.class);

        sc.doStuff();

        CourseService courseService = BeanFactoryComplex.getBean(CourseService.class);
        courseService.createCourse("Math-101", "Baby Math");
        List<Course> courses = courseService.getAllCourses();
        System.out.println("courses: " + courses);

    }
}
