package ttl.larku.controllers.rest;

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

import java.net.URI;
import java.util.List;

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
        return ResponseEntity.ok(RestResultGeneric.ofValue(course));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourse(@PathVariable("id") int id) {
        Course c = courseService.getCourse(id);
        if (c == null) {
            return ResponseEntity.badRequest().body(RestResultGeneric.ofError("Course with id: " + id + " not found"));
        }
        return ResponseEntity.ok(RestResultGeneric.ofValue(c));
    }


    @GetMapping("/code/{courseCode}")
    public ResponseEntity<?> getCourseByCode(@PathVariable("courseCode") String courseCode) {
        Course c = courseService.findByCode(courseCode);
        if (c == null) {
            return ResponseEntity.badRequest().body(RestResultGeneric.ofError("Course with code: " + courseCode + " not found"));
        }
        return ResponseEntity.ok(RestResultGeneric.ofValue(c));
    }

    @PostMapping
    public ResponseEntity<?> addCourse(@RequestBody Course course) {
        Course newCourse = courseService.createCourse(course);
        URI uri = uriCreator.getUriFor(newCourse.getId());

        return ResponseEntity.created(uri).body(RestResultGeneric.ofValue(newCourse));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable("id") int id) {
        Course c = courseService.getCourse(id);
        if (c == null) {
            RestResultGeneric<Course> rr = RestResultGeneric.ofError("Course with id " + id + " not found");
            return ResponseEntity.badRequest().body(rr);
        }
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<?> updateCourse(@RequestBody Course course) {
        int id = course.getId();
        Course s = courseService.getCourse(id);
        if (s == null) {
            RestResultGeneric<Course> rr = RestResultGeneric.ofError("Course with id " + id + " not found");
            return ResponseEntity.badRequest().body(rr);
        }
        courseService.updateCourse(course);
        return ResponseEntity.noContent().build();
    }
}