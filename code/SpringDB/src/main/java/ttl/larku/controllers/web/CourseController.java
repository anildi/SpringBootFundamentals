package ttl.larku.controllers.web;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ttl.larku.domain.Course;
import ttl.larku.service.RegistrationService;

@Controller
@RequestMapping("/admin")
public class CourseController {

    @Autowired
    private RegistrationService regService;

    @RequestMapping(value = "/getCourses", method = RequestMethod.GET)
    public ModelAndView getCourses() {
        List<Course> courses = regService.getCourseService().getAllCourses();
        //return new ModelAndView("/WEB-INF/jsp/showCourses.jsp", "courses", courses);
        return new ModelAndView("showCourses", "courses", courses);
    }

    @RequestMapping(value = "/getCourse", method = RequestMethod.GET)
    public ModelAndView getCourse(@RequestParam int id) {
        Course course = regService.getCourseService().getCourse(id);
        //return new ModelAndView("/WEB-INF/jsp/showCourse.jsp", "course", course);
        return new ModelAndView("showCourse", "course", course);
    }

    @RequestMapping(value = "/addCourse", method = RequestMethod.GET)
    public ModelAndView showAddCourseForm(Course course) {
        course.setCredits(3);
        //return "/WEB-INF/jsp/addCourse.jsp";
        //return "addCourse";
        return new ModelAndView("addCourse", "course", course);
    }

    @RequestMapping(value = "/addCourse", method = RequestMethod.POST)
    public ModelAndView addCourse(Course course, BindingResult errors) {
        regService.getCourseService().createCourse(course);

        List<Course> courses = regService.getCourseService().getAllCourses();
        return new ModelAndView("redirect:getCourses", "courses", courses);
        //return new ModelAndView("/WEB-INF/jsp/showCourses.jsp", "courses", courses);
    }
}
