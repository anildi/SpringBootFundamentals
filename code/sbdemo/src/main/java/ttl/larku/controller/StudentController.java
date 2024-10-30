package ttl.larku.controller;

import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ttl.larku.domain.Student;
import ttl.larku.service.StudentService;

@RestController
@RequestMapping("/student")
public class StudentController {

   private final StudentService studentService;
   private final URICreator uriCreator;

   public StudentController(StudentService studentService,
                            URICreator uricreator) {
      this.studentService = studentService;
      this.uriCreator = uricreator;
   }

   @GetMapping
   public List<Student> getStudents() {
      List<Student> students = studentService.getAllStudents();
      return students;
   }

   @GetMapping("/{id}")
   public ResponseEntity<?> getStudent(@PathVariable int id) {
      Student student = studentService.getStudent(id);
      if(student == null) {
         return ResponseEntity.notFound().build();
      }
      return ResponseEntity.ok(student);
   }

   @PostMapping
   public ResponseEntity<Student>  insertStudent(@RequestBody Student student) {
     Student newStudent = studentService.createStudent(student);

      //http://localhost:8080/student/4

      URI newResource = uriCreator.getURI(newStudent.getId());
//      URI newResource = ServletUriComponentsBuilder
//                .fromCurrentRequest()
//                .path("/{id}")
//                .buildAndExpand(newStudent.getId())
//                .toUri();

//     return ResponseEntity.created(newResource).body(newStudent);
     return ResponseEntity.created(newResource).build();
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<Student>  deleteStudent(@PathVariable int id) {
      boolean result = studentService.deleteStudent(id);

      if(!result) {
         return ResponseEntity.notFound().build();
      }

      return ResponseEntity.noContent().build();
   }

   @PutMapping
   public ResponseEntity<Student>  updateStudent(@RequestBody Student student) {
      boolean result = studentService.updateStudent(student);

      if(!result) {
         return ResponseEntity.notFound().build();
      }

      return ResponseEntity.noContent().build();
   }
}
