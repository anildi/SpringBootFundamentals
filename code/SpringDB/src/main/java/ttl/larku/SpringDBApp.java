package ttl.larku;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;
import ttl.larku.domain.Student;
import ttl.larku.service.StudentService;

@SpringBootApplication
//@EnableJpaRepositories
public class SpringDBApp {
   public static void main(String[] args) {
      SpringApplication springApp = new SpringApplication(SpringDBApp.class);

      springApp.run(args);
   }
}

@Component
@Profile("makemanystudents")
class MakeManyStudents implements CommandLineRunner {
   @Autowired
   private StudentService studentService;

   @Override
   public void run(String... args) throws Exception {
      getFunky();
      Student.Status [] statuses = Student.Status.values();
      for (int i = 0; i < 100; i++) {
         Student student = new Student(randomLetters(ThreadLocalRandom.current().nextInt(2, 8)) + " "
               + randomLetters((ThreadLocalRandom.current().nextInt(2, 10))),
               randomDigits(3) + " "
               + randomDigits(2) + " "
               + randomDigits(5),
            LocalDate.of(ThreadLocalRandom.current().nextInt(1900, 2024),
                  ThreadLocalRandom.current().nextInt(1, 13), ThreadLocalRandom.current().nextInt(1, 29)),
               statuses[ThreadLocalRandom.current().nextInt(statuses.length)]
         );

         studentService.createStudent(student);
      }
   }


   String digits = "012346789";
   int digitsLength = digits.length();
   public String randomDigits(int length) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < length; i++) {
         sb.append(digits.charAt(ThreadLocalRandom.current().nextInt(digitsLength)));
      }
      return sb.toString();
   }

   String letters = getFunky();
   int lettersLength = letters.length();
   public String randomLetters(int length) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < length; i++) {
         sb.append(letters.charAt(ThreadLocalRandom.current().nextInt(2, lettersLength)));
      }
      return sb.toString();
   }

   public String getFunky() {
      String basic = "abcdefghigklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
      StringBuilder sb = new StringBuilder(basic);
      for(int i = 0x00C0; i < 0x00DF; i++) {
         sb.append((char)i);
      }

      String result = sb.toString();
      return result;
   }

}




