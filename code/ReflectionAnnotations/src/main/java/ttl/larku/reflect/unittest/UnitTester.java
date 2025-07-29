package ttl.larku.reflect.unittest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class UnitTester {

	public static void main(String[] args) throws ClassNotFoundException {
		String className = args.length == 0 ? "ttl.larku.reflect.unittest.TestCase" : args[0];
		
		UnitTester unitTester = new UnitTester();

		Class<?> testCaseClass = Class.forName(className);
		Map<String, Boolean> results = unitTester.runTests(testCaseClass);

		Set<Map.Entry<String, Boolean>> entries = results.entrySet();
		for(Map.Entry<String, Boolean> entry : entries) {
			System.out.println("Method " + entry.getKey() + (entry.getValue() ? " passed" : " failed"));
			
		}
	}

	private Class<? extends Annotation> testMethodAnnotation = TestMethod.class;
	private Map<String, Boolean> results;

	public UnitTester() throws ClassNotFoundException {
	}

	public Map<String, Boolean> runTests(Class<?> testCaseClass) {
		results = new LinkedHashMap<>();
		try {
			// Create the instance of the Test Class
			Object testCase = testCaseClass.newInstance();

			// Get all the methods in the testClass
			Method[] methods = testCaseClass.getDeclaredMethods();
			// Cycle through the methods
			for (Method method : methods) {
				// Is the @TestMethod annotation present
				if (method.isAnnotationPresent(testMethodAnnotation)) {
					Class<?>[] paramTypes = method.getParameterTypes();
					// Test Method takes no arguments
					if (paramTypes.length == 0) {
						Class<?> returnType = method.getReturnType();
						// Return type of Test Method is boolean
						if (returnType == Boolean.class || returnType == boolean.class) {
							//Boolean result = (Boolean) method.invoke(testCase, (Object[]) new Class[] {});
							Boolean result = (Boolean) method.invoke(testCase);
							results.put(method.getName(), result);
						}
					}
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return results;
	}
	
	@Override
	public String toString() {
		return "";
	}
}
