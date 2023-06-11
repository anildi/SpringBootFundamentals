package ttl.larku.service;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Track;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TrackService {

    private BaseDAO<Track> trackDAO;

    public TrackService(BaseDAO<Track> trackDAO) {
        this.trackDAO = trackDAO;
    }

    public Track createTrack(String title) {
        Track track = Track.title(title).build();
        track = trackDAO.create(track);

        return track;
    }

    public Track createTrack(String title, String artist, String album, String duration, String date) {
        return Track.title(title).artist(artist).album(album).duration(duration).date(date).build();
    }

    public Track createTrack(Track track) {
        track = trackDAO.create(track);

        return track;
    }

    public boolean deleteTrack(int id) {
        Track track = trackDAO.get(id);
        if (track != null) {
            return trackDAO.delete(track);
        }
        return false;
    }

    public boolean updateTrack(Track newTrack) {
        Track oldTrack = trackDAO.get(newTrack.getId());
        if(oldTrack != null) {
            return trackDAO.update(newTrack);
        }
        return false;
    }

    public Track getTrack(int id) {
        return trackDAO.get(id);
    }

    public List<Track> getAllTracks() {
        return trackDAO.getAll();
    }


    public List<Track> getTracksByTitle(String title) {
        Track example = Track.title(title).build();

        List<Track> found = getTracksBy(example);

        return found;
    }

    public List<Track> getTracksBy(Map<String, String> queryStrings) {
        Track.Builder builder = new Track.Builder();
        queryStrings.forEach((k, v) -> {
            if(k.equals("title")) {
                builder.title(v);
            } else if(k.equals("artist")) {
                builder.artist(v);
            } else if(k.equals("album")) {
                builder.album(v);
            }
        });
        Track example = builder.build();

        List<Track> result = getTracksBy(example);
        return result;
    }

    public List<Track> getTracksBy(Track example) {
       List<Track> result = trackDAO.getByExample(example);
       return result;
    }


    /**
     * Partial update of tracks.  Here we do it "by hand", i.e
     * use the Reflection API directly.  We try to match
     * property names in the input map to corresponding "set"Blah
     * methods in the object.
     *
     * @param id
     * @param props
     */
    public void updateTrackPartial(int id, Map<String, Object> props) {
        Track track = trackDAO.get(id);
        if (track != null) {
            Class<?> clazz = Track.class;
            Map<String, Method> methods = Arrays
                    .asList(clazz.getMethods()).stream()
                    .filter(m -> m.getName().startsWith("set"))
                    .collect(Collectors.toMap(m -> m.getName(), m -> m));

            props.forEach((name, value) -> {
                if (!name.equals("id")) {
                    String setMethodName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
                    Method m = methods.get(setMethodName);
                    if (m != null) {
                        Class<?>[] paramTypes = m.getParameterTypes();
                        if (paramTypes.length == 1) {
                            Object param = value;
                            try {
                                Class<?> pClass = paramTypes[0];
                                //We only handle String properties for now.
                                if (pClass.equals(String.class)) {
                                    param = String.valueOf(value);
                                }
                                m.invoke(track, param);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * Here we use the handy Spring BeanWrapper class to make our lives
     * that little bit simpler.
     *
     * @param id  The id of the resource to update.
     * @param props The properties to update.
     */
    public void updateTrackPartialBeanWrapper(int id, Map<String, Object> props) {
        Track track = trackDAO.get(id);
        if (track != null) {

            BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(track);
            props.forEach((name, value) -> {
                if (!name.equals("id")) {
                    if (bw.isWritableProperty(name)) {
                        Class<?> pClass = bw.getPropertyType(name);
                        bw.setPropertyValue(name, bw.convertIfNecessary(value, pClass));
                    }
                }
            });
        }
    }


    public BaseDAO<Track> getTrackDAO() {
        return trackDAO;
    }

    public void setTrackDAO(BaseDAO<Track> trackDAO) {
        this.trackDAO = trackDAO;
    }

    public void clear() {
        trackDAO.deleteStore();
        trackDAO.createStore();
    }

}
