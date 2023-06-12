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

import static org.junit.Assert.assertEquals;

//TODO - Add Annotations to run Test with SpringExtension, and
// to create the application Context.  Look at Examples in other Tests if
// you need to
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {LarkUConfig.class})
public class StudentServiceTest {

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

    @Autowired
    private StudentService ss;

    @BeforeEach
    public void setup() {
        student1 = new Student(name1);
        student2 = new Student(name2);
        course1 = new Course(code1, title1);
        course2 = new Course(code2, title2);

        class1 = new ScheduledClass(course1, startDate1, endDate1);
        class2 = new ScheduledClass(course2, startDate2, endDate2);

        //studentService = new StudentService();
        ss.clear();
    }

    @Test
    public void testCreateStudent() {
        Student newStudent = ss.createStudent(name1);

        Student result = ss.getStudent(newStudent.getId());

        assertEquals(name1, result.getName());
        assertEquals(1, ss.getAllStudents().size());
    }

    @Test
    public void testDeleteStudent() {
        Student student1 = ss.createStudent(name1);
        Student student2 = ss.createStudent(name2);

        assertEquals(2, ss.getAllStudents().size());

        ss.deleteStudent(student1.getId());

        assertEquals(1, ss.getAllStudents().size());
        assertEquals(name2, ss.getAllStudents().get(0).getName());
    }

    @Test
    public void testUpdateStudent() {
        Student student1 = ss.createStudent(name1);

        assertEquals(1, ss.getAllStudents().size());

        student1.setName(name2);
        ss.updateStudent(student1);

        assertEquals(1, ss.getAllStudents().size());
        assertEquals(name2, ss.getAllStudents().get(0).getName());
    }

}
