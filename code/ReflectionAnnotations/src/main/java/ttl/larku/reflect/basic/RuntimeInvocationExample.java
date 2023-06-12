package ttl.larku.reflect.basic;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author developintelligence llc
 * @version 1.0
 */
public class RuntimeInvocationExample {

	public static void main(String[] args) {
		String className = "ttl.larku.reflect.basic.OtherClass";
		try {
			// get the class
			Class<?> clazz = getClasss(className);

			// OtherClass oc = new OtherClass();

			// Make the object

			//OtherClass oc = new OtherClass();

//			Object clazzInstance = clazz.getDeclaredConstructor(int.class).newInstance(10);

			//Can get all constructors
//			Constructor<?> [] ctors = clazz.getDeclaredConstructors();
//
//			for(Constructor<?> c : ctors) {
//				System.out.println(c);
//			}


//			Constructor<?> ctor = clazz.getDeclaredConstructor();
//			Object clazzInstance = ctor.newInstance();

			Constructor<?> ctor = clazz.getDeclaredConstructor(int.class);
			Object clazzInstance = ctor.newInstance(10);

			// Find the doStuff Method
			Method method = clazz.getMethod("doStuff", String.class);

			// Object result = method.invoke(clazzInstance, new
			// Object[]{"hello" });

			//oc.doStuff("hello");
			Object result = method.invoke(clazzInstance, "hello");

			// print the results
			System.out.println(className + " dostuff result: " + result);

			messWithFields(clazzInstance, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println(clazzInstance);
	}

	private static Class<?> getClasss(String className)
			throws ClassNotFoundException {
		Class<?> returnValue = null;
		returnValue = Class.forName(className);
		return returnValue;
	}

	@SuppressWarnings("unused")
	private static void messWithFields(Object clazzInstance, Class<?> clazz)
			throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {

		Field[] allFields = clazz.getDeclaredFields();

		for (Field f : allFields) {
			System.out.println(f);
		}

		Field field = clazz.getDeclaredField("i");
		//oc.i = 100;
		field.setAccessible(true);

		field.set(clazzInstance, 100);
	}
}

class OtherClass {
	private int i = 0;

	public OtherClass() {
	}

	public OtherClass(int x) {
		i = x;
	}

	public void doStuff(int i) {
	}

	public String doStuff(String stuff) {
		System.out.println("do Stuff called with " + stuff);
		return stuff;
	}

	@Override
	public String toString() {
		return "OtherClass [i=" + i + "]";
	}
	
	public void fun(String s, Integer ...integers ) {
		
		for(Integer it : integers) {
			System.out.println(it);
		}
		
	}

}