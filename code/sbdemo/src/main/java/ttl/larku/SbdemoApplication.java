package ttl.larku;

import java.time.LocalDate;
import java.util.List;
import javax.sound.midi.Track;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import ttl.larku.domain.Student;
import ttl.larku.service.StudentService;

@SpringBootApplication
public class SbdemoApplication {

   public static void main(String[] args) {
      SpringApplication.run(SbdemoApplication.class, args);
   }
}

//Create a Bean that implements command line runner.
//Print out a message from the run method of your bean.

@Component
class MyRunner implements CommandLineRunner {

   private final StudentService studentService;

   public MyRunner(StudentService studentService) {
      this.studentService = studentService;
   }

   @Override
   public void run(String... args) throws Exception {
      List<Student> input = List.of(
            new Student("Joe", "383 922 922922", LocalDate.of(1987, 10, 10), Student.Status.FULL_TIME),
            new Student("Roshan", "37849-09328 922922", LocalDate.of(1991, 10, 10), Student.Status.FULL_TIME),
            new Student("Samay", "4757891922 922922", LocalDate.of(2000, 10, 10), Student.Status.HIBERNATING)
      );

      input.forEach(studentService::createStudent);

      System.out.println("Boo");
      List<Student> students = studentService.getAllStudents();
      System.out.println("size: " + students.size());
      students.forEach(System.out::println);
   }
}

//@Component
//class YourRunner implements CommandLineRunner {
//
//   @Autowired
//   private TrackService trackService;
//
//   @Override
//   public void run(String... args) throws Exception {
//      List<Track> tracks = trackService.getAllTracks();
//      System.out.println("size: " + tracks.size());
//      tracks.forEach(System.out::println);
//   }
//}
