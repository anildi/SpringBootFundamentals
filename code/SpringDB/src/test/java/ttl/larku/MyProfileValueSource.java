package ttl.larku;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.ProfileValueSource;

import java.util.Properties;

/**
 * A Custom profile value source. Strangely enough, application.properties is
 * not on the default profile path, and so @IfProfileValue does not find
 * properties defined there. We add this class to pick up properties from
 * application.properties.
 * <p>
 * This is only available in the test scope.
 *
 * @author whynot
 */
@Component
public class MyProfileValueSource implements ProfileValueSource {

    private static Properties props;

    public MyProfileValueSource() {
    }

    static {
        init();
    }

    public static void init() {
        Resource resource = new ClassPathResource("application.properties");
        try {
            props = PropertiesLoaderUtils.loadProperties(resource);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String get(String property) {
        String result = props.getProperty(property);
        return result;
    }

}
