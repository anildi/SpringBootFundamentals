package ttl.otherpack;

import ttl.larku.reflect.inject.OtherInterface;

/**
 * @author whynot
 */
public class OtherImportantService implements OtherInterface {

    @Override
    public String doStuff(String stuff) {
        return "Did stuff with: " + stuff;
    }
}
