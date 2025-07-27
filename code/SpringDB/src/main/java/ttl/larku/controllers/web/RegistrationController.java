package ttl.larku.controllers.web;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ttl.larku.domain.Course;
import ttl.larku.domain.ScheduledClass;
import ttl.larku.domain.Student;
import ttl.larku.service.RegistrationService;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

    @Autowired
    private RegistrationService regService;

    @RequestMapping(value = "/getAllClasses", method = RequestMethod.GET)
    public ModelAndView getAllClasses() {
        List<ScheduledClass> classes = regService.getClassService().getAllScheduledClasses();
        // ModelAndView mav = new ModelAndView("/WEB-INF/jsp/showClasses.jsp",
        // "classes", classes);
        ModelAndView mav = new ModelAndView("showClasses", "classes", classes);
        return mav;
    }

    @RequestMapping(value = "/addClass", method = RequestMethod.GET)
    public ModelAndView showAddClassForm(ScheduledClass sClass) {
        List<Course> courses = regService.getCourseService().getAllCourses();

        // return new ModelAndView("/WEB-INF/jsp/addClass.jsp", "courses", courses);
        // return new ModelAndView("addClass", "courses", courses);
        return new ModelAndView("addClass", "courses", courses);
    }

    @RequestMapping(value = "/addClass", method = RequestMethod.POST)
    public ModelAndView addClass(@RequestParam String courseCode, @RequestParam String startDate,
                                 @RequestParam String endDate) {

        regService.addNewClassToSchedule(courseCode, LocalDate.parse(startDate),
              LocalDate.parse(endDate));

        List<ScheduledClass> classes = regService.getClassService().getAllScheduledClasses();
        // ModelAndView mav = new ModelAndView("redirect:getAllClasses", "classes",
        // classes);
        ModelAndView mav = new ModelAndView("redirect:getAllClasses", "classes", classes);
        return mav;
    }

    @RequestMapping(value = "/registerStudentForClass", method = RequestMethod.GET)
    public ModelAndView showRegisterStudentForm() {
        List<Student> students = regService.getStudentService().getAllStudents();
        List<ScheduledClass> classes = regService.getClassService().getAllScheduledClasses();

        // ModelAndView mav = new ModelAndView("/WEB-INF/jsp/addRegistration.jsp");
        ModelAndView mav = new ModelAndView("addRegistration");
        mav.addObject("students", students);
        mav.addObject("classes", classes);

        return mav;
    }

    @RequestMapping(value = "/registerStudentForClass", method = RequestMethod.POST)
    public ModelAndView registerStudent(@RequestParam int studentId, @RequestParam int classId) {
        Student student = regService.getStudentService().getStudent(studentId);
        if (student != null) {
            ScheduledClass sClass = regService.getClassService().getScheduledClass(classId);
            if (sClass != null) {
                regService.registerStudentForClass(student.getId(), sClass.getCourse().getCode(),
                        sClass.getStartDate());

                student = regService.getStudentService().getStudent(studentId);

                // return new ModelAndView("/WEB-INF/jsp/showStudent.jsp", "student", student);
                return new ModelAndView("showStudent", "student", student);
            }
        }
        return new ModelAndView("error/studentNotFound");
    }
}