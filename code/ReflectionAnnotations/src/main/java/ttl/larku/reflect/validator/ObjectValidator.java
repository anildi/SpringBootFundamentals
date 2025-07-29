package ttl.larku.reflect.validator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ObjectValidator {

	public List<String> validate(Object target)
			throws IllegalArgumentException, IllegalAccessException {
		Class<?> clazz = target.getClass();

		Field[] fields = clazz.getDeclaredFields();
		List<String> results = new ArrayList<>();
		
		boolean allGood = false;
		for (Field field : fields) {
			field.setAccessible(true);
			if (field.isAnnotationPresent(MyNotNull.class)) {
				allGood = handleNotNull(target, field);
				if(!allGood) {
					results.add("NotNull: " + field.getName() + 
							" failed . Failed Value is " + field.get(target));
				}
			}
			if (field.isAnnotationPresent(MyLength.class)) {
				allGood = handleLength(target, field);
				if(!allGood) {
					results.add("Length: " + field.getName() + 
							" failed . Failed Value is " + field.get(target));
				}
			}
			if (field.isAnnotationPresent(MyStockNumber.class)) {
				allGood = handleStockNumber(target, field);
				if(!allGood) {
					results.add("StockNumber: " + field.getName() + 
							" failed . Failed Value is " + field.get(target));
				}
			}
			
		}
		
		if(allGood) {
			postProcess();
		}
		
		return results;
	}
	
	public void postProcess() {
		
	}

	
	public boolean handleLength(Object target, Field field) throws IllegalArgumentException, IllegalAccessException {

		MyLength uc = (MyLength) field.getAnnotation(MyLength.class);

		// Get the old value
		String value = (String) field.get(target);
		
		//Don't handle Null here
		if(value == null) return true;

		int min = uc.min();
		int max = uc.max();
		int length = value.length();
		
		return length >= min && length <= max;
	}
	
	public boolean handleNotNull(Object target, Field field) throws IllegalArgumentException, IllegalAccessException {

		// Get the old value
		String value = (String) field.get(target);
		
		return value != null;
	}
	
	public void handleUpperCase(Object target, Field field) throws IllegalArgumentException, IllegalAccessException {

		UpperCase uc = (UpperCase) field.getAnnotation(UpperCase.class);

		// Get the old value
		String oldValue = (String) field.get(target);

		int length = uc.length();
		if (length < 0) {
			length = oldValue.length();
		}

		char[] chars = oldValue.toCharArray();

		StringBuilder newValue = new StringBuilder();
		for (int i = 0, j = 0; i < chars.length; i++, j++) {
			char c = chars[i];
			if (j < length) {
				c = Character.toUpperCase(chars[i]);
			}
			newValue.append(c);
		}

		// Set the new value
		field.set(target, newValue.toString());
	}

	private String vinRegEx = "^[A-Z]-[0-9]{4}$";

	public boolean handleStockNumber(Object target, Field field) throws IllegalArgumentException, IllegalAccessException {

		String value = (String) field.get(target);
		
		boolean result = true;
		if(value == null || !value.matches(vinRegEx)) {
			result = false;
		}
		
		return result;
	}
}
