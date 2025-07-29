package ttl.larku.reflect.basic;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import ttl.larku.domain.Student;

/**
 * @author developintelligence llc
 * @version 1.0
 */
public class StudentServiceInvocationExample {

	public static void main(String[] args) {
		String className = "ttl.larku.service.StudentService";
		try {
			// get the class
			Class<?> clazz = getClasss(className);

			Object clazzInstance = clazz.getDeclaredConstructor().newInstance();

			// Find the doStuff Method
			Student student = new Student("Perl", LocalDate.of(2000, 10, 10), Student.Status.HIBERNATING);
			Method method = clazz.getMethod("createStudent", Student.class);

			Object result = method.invoke(clazzInstance, student);

			// print the results
			System.out.println(className + ", result: " + result);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Class<?> getClasss(String className)
			throws ClassNotFoundException {
		Class<?> returnValue = null;
		returnValue = Class.forName(className);
		return returnValue;
	}
}