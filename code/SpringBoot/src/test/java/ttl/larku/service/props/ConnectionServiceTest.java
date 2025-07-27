package ttl.larku.service.props;

//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.ApplicationContext;
//import ttl.larku.service.reg.integcustomcontext.integration.MyTestConfig;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//public class ConnectionServiceTest {
//
//    @Autowired
//    private ConnectionService connectionService;
//
//    @Autowired
//    private ApplicationContext context;
//
//    @BeforeEach
//    public void init() {
//        for(String name: context.getBeanDefinitionNames()) {
//            System.out.println(name);
//        }
//        System.out.println(context.getBeanDefinitionCount() + " beans");
//    }
//
//    @Test
//    public void testConnectionService() {
//        int result = connectionService.makeConnection();
//        String host = connectionService.getHost();
//
//        assertEquals("xyz.com", host);
//        assertEquals(-1, result);
//    }
//}
