package ttl.larku.db.jdbc;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ttl.larku.domain.Course;
import ttl.larku.domain.ScheduledClass;
import ttl.larku.domain.Student;
import ttl.larku.domain.Student.Status;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Sql(scripts = { "/schema-h2.sql", "/data-h2.sql" }, executionPhase= Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//@Transactional
@Tag("optional")
public class JdbcTemplateExamplesTest {

	@TestConfiguration
	public static class JdbcTestConfig {
		@Bean
		public JdbcTemplate jdbcTemplate(DataSource userDataSource) {
			JdbcTemplate template = new JdbcTemplate(userDataSource);
			return template;
		}

		@Bean
		public NamedParameterJdbcTemplate namedParameterTemplate(DataSource userDataSource) {
			NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(userDataSource);
			return template;
		}
	}

	@Autowired
	private JdbcTemplate template;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterTemplate;

	@Test
	public void testSimpleSimpleGets() {
		String sql = "select s.name from Student s where s.id = 1";
		String oneName = template.queryForObject(sql, String.class);

		System.out.println("OneName: " + oneName);

		assertTrue(oneName.contains("Manoj"));
	}

	@Test
	public void testSimpleInsert() {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		String sql = "insert into Student (name, phoneNumber, status) values('Sparkus', '383 3075756619103', 'FULL_TIME')";
		String prepSql = "insert into Student (name, phoneNumber, status) values(?, ?, ?)";

		int numRows = template.update(connection ->
		{
			PreparedStatement ps = connection.prepareStatement(prepSql, new String[] { "id" });
			ps.setString(1, "Sparkus");
			ps.setString(2, "383 030 08488448");
			ps.setString(3, "FULL_TIME");
			return ps;
		}, keyHolder);

		System.out.println("numRows: " + numRows + ", newKey: " + keyHolder.getKey());

		assertEquals(1, numRows);
		assertEquals(5, keyHolder.getKey());
	}

	@Test
	public void testRowMapper() {
		String sql = "select * from Student where id = ?";

		RowMapper<Student> rowMapper = (ResultSet rs, int rowNum) ->
		{
			int id = rs.getInt("id");
			String name = rs.getString("name");
			String phoneNumber = rs.getString("phoneNumber");
			Student.Status status = Status.valueOf(rs.getString("status"));

			Student s = new Student(name, phoneNumber, LocalDate.of(1956, 8, 9), status);
			s.setId(id);
			return s;
		};

		Student s = template.queryForObject(sql, rowMapper, "1");
		System.out.println("Student: " + s);

		assertEquals(1, s.getId());
	}

	@Test
	public void testRowMapperNamedParam() {
		String sql = "select * from Student where id = :id";

		RowMapper<Student> rowMapper = (ResultSet rs, int rowNum) ->
		{
			int id = rs.getInt("id");
			String name = rs.getString("name");
			String phoneNumber = rs.getString("phoneNumber");
			Student.Status status = Status.valueOf(rs.getString("status"));

			Student s = new Student(name, phoneNumber, LocalDate.of(1956, 8, 9), status);
			s.setId(id);
			return s;
		};
		Map<String, Object> paramValues = new HashMap<>();
		paramValues.put("id", 1);

		Student s = namedParameterTemplate.queryForObject(sql, paramValues, rowMapper);
		System.out.println("Student: " + s);

		assertEquals(1, s.getId());
	}

	@Test
	public void testRowMapperComplicated() {

		String sql = "select sc.id, sc.startdate, sc.enddate, s.id as student_id, s.name, s.phonenumber, "
				+ "s.status, c.id as course_id, c.code, c.credits, c.title  "
				+ "from scheduledclass sc left join student_scheduledclass ssc on sc.id = ssc.classes_id "
				+ "left join student s on s.id = ssc.students_id " + "join course c on sc.course_id = c.id "
				+ "where sc.startdate = :startDate and c.code = :code and c.id = sc.course_id";

		ResultSetExtractor<List<ScheduledClass>> rse = (rs) ->
		{
			List<ScheduledClass> result = new ArrayList<>();
			ScheduledClass currClass = null;
			int lastScid = 0;
			while (rs.next()) {
				int id = rs.getInt("id");
				if (id != lastScid) {
					lastScid = id;
					// read the SC and course information
					LocalDate startDate = rs.getDate("startdate").toLocalDate();
					LocalDate endDate = rs.getDate("endDate").toLocalDate();
					int courseId = rs.getInt("course_id");
					String courseCode = rs.getString("code");
					float credits = rs.getFloat("credits");
					String title = rs.getString("title");
					Course course = new Course(courseCode, title);
					course.setId(courseId);
					currClass = new ScheduledClass(course, startDate, endDate);
					currClass.setId(id);
					result.add(currClass);
				}

				Integer studentId = (Integer) rs.getObject("student_id");
				if (studentId != null) {
					String studentName = rs.getString("name");
					String phoneNumber = rs.getString("phoneNumber");
					String status = rs.getString("status");
					Student s = new Student(studentName, phoneNumber, LocalDate.of(1956, 10, 10), Status.valueOf(status));
					s.setId(studentId);
					currClass.addStudent(s);
				}

			}
			return result;
		};

		Map<String, Object> paramValues = new HashMap<>();
		paramValues.put("startDate", "2012-10-10");
		paramValues.put("code", "BOT-202");

		List<ScheduledClass> classes = namedParameterTemplate.query(sql, paramValues, rse);

		for (ScheduledClass sc : classes) {
			System.out.println("sc: " + sc);
			for (Student s : sc.getStudents()) {
				System.out.println("student: " + s);
			}
		}

	}

	@Test
	public void testRowMapperWithMany() {
		String sql = "select * from Student";

		RowMapper<Student> rowMapper = (ResultSet rs, int rowNum) ->
		{
			int id = rs.getInt("id");
			String name = rs.getString("name");
			String phoneNumber = rs.getString("phoneNumber");
			Student.Status status = Status.valueOf(rs.getString("status"));

			Student s = new Student(name, phoneNumber, LocalDate.of(1956, 8, 9), status);
			s.setId(id);
			return s;
		};

		List<Student> students = template.query(sql, rowMapper);
		System.out.println("Students: " + students);

		assertEquals(4, students.size());
	}

}
