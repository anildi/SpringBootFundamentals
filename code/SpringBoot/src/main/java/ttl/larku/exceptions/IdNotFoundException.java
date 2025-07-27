package ttl.larku.exceptions;

public class IdNotFoundException extends RuntimeException{
   private int id;

   public IdNotFoundException(int id, String message) {
      super(message);
      this.id = id;
   }

   public IdNotFoundException(int id) {
      this(id, null);
   }

   public int getId() {
      return id;
   }
}
