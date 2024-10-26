package ttl.larku.service;

import ttl.larku.dao.inmemory.InMemoryTrackDAO;
import ttl.larku.domain.Track;

import java.util.List;
import java.util.function.Predicate;

public class TrackService {

    private InMemoryTrackDAO trackDAO;

    public TrackService() {
        trackDAO = new InMemoryTrackDAO();
    }

    public Track createTrack(String title) {
        //Track track = Track.title(title).build();
        Track track = new Track(title, null, null, null, null);
        //Or
        //Track track = new Track();
        //track.setTitle(title);
        track = trackDAO.create(track);

        return track;
    }

    public Track createTrack(String title, String artist, String album, String duration, String date) {
        //return Track.title(title).artist(artist).album(album).duration(duration).date(date).build();
        return new Track(title, artist, album, duration, date);
    }

    public Track createTrack(Track track) {
        track = trackDAO.create(track);

        return track;
    }

    public void deleteTrack(int id) {
        Track track = trackDAO.get(id);
        if (track != null) {
            trackDAO.delete(track);
        }
    }

    public void updateTrack(Track track) {
        trackDAO.update(track);
    }

    public Track getTrack(int id) {
        return trackDAO.get(id);
    }

    public List<Track> getTracksByTitle(String title) {
        String lc = title.toLowerCase();
        Predicate<Track> pred = (t) -> t.getTitle().toLowerCase().contains(lc);
        List<Track> found = getTracksBy(pred);

        return found;
    }

    /**
     * We use Predicate to create a general query mechanism
     *
     * @param pred
     * @return
     */
    public List<Track> getTracksBy(Predicate<Track> pred) {
        throw new UnsupportedOperationException("GetTracksBy not implemented yet");
    }

    public List<Track> getAllTracks() {
        return trackDAO.getAll();
    }

    public InMemoryTrackDAO getTrackDAO() {
        return trackDAO;
    }

    public void setTrackDAO(InMemoryTrackDAO trackDAO) {
        this.trackDAO = trackDAO;
    }

    public void clear() {
        trackDAO.deleteStore();
        trackDAO.createStore();
    }

}
