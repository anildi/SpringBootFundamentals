package ttl.larku.controllers.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import ttl.larku.dao.repository.StudentRepo;
import ttl.larku.domain.Student;
import ttl.larku.search.JPASearchSpecService;
import ttl.larku.service.RegistrationService;

@Controller
@RequestMapping("/admin")
public class StudentController {

   @Autowired
   private RegistrationService regService;

   @Autowired
   private StudentRepo studentRepo;

   @Autowired
   private JPASearchSpecService<Student, Integer, StudentRepo> searchService;

   @RequestMapping(value = "/getStudent", method = RequestMethod.GET)
   public ModelAndView getStudent(@RequestParam int id) {
      Student student = regService.getStudentService().getStudent(id);
      if (student == null) {
         return new ModelAndView("error/studentNotFound");
      }
      ModelAndView mav = new ModelAndView("showStudent", "student", student);
      return mav;
   }

//   @GetMapping("/getStudents")
//   public ModelAndView getAllTracks(@RequestParam Map<String, Object> queryStrings,
//                                    HttpSession session) {
//      List<Student> students = null;
//      Object tmp = queryStrings.get("pageSize");
//      int pageSize = tmp != null ? Integer.parseInt(tmp.toString()) : -1;
//      if (pageSize == -1) {
//         var sz = (Integer) session.getAttribute("pageSize");
//         if (sz != null) {
//            pageSize = sz;
//         } else {
//            pageSize = 10;
//         }
//      }
//      session.setAttribute("pageSize", pageSize);
//
//      //If we have a "searchParams" query parameter,
//      //break it out and put it into the searchContext as
//      //individual parameters.
//      var searchParams = queryStrings.get("searchParams");
//      if(searchParams != null) {
//        var arr = searchParams.toString().split("&");
//        for(String param : arr) {
//           var innerArr = param.split("=");
//           queryStrings.put(innerArr[0], innerArr[1]);
//        }
//      }
//
//      if (queryStrings.isEmpty()) {
//         students = regService.getStudentService().getAllStudents();
//      } else {
//         //tracks = trackService.getTracksBy(queryStrings);
//         //tracks = trackService.getTracksBySearchSpec(queryStrings);
//         queryStrings.put("pageSize", Integer.toString(pageSize));
//         //students = regService.getStudentService().getTracksByRequestParams(queryStrings);
//         students = searchService.getTracksByRequestParams(queryStrings, studentRepo);
//      }
//
//      tmp = queryStrings.get("page");
//      int page = tmp != null ? Integer.parseInt(tmp.toString()) : 1;
//
//      tmp = queryStrings.get("totalPages");
//      int totalPages = tmp != null ? Integer.parseInt(tmp.toString()) : 1;
//
//      tmp = queryStrings.get("totalElements");
//      long totalElements = tmp != null ? Long.parseLong(tmp.toString()) : 0L;
//
//      var props = Map.of(
//            "page", page,
//            "pageSize", pageSize,
//            "totalPages", totalPages,
//            "totalElements", totalElements,
//            "searchParams", searchParams != null ? searchParams : "",
//
//            "students", students);
//
//      ModelAndView mav = new ModelAndView("showStudents", props);
//      return mav;
//   }

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
      Student student = regService.getStudentService().getStudent(id);
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