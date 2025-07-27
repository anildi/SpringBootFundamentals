package ttl.larku.actuator;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class RandomInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, Object> info = new HashMap<>();

        info.put("number", ThreadLocalRandom.current().nextDouble(22.6));
        info.put("letter", (char)('a' + ThreadLocalRandom.current().nextInt(26)));

        Map<String, Map<String, Object>> r = new HashMap<>();
        r.put("random", info);

        builder.withDetail("randominfo", r);
        builder.build();
    }
}
