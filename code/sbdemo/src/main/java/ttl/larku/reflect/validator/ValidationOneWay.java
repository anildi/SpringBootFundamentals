package ttl.larku.reflect.validator;

import ttl.larku.domain.Student;

/**
 * @author whynot
 */
public class ValidationOneWay {

    public void fun() {
        Car car = new Car();
        car.setModelName("Honda");

        boolean valid = validateCar(car);
        if(!valid) {
//            error();
        } else {
            //do some work
        }

    }

    public boolean validateCustomer(Student c) {

        return true;
    }

    public boolean validateCar(Car c) {
        if(c.getModelName() == null) {
            return false;
        }


        return true;
    }
}
