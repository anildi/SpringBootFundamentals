package ttl.larku;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import ttl.larku.domain.Track;
import ttl.larku.service.TrackService;

import java.util.List;

@SpringBootApplication
public class TrackerApp {
    /**
     * In this case we have no web dependencies, and so no
     * web environment.
     * A CommandLineRunner is useful in this context to actually
     * get some work done.
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(TrackerApp.class);
    }
}

@Component
class AppRunner implements CommandLineRunner {
    private final TrackService trackService;

    //Constructor Injection
    public AppRunner(TrackService trackService) {
        this.trackService = trackService;
    }

    @Override
    public void run(String... args) throws Exception {
        List<Track> tracks = trackService.getAllTracks();
        System.out.println("Tracks:");
        tracks.forEach(System.out::println);
    }
}
