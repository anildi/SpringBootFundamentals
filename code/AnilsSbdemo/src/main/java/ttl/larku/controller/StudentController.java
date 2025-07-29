package ttl.larku.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
   public Student getStudent(@PathVariable int id) {
      Optional<Student> opt = studentService.getStudent(id);
      Student student = opt.orElse(null);
      return student;
   }

   @GetMapping("/req")
   public Student getStudentWithRequestParam(@RequestParam("id") int id) {
      Optional<Student> opt = studentService.getStudent(id);
      Student student = opt.orElse(null);
      return student;
   }

   @PostMapping
   public Student addStudent(@RequestBody Student student) {
      Student newStudent = studentService.createStudent(student);

      return newStudent;
   }

}
