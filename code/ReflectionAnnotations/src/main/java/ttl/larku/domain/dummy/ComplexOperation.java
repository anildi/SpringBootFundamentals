package ttl.larku.domain.dummy;

public class ComplexOperation implements BaseOperation {
    @Override
    public int operation(int a, int b) {
        if (a < 0 || b < 10) {
            throw new RuntimeException("Values are bad, [" + a + ", " + b + "]");
        }

        int result = a % b + 47;

        return result;

    }
    
    public int getAValue(int i) {
    	if(i < 10) {
    		return 3;
    	}
    	else if(i < 20) {
    		return 4;
    	}
    	else {
    		return 5;
    	}
    }
}
