package ttl.larku.reflect.validator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ObjectCreator {

	static class Person {
		private String name;
		private String phoneNumber;
		private int code;

		public Person() {
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPhoneNumber() {
			return phoneNumber;
		}

		public void setPhoneNumber(String phoneNumber) {
			this.phoneNumber = phoneNumber;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		@Override
		public String toString() {
			return "Person [name=" + name + ", phoneNumber=" + phoneNumber + ", code=" + code + "]";
		}

	}

	public static void main(String[] args) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		ObjectCreator oc = new ObjectCreator();

		String json = "{ \"name\" :  \"Moby\", \"phoneNumber\" : \"282 92929 929\", \"nothere\" : \"nothing\" }";
		Map<String, String> propsFromJson = new HashMap<>();
		propsFromJson.put("name", "Moby");
		propsFromJson.put("phoneNumber", "282 92929 929");
		propsFromJson.put("nothere", "nothing");

		Person p = oc.createObject(Person.class, propsFromJson);

		System.out.println("person is " + p);
	}

	/**
	 * Create an object of the specified className and populate it with any matching
	 * properties from the json String. We are only dealing with String properties
	 * 
	 * @param clazz
	 * @param properties
	 * @return the created and initialized Object
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public <T> T createObject(Class<T> clazz, Map<String, String> properties)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		Object instance = clazz.getConstructor().newInstance();

		for (Map.Entry<String, String> entry : properties.entrySet()) {
			String k = entry.getKey();
			String v = entry.getValue();
			String setMethodName = "set" + k.substring(0, 1).toUpperCase() + k.substring(1);
			System.out.println(k + "=" + v);
			try {
				Method setMethod = clazz.getMethod(setMethodName, String.class);
				if (setMethod != null) {
					setMethod.invoke(instance, v);
					System.out.println("Called " + setMethod + " for prop " + k + "=" + v);
				}
			} catch (NoSuchMethodException nsme) {
				System.out.println(nsme);
				continue;
			}
		}

		return clazz.cast(instance);
	}
}
