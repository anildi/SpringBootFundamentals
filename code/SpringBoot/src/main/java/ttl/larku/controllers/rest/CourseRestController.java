package ttl.larku.controllers.rest;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ttl.larku.domain.Course;
import ttl.larku.service.CourseService;

@RestController
@RequestMapping("/adminrest/course")
public class CourseRestController {

   private final UriCreator uriCreator;
   private CourseService courseService;

   //Constructor injection.
   public CourseRestController(CourseService courseService, UriCreator uriCreator) {
      this.courseService = courseService;
      this.uriCreator = uriCreator;
   }

   @GetMapping
   public ResponseEntity<?> getCourses() {
      List<Course> course = courseService.getAllCourses();
      return ResponseEntity.ok(RestResultWrapper.ofValue(course));
   }

   @GetMapping("/{id}")
   public ResponseEntity<?> getCourse(@PathVariable("id") int id) {
      return courseService.getCourse(id)
            .map(course -> ResponseEntity.ok(RestResultWrapper.ofValue(course)))
            .orElse(ResponseEntity.badRequest()
                  .body(RestResultWrapper.ofError("Course with id: " + id + " not found")));
   }


   @GetMapping("/code/{courseCode}")
   public ResponseEntity<?> getCourseByCode(@PathVariable("courseCode") String courseCode) {
      return courseService.getCourseByCode(courseCode)
            .map(course -> ResponseEntity.ok(RestResultWrapper.ofValue(course)))
            .orElse(
                  ResponseEntity.badRequest()
                        .body(RestResultWrapper.ofError("Course with code: "
                              + courseCode + " not f (opt.isEmpty())")));
   }

   @PostMapping
   public ResponseEntity<?> addCourse(@RequestBody Course course) {
      Course newCourse = courseService.createCourse(course);
      URI uri = uriCreator.getURI(newCourse.getId());

      return ResponseEntity.created(uri).body(RestResultWrapper.ofValue(newCourse));
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<?> deleteCourse(@PathVariable("id") int id) {
      var result = courseService.deleteCourse(id);
      if (!result) {
         RestResultWrapper<Course> rr = RestResultWrapper.ofError("Course with id " + id + " not found");
         return ResponseEntity.badRequest().body(rr);
      }
      return ResponseEntity.noContent().build();
   }

   @PutMapping
   public ResponseEntity<?> updateCourse(@RequestBody Course course) {
      var result = courseService.updateCourse(course);
      if (!result) {
         RestResultWrapper<Course> rr = RestResultWrapper.ofError("Course with id " + course.getId() + " not found");
         return ResponseEntity.badRequest().body(rr);
      }
      return ResponseEntity.noContent().build();
   }
}