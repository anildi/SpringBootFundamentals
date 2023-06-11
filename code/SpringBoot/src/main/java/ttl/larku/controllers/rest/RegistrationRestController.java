package ttl.larku.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ttl.larku.domain.ScheduledClass;
import ttl.larku.domain.Student;
import ttl.larku.service.ClassService;
import ttl.larku.service.RegistrationService;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/adminrest/class")
public class RegistrationRestController {
    private RegistrationService regService;
    private ClassService classService;
    private UriCreator uriCreator;

    @Autowired
    public RegistrationRestController(RegistrationService registrationService,
                                      ClassService classService, UriCreator uriCreator) {
        this.regService = registrationService;
        this.classService = classService;
        this.uriCreator = uriCreator;
    }

    @GetMapping
    public ResponseEntity<?> getAllClasses() {
        List<ScheduledClass> classes = classService.getAllScheduledClasses();
        return ResponseEntity.ok().body(RestResultGeneric.ofValue(classes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getScheduledClass(@PathVariable("id") int id) {
        ScheduledClass c = classService.getScheduledClass(id);
        if (c == null) {
            RestResultGeneric<Student> rr = RestResultGeneric.ofError("ScheduledClass with id: " + id + " not found");
            return ResponseEntity.badRequest().body(rr);
        }
        return ResponseEntity.ok(RestResultGeneric.ofValue(c));
    }

    @GetMapping("/code/{courseCode}")
    public ResponseEntity<?> getScheduledClassPath(@PathVariable("courseCode") String courseCode) {
        List<ScheduledClass> cl = classService.getScheduledClassesByCourseCode(courseCode);
        if (cl == null || cl.size() == 0) {
            RestResultGeneric<Void> r1 = RestResultGeneric.ofError("ScheduledClass with code: " +
                    courseCode + " not found");
            return ResponseEntity.badRequest().body(r1);
        }


        return ResponseEntity.ok(RestResultGeneric.ofValue(cl));
    }


    @PostMapping
    public ResponseEntity<?> addClass(@RequestParam("courseCode") String courseCode,
                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                      @RequestParam("startDate") LocalDate startDate,
                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                      @RequestParam("endDate") LocalDate endDate) {

        ScheduledClass sc = regService.addNewClassToSchedule(courseCode, startDate, endDate);

        URI uri = uriCreator.getUriFor(sc.getId());

        return ResponseEntity.created(uri).body(RestResultGeneric.ofValue(sc));
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> registerStudent(@RequestParam int studentId, @RequestParam int classId) {

        ScheduledClass sClass = regService.getClassService().getScheduledClass(classId);
        regService.registerStudentForClass(studentId, sClass.getCourse().getCode(), sClass.getStartDate());

        Student student = regService.getStudentService().getStudent(studentId);

        return ResponseEntity.ok(RestResultGeneric.ofValue(student));
    }

    @RequestMapping(value = "/register/{studentId}/{classId}", method = RequestMethod.POST)
    public ResponseEntity<?> registerStudentWithPath(@PathVariable int studentId, @PathVariable int classId) {

        return registerStudent(studentId, classId);
    }
}
