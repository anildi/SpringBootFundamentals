package ttl.larku.reflect.validator;

import java.util.List;

public class SomeApplication {

	public static void main(String [] args) throws IllegalArgumentException, IllegalAccessException {
		
		
		//Get a car Object from somewhere, say over the web
		Car car = new Car();
		car.setModelName("MyModel");
		car.setNumDoors(2);

		car.setStockNumber("A-1847");
		
		ObjectValidator validator = new ObjectValidator();
		List<String> results = validator.validate(car);
		
		System.out.println("Results:" );
		for(String result : results) {
			System.out.println(result);
		}
		
	}
}
