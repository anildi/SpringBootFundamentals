package ttl.larku.rating;

import java.math.BigDecimal;

public interface RatingProvider {
   BigDecimal getRating(int id);

   BigDecimal getRating(int id, String user, String pw);
}
