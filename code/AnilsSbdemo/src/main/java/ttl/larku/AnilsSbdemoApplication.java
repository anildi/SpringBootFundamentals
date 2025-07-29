package ttl.larku;

import java.util.List;
import javax.sound.midi.Track;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import ttl.larku.domain.Student;
import ttl.larku.service.StudentService;

@SpringBootApplication
//@Configuration
//@ComponentScan
//@EnableAutoConfiguration
public class AnilsSbdemoApplication {

   public static void main(String[] args) {
      SpringApplication.run(AnilsSbdemoApplication.class, args);
   }

}

//Create a bean that implements the CommandLineRunner interface
//Print out some message from the "run" method.
@Component
class MyRunner implements CommandLineRunner {

   @Autowired
   private StudentService studentService;

   @Override
   public void run(String... args) throws Exception {
      System.out.println("Here we go");

      var input = List.of(
            new Student("Joe", "383 939 93393", Student.Status.HIBERNATING),
            new Student("Franny", "84949 939 93393", Student.Status.PART_TIME),
            new Student("Sameer", "383 939 93393", Student.Status.FULL_TIME),
            new Student("Rachna", "383 939 93393", Student.Status.FULL_TIME)
            );

      input.forEach(studentService::createStudent);

      List<Student> students = studentService.getAllStudents();

      System.out.println("students: " + students);
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
//      System.out.println("Here we go");
//
//      List<Track> tracks = trackService.getAllTracks();
//
//      System.out.println("tracks : " + tracks);
//   }
//}
