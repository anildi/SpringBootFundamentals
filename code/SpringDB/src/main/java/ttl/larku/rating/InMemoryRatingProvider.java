package ttl.larku.rating;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("! networkrating")
public class InMemoryRatingProvider implements RatingProvider {

   private double lowerLimit = -5.0;
   private double upperLimit = 5.0;

   private DecimalFormat decimalFormat = new DecimalFormat("0.0");

   @Override
   public BigDecimal getRating(int id) {
      var rd = ThreadLocalRandom.current().nextDouble(lowerLimit, upperLimit);
      var rating = new BigDecimal(String.valueOf(rd)).setScale(1, RoundingMode.CEILING);

      return rating;
   }

   @Override
   public BigDecimal getRating(int id, String user, String pw) {
      return getRating(id);
   }
}
