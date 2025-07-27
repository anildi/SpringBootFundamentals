package ttl.larku.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ttl.larku.domain.Track;
import ttl.larku.service.TrackService;

public class PlaylistApp {

    public static void main(String[] args) {
        new PlaylistApp().go();
    }

    public void go() {
        TrackService ts = new TrackService();
        initService(ts);
        List<Track> playList = ts.getAllTracks();

        playList.forEach(System.out::println);

        //Artists for Tracks with durations < 500
        List<String> artists = playList.stream().filter(t -> {
            if (t.getDuration() == null) {
                return false;
            }
            int duration = Integer.valueOf(t.getDuration().replace(":", ""));
            return duration < 500;
        }).map(Track::getArtist)
                .collect(Collectors.toList());

        //Map of two elements: Artist names for artists with tracks
        //less than 500, and all others
        Map<Boolean, List<String>> part = playList.stream()
                .collect(Collectors.partitioningBy(t -> {
                    if (t.getDuration() == null) {
                        return false;
                    }
                    int duration = Integer.valueOf(t.getDuration().replace(":", ""));
                    return duration < 500;
                }, Collectors.mapping(Track::getArtist, Collectors.toList())));


        part.forEach((k, v) -> System.out.println(k + " : " + v));

    }

    public void initService(TrackService ts) {
        tracks().forEach(t -> ts.createTrack(t));
    }

    public List<Track> tracks() {
        List<Track> tracks = new ArrayList<>();
        tracks.add(new Track("The Shadow Of Your Smile", "Big John Patton", "Let 'em Roll", "06:15", "1965"));
        tracks.add(new Track("I'll Remember April", "Jim Hall and Ron Carter", null, null, null));
        tracks.add(new Track("What's New", "John Coltrane", "Ballads", "03:47", null));
        tracks.add(new Track("Leave It to Me", "Herb Ellis", "Three Guitars in Bossa Nova Time", "03:13", "1963"));

        tracks.add(new Track("Have you met Miss Jones", "George Van Eps", "Pioneers of the Electric Guitar", "02:18",
                "2013"));

        tracks.add(new Track("My Funny Valentine", "Johnny Smith", "Moonlight in Vermont", "02:48", null));

        return tracks;
    }
}
