package ttl.larku.service;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import ttl.larku.dao.repository.TrackRepository;
import ttl.larku.domain.Track;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;

@Service
public class TrackRepoService {

    private TrackRepository trackDAO;

    public TrackRepoService(TrackRepository trackDAO) {
        this.trackDAO = trackDAO;
    }

    public Track createTrack(String title) {
        Track track = Track.title(title).build();
        track = trackDAO.save(track);

        return track;
    }

    public Track createTrack(String title, String artist, String album, String duration, String date) {
        return Track.title(title).artist(artist).album(album).duration(duration).date(date).build();
    }


    public Track createTrack(Track track) {
        track = trackDAO.save(track);

        return track;
    }

    public boolean deleteTrack(int id) {
        Track track = trackDAO.findById(id).orElse(null);
        if (track != null) {
            trackDAO.delete(track);
            return true;
        }
        return false;
    }

    public boolean updateTrack(Track newTrack) {
        Track oldTrack = trackDAO.findById(newTrack.getId()).orElse(null);
        if(oldTrack != null) {
            trackDAO.save(newTrack);
            return true;
        }
        return false;
    }

    public List<Track> getTracksByTitle(String title) {
        String lc = title.toLowerCase();
        List<Track> found = trackDAO.findByTitleIgnoreCaseContaining(title);

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

    /**
     * We are using the Query By Example feature of Spring Data JPA.
     * We can make an Example from the "probe" (example) passed
     * in.  We can also specify how we would like things to
     * be matched using the ExampleMatcher.  The actual matching
     * is done by the library.
     * @param probe
     * @return
     */
    public List<Track> getTracksBy(Track probe) {
        //Here we can set up rules on how to do the matching.
        //We are selecting anything that matches on any property (matchingAny),
        // matching the "artist" property with contains,
        // matching the "title" property with contains,
        // matching the "album" property with contains,
        // not looking at the "id" property,
        // ignoring case for all matches,
        //and not matching null values.
        ExampleMatcher matcher = ExampleMatcher.matchingAny()
                .withMatcher("artist", contains())
                .withMatcher("title", contains())
                .withMatcher("album", contains())
                .withIgnorePaths("id")
                .withIgnoreCase()
                .withIgnoreNullValues();

        //Now we make our Example from the probe and the matcher
        Example<Track> example = Example.of(probe, matcher);

        List<Track> found = trackDAO.findAll(example);

        return found;
    }

    public Track getTrack(int id) {
        return trackDAO.findById(id).orElse(null);
    }

    public List<Track> getAllTracks() {
        return trackDAO.findAll();
    }

//    public BaseDAO<Track> getTrackDAO() {
//        return trackDAO;
//    }
//
//    public void setTrackDAO(BaseDAO<Track> trackDAO) {
//        this.trackDAO = trackDAO;
//    }

    public void clear() {
//        trackDAO.deleteStore();
//        trackDAO.createStore();
    }


    public void updateTrackPartial(int id, Map<String, Object> props) {
        Track track = trackDAO.findById(id).orElse(null);
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

    public void updateTrackPartialBeanWrapper(int id, Map<String, Object> props) {
        Track track = trackDAO.findById(id).orElse(null);
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

}
