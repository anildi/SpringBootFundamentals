package ttl.larku.domain;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties("ttl.connection-service")
//@PropertySource("classpath:larkUContext.properties")
public class ConnectionServiceProperties {

    private String host;
    private int port;
    private Duration timeOut;
    private int retriesOnTimeout;

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
