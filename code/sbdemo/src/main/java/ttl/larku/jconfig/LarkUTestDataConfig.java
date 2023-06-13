package ttl.larku.jconfig;

import ttl.larku.dao.BaseDAO;
import ttl.larku.dao.inmemory.InMemoryCourseDAO;
import ttl.larku.dao.inmemory.InMemoryStudentDAO;
import ttl.larku.domain.Course;
import ttl.larku.domain.Student;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LarkUTestDataConfig {

    public Student student1() {
        Student student = new Student();
        student.setId(1);
        student.setName("Manoj");
        student.setStatus(Student.Status.FULL_TIME);
        student.setPhoneNumber("222 333-4444");
        student.setDob(LocalDate.parse("1956-08-15"));
        return student;
    }

    public Student student2() {
        Student student = new Student();
        student.setId(2);
        student.setName("Ana");
        student.setStatus(Student.Status.PART_TIME);
        student.setPhoneNumber("222 333-7900");
        student.setDob(LocalDate.parse("1965-09-25"));
        return student;
    }

    public Student student3() {
        Student student = new Student();
        student.setId(3);
        student.setName("Roberta");
        student.setStatus(Student.Status.HIBERNATING);
        student.setPhoneNumber("383 343-5879");
        student.setDob(LocalDate.parse("2001-07-28"));
        return student;
    }

    public Student student4() {
        Student student = new Student();
        student.setId(4);
        student.setName("Madhu");
        student.setStatus(Student.Status.PART_TIME);
        student.setPhoneNumber("223 598 8279");
        student.setDob(LocalDate.parse("1970-01-17"));
        return student;
    }

    Course course1() {
        Course course = new Course();
        course.setId(1);
        course.setTitle("Intro To BasketWeaving");
        course.setCode("BKTW-101");
        course.setCredits(3);

        return course;
    }

    Course course2() {
        Course course = new Course();
        course.setId(2);
        course.setTitle("Yet More Botany");
        course.setCode("BOT-202");
        course.setCredits(2);

        return course;
    }

    Course course3() {
        Course course = new Course();
        course.setId(3);
        course.setTitle("Intro To Math");
        course.setCode("MATH-101");
        course.setCredits(4);

        return course;
    }


    public BaseDAO<Student> studentDAOWithInitData() {
        InMemoryStudentDAO dao = new InMemoryStudentDAO();

        dao.create(student1());
        dao.create(student2());
        dao.create(student3());
        dao.create(student4());

        return dao;
    }

    public Map<Integer, Student> initStudents() {
        Map<Integer, Student> students = new HashMap<>();
        students.put(student1().getId(), student1());
        students.put(student2().getId(), student2());
        students.put(student3().getId(), student3());
        students.put(student4().getId(), student4());

        return students;
    }

    public BaseDAO<Course> courseDAOWithInitData() {
        InMemoryCourseDAO dao = new InMemoryCourseDAO();
        dao.create(course1());
        dao.create(course2());
        dao.create(course3());

        return dao;

    }

    public Map<Integer, Course> initCourses() {
        Map<Integer, Course> courses = new HashMap<>();

        courses.put(course1().getId(), course1());
        courses.put(course2().getId(), course2());
        courses.put(course3().getId(), course3());
        return courses;
    }


    private Date convertToDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);

        return cal.getTime();
    }

}
