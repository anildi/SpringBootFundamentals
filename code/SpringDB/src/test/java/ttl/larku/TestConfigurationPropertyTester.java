package ttl.larku;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ttl.larku.domain.DummyTimerConfigFromProps;
import ttl.larku.domain.LarkUPropertiesHolder;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag("integration")
public class TestConfigurationPropertyTester {

    @Autowired
    private LarkUPropertiesHolder larkUPropertiesHolder;

    @Autowired
    private DummyTimerConfigFromProps dummyTimerConfigFromProps;

    @Test
    public void testConfigProps() {
        String name = larkUPropertiesHolder.getName();
        int value = larkUPropertiesHolder.getValue();
        double price = larkUPropertiesHolder.getPrice();

        System.out.println("name: " + name + ", value: " + value + ", price: " + price);

        assertEquals("Big thing", name);
        assertEquals(10, value);
        assertEquals(33.5, price, 0.1);
    }

    @Test
    public void testPropConfigedBean() {
        Duration timeout = dummyTimerConfigFromProps.getTimeout();
        boolean countDown = dummyTimerConfigFromProps.isCountDown();

        System.out.println("timeout: " + timeout + ", countDown: " + countDown);

        assertEquals(Duration.ofSeconds(10), timeout);
        assertEquals(false, countDown);
    }
}
