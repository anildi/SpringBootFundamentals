package ttl.larku.rating;

public interface RatingProvider {
   default String getRating(int id) {
      throw new UnsupportedOperationException("Needs Implementing");
   }

   default String getRating(int id, String user, String pw) {
      throw new UnsupportedOperationException("Needs Implementing");
   }
}
