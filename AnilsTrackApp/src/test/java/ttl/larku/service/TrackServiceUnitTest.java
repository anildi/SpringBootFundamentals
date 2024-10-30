package ttl.larku.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Track;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class TrackServiceUnitTest {

   @Mock
   private BaseDAO<Track> trackDAO;

   @InjectMocks
   private TrackService trackService;

   @Test
   public void testCreateTrackWithTrack() {
      String title = "The Moon in August";
      Track track = Track.title(title).build();

      Mockito.when(trackDAO.create(track)).thenReturn(track);

      Track newTrack = trackService.createTrack(track);

      assertSame(track, newTrack);

      Mockito.verify(trackDAO).create(track);
   }

   @Test
   public void testCreateTrackWithTitle() {
      String title = "The Moon in August";
      Track track = Track.title(title).build();

      Mockito.when(trackDAO.create(any())).then(returnsFirstArg());

      Track newTrack = trackService.createTrack(title);

      assertEquals(title, newTrack.getTitle());

      Mockito.verify(trackDAO).create(any());
   }

}
