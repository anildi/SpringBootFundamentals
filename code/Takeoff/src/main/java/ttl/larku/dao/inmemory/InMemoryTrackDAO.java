package ttl.larku.dao.inmemory;

import ttl.larku.domain.Track;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryTrackDAO {

    private Map<Integer, Track> tracks = new HashMap<Integer, Track>();
    private static AtomicInteger nextId = new AtomicInteger(0);

    public boolean update(Track updateObject) {
        if (tracks.containsKey(updateObject.getId())) {
            tracks.put(updateObject.getId(), updateObject);
            return true;
        }
        return false;
    }

    public boolean delete(Track track) {
        return tracks.remove(track.getId()) != null;
    }

    public Track create(Track newObject) {
        //Create a new Id
        int newId = nextId.getAndIncrement();
        newObject.setId(newId);
        tracks.put(newId, newObject);

        return newObject;
    }

    public Track get(int id) {
        return tracks.get(id);
    }

    public List<Track> getAll() {
        return new ArrayList<Track>(tracks.values());
    }

    public void deleteStore() {
        tracks = null;
    }

    public void createStore() {
        tracks = new HashMap<Integer, Track>();
        nextId = new AtomicInteger(0);
    }

    public Map<Integer, Track> getTracks() {
        nextId = new AtomicInteger(0);
        return tracks;
    }

    public void setTracks(Map<Integer, Track> tracks) {
        this.tracks = tracks;
    }
}
