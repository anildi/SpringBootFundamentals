package ttl.larku.service.props;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class AtValueDemoServiceTest {

    @Autowired
    private AtValueDemoService avds;

    @Test
    public void testAtValuesSet() {
        String profile = avds.getProfiles();
        boolean isDev = avds.isDevelopment();

        String host = avds.getHost();

        System.out.println("profile: " + profile + ", isDev: " + isDev + ", host: " + host);

        assertEquals("xyz.com", host);
    }
}
