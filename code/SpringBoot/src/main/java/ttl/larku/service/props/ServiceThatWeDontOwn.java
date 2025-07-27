package ttl.larku.service.props;

import java.time.Duration;

public class ServiceThatWeDontOwn {

    private Duration timeout;
    private boolean countDown;

    public Duration getTimeout() {
        return timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    public boolean isCountDown() {
        return countDown;
    }

    public void setCountDown(boolean countDown) {
        this.countDown = countDown;
    }

    public void doServiceTypeThing() {

    }

    @Override
    public String toString() {
        return "ServiceThatWeDontOwn{" +
                "timeout=" + timeout +
                ", countDown=" + countDown +
                '}';
    }
}
