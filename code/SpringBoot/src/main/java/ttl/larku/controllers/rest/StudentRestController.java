package ttl.larku.controllers.rest;

import com.ctc.wstx.shaded.msv_core.util.Uri;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import ttl.larku.domain.Student;
import ttl.larku.service.StudentService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/adminrest/student")
public class StudentRestController {

    private final UriCreator uriCreator;
    private StudentService studentService;

    //Constructor injection.  Also helps with testing.
    public StudentRestController(StudentService studentService, UriCreator uriCreator) {
        this.studentService = studentService;
        this.uriCreator = uriCreator;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudent(@PathVariable("id") int id) {
        Student s = studentService.getStudent(id);
        if (s == null) {
            return ResponseEntity.badRequest().body(RestResultGeneric.ofError("Student with id: " + id + " not found"));
        }
        return ResponseEntity.ok(RestResultGeneric.ofValue(s));
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT}, path = "/lowlevel")
    public String getLow(HttpServletRequest request, @RequestHeader HttpHeaders headers) throws IOException {
        String acceptHeader = headers.get("accept").stream().findFirst().orElse(null);
        String method = request.getMethod();
        String result = "boo";
        if (method.equalsIgnoreCase("POST")) {
            result = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        }

        return result;
    }

    @GetMapping
    public ResponseEntity<?> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return ResponseEntity.ok(RestResultGeneric.ofValue(students));
    }

//    @PostMapping
    public ResponseEntity<?> createStudent(@RequestBody Student s) {
        s = studentService.createStudent(s);

        URI newResource = uriCreator.getUriFor(s.getId());

//        URI newResource = ServletUriComponentsBuilder
//                .fromCurrentRequest()
//                .path("/{id}")
//                .buildAndExpand(s.getId())
//                .toUri();

        return ResponseEntity.created(newResource).body(RestResultGeneric.ofValue(s));
    }


    @GetMapping(value = "/headers/{id}", headers = {"myheader=myvalue", "otherheader=othervalue"})
    public ResponseEntity<?> getStudentWithHeader(@PathVariable("id") int id) {
        Student s = studentService.getStudent(id);
        if (s == null) {
            return ResponseEntity.badRequest().body(RestResultGeneric.ofError("Student with id: " + id + " not found"));
        }
        return ResponseEntity.ok(RestResultGeneric.ofValue(s));
    }

    @GetMapping("/byname/{name}")
    public ResponseEntity<?> getStudentByName(@PathVariable("name") String name) {
        List<Student> result = studentService.getByName(name);
        return ResponseEntity.ok(RestResultGeneric.ofValue(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable("id") int id) {
        if (studentService.deleteStudent(id)) {
            return ResponseEntity.noContent().build();
        } else {
            RestResultGeneric<Student> rr = RestResultGeneric.ofError("Student with id " + id + " not found");
            return ResponseEntity.badRequest().body(rr);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateStudent(@RequestBody Student student) {
        if (studentService.updateStudent(student)) {
            return ResponseEntity.noContent().build();
        } else {
            RestResultGeneric<Student> rr = RestResultGeneric.ofError("Student with id "
                    + student.getId() + " not found");
            return ResponseEntity.badRequest().body(rr);
        }
    }


    //For doing JSR 303 validation.  This requires the spring-boot-starter-validation in the pom file.
    //Note - if you uncomment this code, make sure you comment out the @Post method above.
    //Also requires the qualifier with @Autowired, because there are two validators in the class path.
    //One is the LocalValidatorFactoryBean with name "defaultValidator", and the other is
    //some kind of validator adaptor thingy with the name 'mvcValidator', that just points at the
    //first one.  We have to inject one of them by name.  Either name works.
    //Or, of course, just use @Resource with either name.
    //Update:  Better way is to declare a LocalValidatorFactoryBean in our @Configuration.
    //This makes the AutoConfigured validators go away, and you can then simply @Autowire.
    @Autowired
//    @Qualifier("mvcValidator")
//    @Resource(name = "defaultValidator")
    private Validator validator;

    /**
     * Two ways to do Validation.
     * One is to call the validator manually, as on the
     * first line of this function.  In this case you handle the errors
     * yourself, as show in the code.
     *
     * The other is to apply an @Valid annotation to the Student argument.  This will
     * make Spring automatically do the validation before it calls this function.
     * If the validation fails, Spring will throw a MethodArgumentNotValidException.
     * You can catch that exception in a @RestControllerAdvice class.
     * See ttl.larku.exceptions.GlobalErrorHandler for an example of @RestControllerAdvice.
     * If you do not catch the Exception, then the user gets a default error response.
     *
     * @param s
     * @param errors
     * @return
     */
    @PostMapping
    public ResponseEntity<?> createStudent(@RequestBody @Valid Student s, Errors errors) {
        validator.validate(s, errors);
        if (errors.hasErrors()) {
            List<String> errmsgs = errors.getFieldErrors().stream()
                    .map(error -> "error:" + error.getField() + ": " + error.getDefaultMessage()
                            + ", supplied Value: " + error.getRejectedValue())
                    .collect(toList());
            return ResponseEntity.badRequest().body(RestResultGeneric.ofError(errmsgs));
        }
        s = studentService.createStudent(s);

        URI newResource = uriCreator.getUriFor(s.getId());

        return ResponseEntity.created(newResource).body(RestResultGeneric.ofValue(s));
    }

//    //An example of an ExceptionHandler that is local to this controller.
//    //That is, it will only handle this exception in this controller.
//    //This exception is thrown when Spring cannot convert an argument.
//    //To test, try and get a student with an id of "abc".
//    //This controller can also be created in the ttl.larku.exceptions.GlobalErrorHandler
//    //instead of here, to handle this exception for all controllers.
//    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
//    protected RestResultGeneric<?> handleMethodArgument(MethodArgumentTypeMismatchException ex, WebRequest request) {
//        var errMessage = "In class ExceptionHandler: MethodArgumentTypeMismatch: name: " + ex.getName() + ", value: " + ex.getValue() + ", message: " +
//                ex.getMessage() + ", parameter: " + ex.getParameter();
//
//        RestResultGeneric<?> rr = RestResultGeneric.ofError(errMessage);
//
//        return rr;
//    }
}
