package ttl.larku;

import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import ttl.larku.domain.Track;
import ttl.larku.service.TrackService;

@SpringBootApplication
public class AnilsTrackAppApplication {

   public static void main(String[] args) {
      SpringApplication.run(AnilsTrackAppApplication.class, args);
   }
}

@Component
class TrackRunner implements CommandLineRunner {

   private TrackService trackService;

   TrackRunner(TrackService trackService) {
      this.trackService = trackService;
   }

   @Override
   public void run(String... args) throws Exception {
      List<Track> tracks = trackService.getAllTracks();
      System.out.println("tracks.size: " + tracks.size());
      tracks.forEach(System.out::println);
   }
}
