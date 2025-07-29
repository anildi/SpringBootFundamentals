package ttl.larku.service;

import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Course;
import ttl.larku.reflect.inject.MyInject;
import ttl.larku.reflect.inject.OtherInterface;

import java.util.List;

public class CourseService {

	@MyInject
	private BaseDAO<Course> courseDAO;

	@MyInject
	private OtherInterface otherService;
	
	public CourseService() {
//		courseDAO = new InMemoryCourseDAO();
	}
	
	public Course createCourse(String code, String title) {
		Course course = new Course(code, title);
		course = courseDAO.create(course);
		
		return course;
	}
	
	public Course createCourse(Course course) {
		course = courseDAO.create(course);
		
		return course;
	}
	
	public boolean deleteCourse(int id) {
		Course course = courseDAO.get(id);
		if(course != null) {
			courseDAO.delete(course);
		}
		return false;
	}
	
	public boolean updateCourse(Course course) {
		Course oldCourse = courseDAO.get(course.getId());
		if(oldCourse != null) {
			courseDAO.update(course);
			return true;
		}
		return false;
	}
	
	public Course getCourseByCode(String code) {
		List<Course> courses = courseDAO.getAll();
		for(Course course : courses) {
			if(course.getCode().equals(code)) {
				return course;
			}
		}
		return null;
	}
	
	public Course getCourse(int id) {
		return courseDAO.get(id);
	}
	
	public List<Course> getAllCourses() {
		System.out.println(otherService.doStuff("CourseService stuff"));
		return courseDAO.getAll();
	}
	
	public BaseDAO<Course> getCourseDAO() {
		return courseDAO;
	}

	public void setCourseDAO(BaseDAO<Course> courseDAO) {
		this.courseDAO = courseDAO;
	}
}
