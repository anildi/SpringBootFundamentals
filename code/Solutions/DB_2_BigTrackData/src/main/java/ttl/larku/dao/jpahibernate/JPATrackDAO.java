package ttl.larku.dao.jpahibernate;

import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Track;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class JPATrackDAO implements BaseDAO<Track> {

    private Map<Integer, Track> tracks = new ConcurrentHashMap<Integer, Track>();
    private AtomicInteger nextId = new AtomicInteger(1);

    @Override
    public boolean update(Track updateObject) {
        return tracks.computeIfPresent(updateObject.getId(), (key, uo) -> updateObject) != null;
    }

    @Override
    public boolean delete(Track deleteObject) {
        return tracks.remove(deleteObject.getId()) != null;
    }

    @Override
    public Track create(Track newObject) {
        //Create a new Id
        int newId = nextId.getAndIncrement();
        newObject.setId(newId);
        tracks.put(newId, newObject);

        return newObject;
    }

    @Override
    public Track get(int id) {
        return tracks.get(id);
    }

    @Override
    public List<Track> getAll() {
        return new ArrayList<Track>(tracks.values());
    }

    @Override
    public List<Track> getByExample(Track example) {
        Predicate<Track> pred = null;
        //We are going to 'or' all the fields in the example object.
        if (example.getTitle() != null) {
            Predicate<Track> titlePred = (t) -> t.getTitle() != null
                    && t.getTitle().toUpperCase().contains(example.getTitle().toUpperCase());
            pred = pred == null ? titlePred : pred.or(titlePred);
        }

        if (example.getArtist() != null) {
            Predicate<Track> titlePred = (t) -> t.getArtist() != null
                    && t.getArtist().toUpperCase().contains(example.getArtist().toUpperCase());
            pred = pred == null ? titlePred : pred.or(titlePred);
        }
        if (example.getAlbum() != null) {
            Predicate<Track> titlePred = (t) -> t.getAlbum() != null
                    && t.getAlbum().toUpperCase().contains(example.getAlbum().toUpperCase());
            pred = pred == null ? titlePred : pred.or(titlePred);
        }
        //etc. etc.

        //If nothing yet, then return all.
        pred = pred == null ? (t) -> true : pred;

        List<Track> result = getAll()
                .stream()
                .filter(pred)
                .collect(Collectors.toList());

        return result;
    }

    @Override
    public void deleteStore() {
        tracks = null;
    }

    @Override
    public void createStore() {
        tracks = new ConcurrentHashMap<>();
        nextId.set(1);
    }

    public Map<Integer, Track> getTracks() {
        nextId.set(1);
        return tracks;
    }

    public void setTracks(Map<Integer, Track> tracks) {
        this.tracks = tracks;
    }
}
