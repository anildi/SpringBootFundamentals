package ttl.larku.reflect.validator;

import java.util.List;
import java.util.ArrayList;

/**
 * This example illustrates
 * the use of the @SupressWarnings
 * annotation.
 */
public class SupressWarningsExample {

  @SuppressWarnings({"unchecked"})
  public List buildList() {
    List intList = new ArrayList();
    intList.add(1);
    return intList;
  }

}
