package ttl.larku.service.props;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Just to demonstrate the use of @Value annotation
 */
@Service
public class AtValueDemoService {

    //Read a property from the Environment
    //using "${...}"
    @Value("${spring.profiles.active}")
    private String profiles;

    //Run code and do comparisions using "#{...}
    @Value("#{'${spring.profiles.active}' == 'development'}")
    private boolean isDevelopment;

    //Call a method on another bean.
    @Value("#{connectionService.getHost()}")
    private String host;

    public String getProfiles() {
        return profiles;
    }

    public void setProfiles(String profiles) {
        this.profiles = profiles;
    }

    public boolean isDevelopment() {
        return isDevelopment;
    }

    public void setDevelopment(boolean development) {
        isDevelopment = development;
    }

    public String getHost() {
        return host;
    }
}
