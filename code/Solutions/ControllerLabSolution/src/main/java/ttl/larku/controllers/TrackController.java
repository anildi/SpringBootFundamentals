package ttl.larku.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ttl.larku.domain.Track;
import ttl.larku.service.TrackService;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/track")
public class TrackController {

    private TrackService trackService;
    private UriCreator uriCreator;

    //Constructor Injection.
    public TrackController(TrackService trackService, UriCreator uriCreator) {
        this.trackService = trackService;
        this.uriCreator = uriCreator;
    }

    /**
     * A very simple example of passing in query parameters to specify search criteria.
     * Should be called like: http://localhost:8080/track?title=Blah%20Blah%20Blah&album=OtherBlah.
     * We capture all the passed in parameters, if any.  Look in the service and DAO to
     * how we process them.
     *
     * @param queryStrings the query params in a map
     * @return a possibly empty list of tracks
     */
    @GetMapping
    public ResponseEntity<?> getAllTracks(@RequestParam Map<String, String> queryStrings) {
        List<Track> tracks = null;
        if(queryStrings.size() == 0) {
            tracks = trackService.getAllTracks();
        } else {
            tracks = trackService.getTracksBy(queryStrings);
        }
        return ResponseEntity.ok(tracks);
    }

    /**
     * POST is allowed to do anything.
     * An example of using a POST for a query.  We need
     * POST because Spring MVC does not allow a body in a
     * GET request.
     * Here we are doing a simple version of query by example.
     * The user sends us a Track as json with the query fields
     * filled in, everything else null.
     *
     * @param example
     * @return
     */
    @PostMapping("/query")
    public ResponseEntity<?> getAllTracksWithBody(@RequestBody Track example) {
        List<Track> result = trackService.getTracksBy(example);
        return ResponseEntity.ok(result);
    }


    /**
     * Numbers will come here
     *
     * @param id
     * @return
     */
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<?> getTrack(@PathVariable("id") int id) {
        Track track = trackService.getTrack(id);
        if (track == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(track);
    }

    /**
     * Starting with alpha will come in here
     *
     * @param otherStuff
     * @return
     */
    @GetMapping("/{otherstuff:[a-z].*}")
    public ResponseEntity<?> getOtherStuff(@PathVariable("otherstuff") String otherStuff) {
        return ResponseEntity.ok(otherStuff);
    }

    @PostMapping
    public ResponseEntity<?> addTrack(@RequestBody Track track) {
        Track newTrack = trackService.createTrack(track);

        URI uri = uriCreator.getUriFor(newTrack.getId());
//        URI uri = ServletUriComponentsBuilder
//                .fromCurrentRequest()
//                .path("/{id}")
//                .buildAndExpand(newTrack.getId())
//                .toUri();

        return ResponseEntity.created(uri).body(newTrack);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrack(@PathVariable("id") int id) {
        if (trackService.deleteTrack(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping
    public ResponseEntity<?> updateTrack(@RequestBody Track track) {
        if(trackService.updateTrack(track)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Do partial updates.  The data comes to us as a map of property, value.
     * We use reflection in trackService.updateStudentPartial to do the updates.
     *
     * @param id
     * @param props
     * @return
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateStudentPartial(@PathVariable("id") int id, @RequestBody Map<String, Object> props) {
//        regService.getStudentService().updateStudentPartial(id, props);
        trackService.updateTrackPartialBeanWrapper(id, props);
        return ResponseEntity.noContent().build();

    }

}
