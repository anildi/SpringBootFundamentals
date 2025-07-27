package ttl.larku.service.props;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
@ConfigurationProperties(value = "ttl.connection-service")
public class ConnectionServiceProperties {

    @NotNull
    private String host;

    @Min(1025)
    @Max(65535)
    private int port;

    private Duration timeOut = Duration.ofSeconds(2);

    private int retriesOnTimeout = 0;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Duration getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Duration timeOut) {
        this.timeOut = timeOut;
    }

    public int getRetriesOnTimeout() {
        return retriesOnTimeout;
    }

    public void setRetriesOnTimeout(int retriesOnTimeout) {
        this.retriesOnTimeout = retriesOnTimeout;
    }

    @Override
    public String toString() {
        return "ConnectionManagerProperties{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", timeOut=" + timeOut +
                ", retriesOnTimeout=" + retriesOnTimeout +
                '}';
    }
}
