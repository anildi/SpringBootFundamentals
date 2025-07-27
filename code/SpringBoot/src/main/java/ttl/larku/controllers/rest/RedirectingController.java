package ttl.larku.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ttl.larku.domain.Student;
import ttl.larku.service.StudentService;

import java.net.URI;

@Controller
@RequestMapping("/adminrest/redirector")
public class RedirectingController {

    @Autowired
    private StudentService studentService;

    @PostMapping
    public String createStudent(@RequestBody Student s) {
        s = studentService.createStudent(s);

        URI newResource = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(s.getId())
                .toUri();

//        return ResponseEntity.created(newResource).body(new RestResult(s));
        return "redirect:/adminrest/student";
    }
}
