package ttl.larku.service;

import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Course;
import ttl.larku.rating.RatingProvider;


@Service("courseService")
@Profile("networkrating")
public class CourseServiceWithRating implements CourseService {

   private BaseDAO<Course> courseDAO;
   private final RatingProvider ratingProvider;

   private RestClient restClient;

   public CourseServiceWithRating(BaseDAO<Course> courseDAO,
                                  RatingProvider ratingProvider) {
      this.courseDAO = courseDAO;
      this.ratingProvider = ratingProvider;
      var baseUrl = "http://localhost:10001/rating";
      this.restClient = RestClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader("Accept", "application/json")
            .defaultHeader("Content-Type", "application/json")
            .build();
   }

   public Course createCourse(String code, String title) {
      Course course = new Course(code, title);
      course = createCourse(course);

      return course;
   }

   public Course createCourse(Course course) {
      course = courseDAO.insert(course);

      return course;
   }

   public boolean deleteCourse(int id) {
      return courseDAO.delete(id);
   }

   public boolean updateCourse(Course newCourse) {
      return courseDAO.update(newCourse);
   }

   public Optional<Course> getCourseByCode(String code) {
      List<Course> courses = courseDAO.findBy(c -> c.getCode().contains(code));
      if (!courses.isEmpty()) { //take the first one
         var course = courses.get(0);
         course.setRating(ratingProvider.getRating(course.getId()));
      }
      return !courses.isEmpty() ? Optional.of(courses.get(0)) : Optional.empty();
   }


   public Optional<Course> getCourse(int id) {
      return courseDAO.findById(id)
            .map(course -> {
               course.setRating(ratingProvider.getRating(course.getId()));
               return course;
            });
   }

   public List<Course> getAllCourses() {
      var courses = courseDAO.findAll();
      courses.forEach(c -> c.setRating(ratingProvider.getRating(c.getId())));

      return courses;
   }

   public BaseDAO<Course> getCourseDAO() {
      return courseDAO;
   }

   public void setCourseDAO(BaseDAO<Course> courseDAO) {
      this.courseDAO = courseDAO;
   }

   public void clear() {
      courseDAO.deleteStore();
      courseDAO.createStore();
   }

//    private String getRatingFromService(Course course) {
//        var response = restClient.get()
//              .uri("/{id}", course.getId())
//              .retrieve()
//              .toEntity(String.class);
//        if(response.getStatusCode() == HttpStatus.OK) {
//            var rating = response.getBody();
//            return rating;
//        }
//        return null;
//    }
}
