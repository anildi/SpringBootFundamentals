package ttl.larku.didemo.circular;

import java.lang.reflect.Field;

/**
 * Create a crude Java version of @Lazy.
 * @author whynot
 */

class X {
    private final Y y;
    public X(Y y) {
        this.y = y;
//        System.out.println("A::Ctor, a class in A" + this.getClass() + ", a class in B: " + this.b.getA().getClass() + ", z: " + z);
    }

    public void doX() {
        System.out.println("Doing X");
    }

    public void callY() {
        y.doY();
    }

//    @Autowired
//    public void setY(Y y) {
//        this.y = y;
//    }
}

class Xprime extends X
{
    public Xprime(Y y) {
        super(y);
    }

}

class Y {
    private X x;
//    public B(A a) {
//        this.a = a;
//        System.out.println("a's class in B: " + this.a.getClass());
//    }

    public Y(X x) {
        this.x = x;
//        System.out.println("a's class in B: " + this.a.getClass());
    }

    public void callX() {
        x.doX();
        System.out.println("In Y::callX " + this.x.getClass());
    }

    public void doY() {
        System.out.println("Doing Y::doY");
    }

    public X getX() {
        return x;
    }
    //    @Autowired
    public void setX(X x) {
        this.x = x;
    }
}

class OurLazyDriverJava
{
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        //These would come from a factory, so all the Xprime
        //stuff would be invisible to the user.
        Xprime x = new Xprime(null);
        Y  y = new Y(x);

        //Use reflection to set the field.  In this case
        //we don't need a setter.  And we can even
        //reset a 'final' field.
        Class<?> xClass = x.getClass().getSuperclass();
        Field [] fields = xClass.getDeclaredFields();
        Field yField = xClass.getDeclaredField("y");
        yField.setAccessible(true);
        yField.set(x, y);

        y.callX();
        x.callY();
//        b.setA(a);
//        a.setB(b);
    }
}