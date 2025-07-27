package ttl.larku.app;

import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ttl.larku.TrackerConfig;
import ttl.larku.domain.Track;
import ttl.larku.service.TrackService;

public class PlaylistApp {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext appContext =
                new AnnotationConfigApplicationContext();
        appContext.getEnvironment().setActiveProfiles("development");
        //appContext.getEnvironment().setActiveProfiles("production");
        appContext.register(TrackerConfig.class);
        appContext.refresh();

        TrackService ts = appContext.getBean("trackService", TrackService.class);

        List<Track> tracks = ts.getAllTracks();
        tracks.forEach(System.out::println);


    }
}
