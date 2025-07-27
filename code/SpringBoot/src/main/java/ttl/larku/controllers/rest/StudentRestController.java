package ttl.larku.controllers.rest;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ttl.larku.domain.Student;
import ttl.larku.service.StudentService;

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
      return studentService.getStudent(id)
            .map(s -> ResponseEntity.ok(RestResultWrapper.ofValue(s)))
            .orElseGet(() -> {
               var pd = uriCreator.getProblemDetail(HttpStatus.BAD_REQUEST,
                     "Student with id: " + id + " not found");
               return ResponseEntity.badRequest()
                     .body(RestResultWrapper.ofError(pd));
            });
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
      return ResponseEntity.ok(RestResultWrapper.ofValue(students));
   }

   //    @PostMapping
   public ResponseEntity<?> createStudent(@RequestBody Student s) {
      s = studentService.createStudent(s);

      URI newResource = uriCreator.getURI(s.getId());

//        URI newResource = ServletUriComponentsBuilder
//                .fromCurrentRequest()
//                .path("/{id}")
//                .buildAndExpand(s.getId())
//                .toUri();

      return ResponseEntity.created(newResource).body(RestResultWrapper.ofValue(s));
   }


   @GetMapping(value = "/headers/{id}", headers = {"myheader=myvalue", "otherheader=othervalue"})
   public ResponseEntity<?> getStudentWithHeader(@PathVariable("id") int id) {
      return studentService.getStudent(id)
            .map(s -> ResponseEntity.ok(RestResultWrapper.ofValue(s)))
            .orElseGet(() -> ResponseEntity.badRequest()
                  .body(RestResultWrapper.ofError("Student with id: " + id + " not found")));
   }

   @GetMapping("/byname/{name}")
   public ResponseEntity<?> getStudentByName(@PathVariable("name") String name) {
      List<Student> result = studentService.getByName(name);
      return ResponseEntity.ok(RestResultWrapper.ofValue(result));
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<?> deleteStudent(@PathVariable("id") int id) {
      if (studentService.deleteStudent(id)) {
         return ResponseEntity.noContent().build();
      } else {
         RestResultWrapper<Student> rr = RestResultWrapper.ofError("Student with id " + id + " not found");
         return ResponseEntity.badRequest().body(rr);
      }
   }

   @PutMapping
   public ResponseEntity<?> updateStudent(@RequestBody Student student) {
      if (studentService.updateStudent(student)) {
         return ResponseEntity.noContent().build();
      } else {
         RestResultWrapper<Student> rr = RestResultWrapper.ofError("Student with id "
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
   //Update 2:  Our Local bean does not seem to be necessary any more.  Works without it.
   //But we have it configured as a Bean anyway.
   @Autowired
//    @Qualifier("mvcValidator")
//    @Resource(name = "defaultValidator")
   private Validator validator;

   /**
    * Two ways to do Validation.
    * One is to call the validator manually, as on the
    * first line of this function.  In this case you handle the errors
    * yourself, as show in the code.
    * <p>
    * The other is to apply an @Valid annotation to the Student argument.  This will
    * make Spring automatically do the validation before it calls this function.
    * If the validation fails, Spring will do one of the following:
    * - if this function declares an Errors argument, it will
    * call this function with the Errors argument containing the errors.
    * In this case you check for and handle errors yourself.
    * - If this function does *not* have an Errors argument, then
    * Spring will throw a MethodArgumentNotValidException on Validation
    * failure.
    * You can catch that exception in a @RestControllerAdvice class.
    * <p>
    * See ttl.larku.exceptions.GlobalErrorHandler for an example of @RestControllerAdvice.
    * If you do not catch the Exception, then the user gets a default error response.
    * <p>
    * Note: You should *NOT* do manual validation if you are using the @Valid annotation, else you
    * may wind up doing the same validation twice.
    *
    * @param s      The incoming Student
    * @param errors The errors object, which may contain errors already if you are using
    *               the @Valid annotation
    * @return A ResponseEntity with Status of created (201) and a Location header with the new URI.
    */
   @PostMapping
   public ResponseEntity<?> createStudent(@RequestBody /*@Valid*/ Student s, Errors errors) {
      validator.validate(s, errors);
      if (errors.hasErrors()) {
         List<String> errmsgs = errors.getFieldErrors().stream()
               .map(error -> "Manual Validation error:" + error.getField() + ": " + error.getDefaultMessage()
                     + ", supplied Value: " + error.getRejectedValue())
               .collect(toList());
         return ResponseEntity.badRequest().body(RestResultWrapper.ofError(errmsgs));
      }
      s = studentService.createStudent(s);

      URI newResource = uriCreator.getURI(s.getId());

      return ResponseEntity.created(newResource).body(RestResultWrapper.ofValue(s));
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
