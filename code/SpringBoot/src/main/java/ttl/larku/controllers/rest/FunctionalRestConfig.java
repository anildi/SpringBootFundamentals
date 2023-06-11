package ttl.larku.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ttl.larku.domain.Student;
import ttl.larku.service.StudentService;

import javax.servlet.ServletException;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.servlet.function.RequestPredicates.*;
import static org.springframework.web.servlet.function.RouterFunctions.route;


@Configuration
public class FunctionalRestConfig {

    @Autowired
    private FunctionalStudentHandler handler;

    @Bean
    public RouterFunction<ServerResponse> router() throws Exception {
        RouterFunction<ServerResponse> route = route()
                .GET("/func/student", accept(APPLICATION_JSON), handler::getAllStudents)
                .GET("/func/student/{id}", accept(APPLICATION_JSON), handler::getStudent)
                .GET("/func/headers/{id}",
                        accept(APPLICATION_JSON)
                        .and(headers((headers) -> {
                            if(headers == null) return false;
                            String fh = headers.firstHeader("someheader");
//                            if(fh == null) return false;
//                            return fh.equals("somevalue");
                            return fh != null && fh.equals("somevalue");

                        })),
                        handler::getStudentWithHeader)
                .POST("/func/student",
                        accept(APPLICATION_JSON)
                                .and(contentType(APPLICATION_JSON)),
                        handler::createStudent)
                .DELETE("/{id}", handler::deleteStudent)
                .PUT("/func/student", handler::deleteStudent)
                .build();
        return route;
    }
}


@Component
class FunctionalStudentHandler {

    private StudentService studentService;

    FunctionalStudentHandler(StudentService studentService) {
        this.studentService = studentService;
    }


    public ServerResponse getAllStudents(ServerRequest request) {
        List<Student> students = studentService.getAllStudents();
        return ServerResponse.ok().body(RestResultGeneric.ofValue(students));
    }

    public ServerResponse createStudent(ServerRequest request) throws ServletException, IOException {
        Student s = request.body(Student.class);
        s = studentService.createStudent(s);

        URI newResource = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(s.getId())
                .toUri();

        return ServerResponse.created(newResource).body(RestResultGeneric.ofValue(s));
    }


    public ServerResponse getStudent(ServerRequest request) {
        int id = Integer.parseInt(request.pathVariable("id"));
        Student s = studentService.getStudent(id);
        if (s == null) {
            return ServerResponse.badRequest().body(RestResultGeneric.ofError("Student with id: " + id + " not found"));
        }
        return ServerResponse.ok().body(RestResultGeneric.ofValue(s));
    }

//    @GetMapping(value = "/headers/{id}", headers = {"myheader=myvalue", "otherheader=othervalue"})
    public ServerResponse getStudentWithHeader(ServerRequest request) {
        int id = Integer.parseInt(request.pathVariable("id"));
        Student s = studentService.getStudent(id);
        if (s == null) {
            return ServerResponse.badRequest().body(RestResultGeneric.ofError("Student with id: " + id + " not found"));
        }
        return ServerResponse.ok().body(RestResultGeneric.ofValue(s));
    }

    //@DeleteMapping("/{id}")
    public ServerResponse deleteStudent(ServerRequest request) {
        int id = Integer.parseInt(request.pathVariable("id"));
        Student s = studentService.getStudent(id);
        if (s == null) {
            RestResultGeneric<Student> rr = RestResultGeneric.ofError("Student with id " + id + " not found");
            return ServerResponse.badRequest().body(rr);
        }
        studentService.deleteStudent(id);
        return ServerResponse.noContent().build();
    }

//    @PutMapping
    public ServerResponse updateStudent(ServerRequest request) throws ServletException, IOException {
        Student student = request.body(Student.class);
        int id = student.getId();
        Student s = studentService.getStudent(id);
        if (s == null) {
            RestResultGeneric<Student> rr = RestResultGeneric.ofError("Student with id " + id + " not found");
            return ServerResponse.badRequest().body(rr);
        }
        studentService.updateStudent(student);
        return ServerResponse.noContent().build();
    }
}


//    @PostMapping
//    public ResponseEntity<?> createStudent(@RequestBody Student s,
//                                           UriComponentsBuilder ucb, Errors errors) {
////        validator.validate(s, errors);
//        if (errors.hasErrors()) {
//            List<String> errmsgs = errors.getFieldErrors().stream()
//                    .map(error -> "error:" + error.getField() + ": " + error.getDefaultMessage()
//                            + ", supplied Value: ")
//                    .collect(toList());
//            return ResponseEntity.badRequest().body(new RestResult(RestResult.Status.Error,
//                    errmsgs));
//        }
//        s = studentService.createStudent(s);
//        UriComponents uriComponents = ucb.path("/student/{id}")
//                .buildAndExpand(s.getId());
//
//        return ResponseEntity.created(uriComponents.toUri()).body(new RestResult(s));
//    }
