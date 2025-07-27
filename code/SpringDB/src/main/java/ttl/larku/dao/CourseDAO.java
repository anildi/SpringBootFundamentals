package ttl.larku.dao;

import ttl.larku.domain.Course;

public interface CourseDAO extends BaseDAO<Course>{

   public Course findByCode(String code);
}
