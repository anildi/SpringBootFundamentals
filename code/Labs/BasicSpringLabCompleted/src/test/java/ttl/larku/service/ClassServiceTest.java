package ttl.larku.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ClassServiceTest {

    @Autowired
    private ClassService classService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private StudentService studentService;

    private String name1 = "Bloke";
    private String name2 = "Blokess";
    private String newName = "Different Bloke";

    private String code1 = "BOT-101";
    private String code2 = "BOT-202";
    private String badCode = "BADNESS";
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

    @BeforeEach
    public void setup() {
        student1 = new Student(name1);
        student2 = new Student(name2);
        course1 = new Course(code1, title1);
        course2 = new Course(code2, title2);

        class1 = new ScheduledClass(course1, startDate1, endDate1);
        class2 = new ScheduledClass(course2, startDate2, endDate2);

        //Clean up the DAO's we will be using
        //classService = new ClassService();
        //courseService = new CourseService();
        //studentService = new StudentService();
        //Empty out our databases
        studentService.clear();
        courseService.clear();
        classService.clear();

        //Initialize courseService
        courseService.createCourse(code1, title1);
        courseService.createCourse(code2, title2);

    }

    @Test
    public void testCreateScheduledClass() {

        ScheduledClass newScheduledClass = classService.createScheduledClass(code1,
                startDate1, endDate1);

        ScheduledClass result = classService.getScheduledClass(newScheduledClass.getId());

        assertEquals(code1, result.getCourse().getCode());
        assertEquals(1, classService.getAllScheduledClasses().size());
    }

    @Test
    public void testDeleteScheduledClass() {
        ScheduledClass sClass1 = classService.createScheduledClass(code1, startDate1, endDate1);
        ScheduledClass sClass2 = classService.createScheduledClass(code2, startDate2, endDate2);

        assertEquals(2, classService.getAllScheduledClasses().size());

        classService.deleteScheduledClass(sClass1.getId());

        assertEquals(1, classService.getAllScheduledClasses().size());
        assertEquals(code2, classService.getAllScheduledClasses().get(0).getCourse().getCode());
    }

    @Test
    public void testUpdateScheduledClass() {
        ScheduledClass sClass1 = classService.createScheduledClass(code1, startDate1, endDate1);

        assertEquals(1, classService.getAllScheduledClasses().size());

        sClass1.setCourse(course2);
        classService.updateScheduledClass(sClass1);

        assertEquals(1, classService.getAllScheduledClasses().size());
        assertEquals(code2, classService.getAllScheduledClasses().get(0).getCourse().getCode());
    }

    @Test
    public void testSearchForNonExistentCode() {
        ScheduledClass sClass1 = classService.createScheduledClass(code1, startDate1, endDate1);

        List<ScheduledClass> result = classService.getScheduledClassesByCourseCode(code2);

        assertEquals(0, result.size());
    }

    @Test
    public void testForMissingCodeOnCreate() {
        ScheduledClass sClass1 = classService.createScheduledClass(badCode, startDate1, endDate1);

        List<ScheduledClass> result = classService.getScheduledClassesByCourseCode(code1);

        assertEquals(0, result.size());
    }

    @Test
    public void testBadIdOnDelete() {
        ScheduledClass sClass1 = classService.createScheduledClass(code1, startDate1, endDate1);

        classService.deleteScheduledClass(Integer.MIN_VALUE);

        List<ScheduledClass> result = classService.getScheduledClassesByCourseCode(code1);
        assertEquals(1, result.size());
    }

}
