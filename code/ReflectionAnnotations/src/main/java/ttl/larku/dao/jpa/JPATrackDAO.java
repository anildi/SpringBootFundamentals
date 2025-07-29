package ttl.larku.dao.jpa;

import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Track;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class JPATrackDAO implements BaseDAO<Track> {

	private Map<Integer, Track> tracks = new HashMap<Integer, Track>();
	private static AtomicInteger nextId = new AtomicInteger(1);

	@Override
	public boolean update(Track updateObject) {
		return tracks.computeIfPresent(updateObject.getId(), (k, oldValue) -> updateObject) != null;
	}

	@Override
	public boolean delete(Track track) {
		return tracks.remove(track.getId()) != null;
	}

	@Override
	public Track create(Track newObject) {
		//Create a new Id
		int newId = nextId.getAndIncrement();
		newObject.setId(newId);
		newObject.setAlbum("JPA: " + newObject.getAlbum());
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
	
	public Map<Integer, Track> getTracks() {
		nextId.set(1);
		return tracks;
	}

	public void setTracks(Map<Integer, Track> tracks) {
		this.tracks = tracks;
	}
	
	public void deleteStore() {
		tracks = null;
		nextId.set(1);
	}
	
	public void createStore() {
		tracks = new ConcurrentHashMap<>();
		nextId.set(1);
	}
}
