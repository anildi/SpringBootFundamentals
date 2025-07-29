package ttl.larku.domain.dummy;

public class SimpleOperation implements BaseOperation
{
    @Override
    public int operation(int a, int b) {
        if(a < 0 || b < 10) {
            throw new RuntimeException("Values are bad, [" + a + ", " + b + "]");
        }

        int result = a + b;

        return result;

    }
}


