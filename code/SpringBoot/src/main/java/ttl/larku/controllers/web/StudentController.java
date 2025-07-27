package ttl.larku.controllers.web;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import ttl.larku.domain.Student;
import ttl.larku.service.RegistrationService;

@Controller
@RequestMapping("/admin")
public class StudentController {

    @Autowired
    private RegistrationService regService;

    @RequestMapping(value = "/getStudent", method = RequestMethod.GET)
    public ModelAndView getStudent(@RequestParam int id) {
        Student student = regService.getStudentService().getStudent(id).orElse(null);
        if (student == null) {
            return new ModelAndView("error/studentNotFound");
        }
        ModelAndView mav = new ModelAndView("showStudent", "student", student);
        return mav;
    }

    @RequestMapping(value = "/getStudents", method = RequestMethod.GET)
    public ModelAndView getStudents() {
        List<Student> students = regService.getStudentService().getAllStudents();
        ModelAndView mav = new ModelAndView("showStudents", "students", students);
        return mav;
    }

    @RequestMapping(value = "/getStudentsAjax", method = RequestMethod.GET)
    public ModelAndView getStudentsAjax() {
        List<Student> students = regService.getStudentService().getAllStudents();
        ModelAndView mav = new ModelAndView("showStudentsJQuery", "students", students);
        return mav;
    }

    @RequestMapping(value = "/showStudents", method = RequestMethod.GET)
    public ModelAndView showStudents() {
        List<Student> students = regService.getStudentService().getAllStudents();
        return new ModelAndView("showStudentsJQuery", "students", students);
    }

    @RequestMapping(value = "/getStudentTheHardWay", method = RequestMethod.GET)
    public ModelAndView getStudentTheHardWay(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        Student student = regService.getStudentService().getStudent(id).orElse(null);
        if (student == null) {
            return new ModelAndView("error/studentNotFound");
        }
        ModelAndView mav = new ModelAndView("showStudent", "student", student);
        return mav;
    }


    @RequestMapping(value = "/addStudentsAjax", method = RequestMethod.GET)
    public ModelAndView addStudentsResty() {
        ModelAndView mav = new ModelAndView("addStudentsJQueryAjax", "statusList", Student.Status.values());
        return mav;
    }

    @RequestMapping(value = "/addStudent", method = RequestMethod.GET)
    public ModelAndView showAddStudentForm(@ModelAttribute("student") Student student) {
        student.setName("No Name");
        student.setStatus(Student.Status.FULL_TIME);
        //return "/WEB-INF/jsp/addStudent.jsp";
        ModelAndView mav = new ModelAndView("addStudent");
        return mav;
    }

    @RequestMapping(value = "/addStudent", method = RequestMethod.POST)
    public ModelAndView addStudent(Student student, BindingResult errors) {
        if (errors.hasErrors()) {
            return new ModelAndView("errorPage");
        }
        regService.getStudentService().createStudent(student);
        //List<Student> students = regService.getStudentService().getAllStudents();

        return new ModelAndView("redirect:getStudents");
    }


    @RequestMapping(value = "/addStudent2", method = RequestMethod.POST)
    public ModelAndView addStudentWithSessionAttributes(Student student, BindingResult errors,
                                                        SessionStatus sStatus,
                                                        @RequestParam(required = false) String addStudentDone) {
        regService.getStudentService().createStudent(student);
//        List<Student> students = regService.getStudentService().getAllStudents();

        if (addStudentDone != null) {
            sStatus.setComplete();
        }
        return new ModelAndView("redirect:getStudents");
    }
}