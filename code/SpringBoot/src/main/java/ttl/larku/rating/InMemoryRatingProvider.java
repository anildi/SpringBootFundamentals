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

   private double upperLimit = 5.0;

   private DecimalFormat decimalFormat = new DecimalFormat("0.0");

   @Override
   public String getRating(int id) {
      var rd = ThreadLocalRandom.current().nextDouble(upperLimit);
      var rating = new BigDecimal(String.valueOf(rd)).setScale(1, RoundingMode.CEILING);

      var result = decimalFormat.format(rating);
      return "InMem:" + result;
   }
}
