package ttl.larku;

import java.util.List;
import javax.sound.midi.Track;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import ttl.larku.domain.Student;
import ttl.larku.service.StudentService;

@SpringBootApplication //(scanBasePackages = {"ttl.larku", "org.ttl"})
//@Configuration
//@ComponentScan
//@EnableAutoConfiguration
public class SbdemoApplication {

   public static void main(String[] args) {
      SpringApplication.run(SbdemoApplication.class, args);
   }
}

//Create a Bean that implements command line runner.
//Print out a message from the run method of your bean.

@Component
class MyRunner implements CommandLineRunner {

   @Autowired
   private StudentService studentService;

   @Override
   public void run(String... args) throws Exception {
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
