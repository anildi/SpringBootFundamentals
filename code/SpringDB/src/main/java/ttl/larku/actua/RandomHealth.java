package ttl.larku.actua;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class RandomHealth implements HealthIndicator {

    @Override
    public Health health() {
        double r = ThreadLocalRandom.current().nextDouble();
        if (r < .5) {
            return Health.down().withDetail("Randomly Down: ", r).build();
        }
        return Health.up().withDetail("Randomly Up: ", r).build();
    }
}
