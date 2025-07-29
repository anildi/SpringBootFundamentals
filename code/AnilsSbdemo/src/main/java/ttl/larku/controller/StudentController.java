package ttl.larku.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ttl.larku.domain.Student;
import ttl.larku.service.StudentService;

@RestController
@RequestMapping("/student")
public class StudentController {

   @Autowired
   private StudentService studentService;

   @GetMapping
   public List<Student> getStudents() {
      List<Student> students = studentService.getAllStudents();
      return students;
   }

   @GetMapping("/{id}")
   public ResponseEntity<?> getStudent(@PathVariable int id) {
      Optional<Student> opt = studentService.getStudent(id);
      if(opt.isEmpty()) {
         return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No student with id " + id);
      }
      Student student = opt.get();
      return ResponseEntity.ok(student);
   }

   @GetMapping("/req")
   public Student getStudentWithRequestParam(@RequestParam("id") int id) {
      Optional<Student> opt = studentService.getStudent(id);
      Student student = opt.orElse(null);
      return student;
   }

   @PostMapping
   public ResponseEntity<Student> addStudent(@RequestBody Student student) {
      Student newStudent = studentService.createStudent(student);

      //http://localhost:8080/student/{new student id}

        URI newResource = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newStudent.getId())
                .toUri();

      //return ResponseEntity.created(newResource).body(newStudent);
      return ResponseEntity.created(newResource).build();
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<?> deleteStudent(@PathVariable("id") int id) {
      boolean result = studentService.deleteStudent(id);
      if(!result) {
         return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No student with id " + id);
      }

      return ResponseEntity.noContent().build();

   }

   @PutMapping
   public ResponseEntity<?> updateStudent(@RequestBody Student student) {
      boolean result = studentService.updateStudent(student);
      if(!result) {
         return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No student with id " + student.getId());
      }

      //return ResponseEntity.noContent().build();
      return ResponseEntity.ok(student);
   }

}
