package ttl.larku.postproc;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Student;

public class PostProcApp {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        TestService testService = context.getBean("postProcBean", TestService.class);

        BaseDAO<Student> dao = testService.getDao();

        System.out.println("dao is " + dao);

    }
}
