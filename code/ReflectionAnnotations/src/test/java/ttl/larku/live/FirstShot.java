package ttl.larku.live;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author whynot
 */

class ClassToTest
{
    private int i;
    public ClassToTest(int i) {
        this.i =i;
    }

    public int getI() {
        return i;
    }
}

public class FirstShot {

    @Test
    public void firstGuy() {
        ClassToTest ti = new ClassToTest(10);

        assertEquals(10, ti.getI());
    }

}
