package ttl.larku.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ttl.larku.domain.Course;
import ttl.larku.domain.ScheduledClass;
import ttl.larku.domain.Student;
import ttl.larku.jconfig.LarkUConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {LarkUConfig.class})
public class RegistrationServiceTest {

    private String name1 = "Bloke";
    private String name2 = "Blokess";
    private String newName = "Different Bloke";

    private String code1 = "BOT-101";
    private String code2 = "BOT-202";
    private String title1 = "Intro To Botany";
    private String title2 = "Outtro To Botany";

    private String startDate1 = "10/10/2012";
    private String startDate2 = "10/10/2013";
    private String endDate1 = "05/10/2013";
    private String endDate2 = "05/10/2014";

    private Course course1;
    private Course course2;
    private ScheduledClass class1;
    private ScheduledClass class2;
    private Student student1;
    private Student student2;

    //TODO - Dependency Injection using annotations.
    //Look at other tests if you need an example
    @Autowired
    private RegistrationService regService;

    //TODO - Dependency Injection using annotations.
    //Look at other tests if you need an example
    @Autowired
    private ApplicationContext appContext;

    @BeforeEach
    public void setup() {
        student1 = new Student(name1);
        student2 = new Student(name2);
        course1 = new Course(code1, title1);
        course2 = new Course(code2, title2);

        class1 = new ScheduledClass(course1, startDate1, endDate1);
        class2 = new ScheduledClass(course2, startDate2, endDate2);

        ClassService classService = appContext.getBean("classService", ClassService.class);
        CourseService courseService = appContext.getBean("courseService", CourseService.class);
        StudentService studentService = appContext.getBean("studentService", StudentService.class);

        //Empty out our databases
        studentService.clear();
        courseService.clear();
        classService.clear();

        //Initialize Services
        course1 = courseService.createCourse(code1, title1);
        course2 = courseService.createCourse(code2, title2);

        student1 = studentService.createStudent(name1);
        student2 = studentService.createStudent(name2);

    }

    @Test
    public void testAddClassAndStudent() {
        ScheduledClass sClass = regService.addNewClassToSchedule(code1, startDate1, endDate1);

        regService.registerStudentForClass(student1.getId(), code1, startDate1);

        List<Student> students = regService.getStudentsForClass(code1, startDate1);

        assertEquals(1, students.size());
        assertEquals(name1, students.get(0).getName());

        assertEquals(1, students.get(0).getClasses().size());
        assertEquals(code1, students.get(0).getClasses().get(0).getCourse().getCode());
    }

    @Test
    public void testDropStudent() {
        ScheduledClass sClass = regService.addNewClassToSchedule(code1, startDate1, endDate1);

        regService.registerStudentForClass(student1.getId(), code1, startDate1);
        regService.registerStudentForClass(student2.getId(), code1, startDate1);

        List<Student> students = regService.getStudentsForClass(code1, startDate1);

        assertEquals(2, students.size());
        assertEquals(name1, students.get(0).getName());

        regService.dropStudentFromClass(student1.getId(), code1, startDate1);

        students = regService.getStudentsForClass(code1, startDate1);

        assertEquals(1, students.size());
        assertEquals(name2, students.get(0).getName());

        assertEquals(1, students.get(0).getClasses().size());
        assertEquals(code1, students.get(0).getClasses().get(0).getCourse().getCode());
    }


}
