package ttl.larku.service.props;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ttl.larku.domain.ConnectionServiceProperties;

import java.net.InetSocketAddress;
import java.net.Socket;

@Service
public class ConnectionService {

    @Autowired
    private ConnectionServiceProperties props;

    public int makeConnection() {
        int retries = props.getRetriesOnTimeout();
        String host = props.getHost();
        int port = props.getPort();
        System.out.println("Making Connection to: " + host + ":" + port);
        for(int numTries = 0; numTries < props.getRetriesOnTimeout(); numTries++) {
            try {
                Socket s = new Socket();
                s.setSoTimeout((int)(props.getTimeOut().toMillis()));
                s.connect(new InetSocketAddress(props.getHost(), props.getPort()),(int)props.getTimeOut().toMillis());
                //interact with server
                
                return 10;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error with connection, numTries: " + numTries);
            }
        }
        return -1;
    }

    public String getHost() {
        return props.getHost();
    }
}
