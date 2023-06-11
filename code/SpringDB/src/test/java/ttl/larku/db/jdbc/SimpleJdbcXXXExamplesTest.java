package ttl.larku.db.jdbc;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ttl.larku.domain.Student;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Sql(scripts = { "/schema-h2.sql", "/data-h2.sql" }, executionPhase= Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//@Transactional
@Tag("optional")
public class SimpleJdbcXXXExamplesTest {
	
	@TestConfiguration
	public static class TestConfig {
		@Bean
		public JdbcTemplate jdbcTemplate(DataSource userDataSource) {
			JdbcTemplate template = new JdbcTemplate(userDataSource);
			return template;
		}

		@Bean
		public SimpleJdbcInsert simpleJdbcInsert(DataSource userDataSource) {
			SimpleJdbcInsert template = new SimpleJdbcInsert(userDataSource)
				.withTableName("Student").usingGeneratedKeyColumns("ID");;
			return template;
		}

		@Bean
		public NamedParameterJdbcTemplate namedParameterTemplate(DataSource userDataSource) {
			NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(userDataSource);
			return template;
		}
		
	}
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private SimpleJdbcInsert simpleJdbcInsert;
	
	@Test
	public void testSimpleJdbcInsert() {
		Map<String, Object> parameters = new HashMap<String, Object>();
	    parameters.put("name", "FurryThing");
	    parameters.put("phoneNumber", "890 2374 23402304");
	    parameters.put("status", "FULL_TIME");

	    int numRows = simpleJdbcInsert.execute(parameters);

	    Number newKey = simpleJdbcInsert.executeAndReturnKey(parameters);
	    
	    assertEquals(1, numRows);
	    assertTrue(newKey.intValue() > 4);
		
	}

	@Test
	public void testSimpleJdbcInsertfromJavaBean() {
		Student student = new Student("Marlye", "383 060 87383", LocalDate.of(1956, 2, 5), Student.Status.HIBERNATING);

		BeanPropertySqlParameterSource javaBeanSource = new BeanPropertySqlParameterSource(student);

	    int numRows = simpleJdbcInsert.execute(javaBeanSource);

	    Number newKey = simpleJdbcInsert.executeAndReturnKey(javaBeanSource);
	    
	    
	    int count = jdbcTemplate.queryForObject("select count(*) from student", Integer.class);
		
	    assertEquals(1, numRows);
	    assertTrue(newKey.intValue() > 4);
	    assertEquals(6, count);
	}

}
