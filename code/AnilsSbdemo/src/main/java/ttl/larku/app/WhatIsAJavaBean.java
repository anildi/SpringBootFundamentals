package ttl.larku.app;

/**
 * A JavaBean is a class with get/set methods and,
 * usually, a zero argument constructor.
 * The key thing is that a JavaBean has properties whose
 * names are governed by the names of the get/set methods,
 * NOT the names of variables.
 * <p>
 * So this is a JavaBean which has a read/write property
 * called 'age'.  The lower case 'a' is important.
 *
 * @author whynot
 */
public class WhatIsAJavaBean {

    private int xyz;

    public int getAge() {
        return xyz;
    }

    public void setAge(int age) {
        this.xyz = age;
    }


}
