package ttl.larku.actuator;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import org.springframework.http.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;

public class ServiceHealthChecker {
    RestTemplate rt = new RestTemplate();

    String healthUrl = "http://localhost:8080/actuator/health";

    public static void main(String[] args) {
        ServiceHealthChecker shc = new ServiceHealthChecker();
        shc.go();
    }

    public void go() {
        while(true) {
            boolean updown = health();
            System.out.println("Registration Service is up: " + updown + ", at " + LocalDateTime.now());

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean health() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = rt.exchange(healthUrl,
                    HttpMethod.GET, entity, String.class);

            String jsonResponse = response.getBody();

            ReadContext ctx = JsonPath.parse(jsonResponse);

            String status = ctx.read("$.status");
            if (status.equals("UP")) {
                return true;
            }
        }catch(HttpServerErrorException ex) {
            System.out.println("Error contacting RegistrationService, status Code: " + ex.getStatusCode() +
                    ", detailedError: " + ex.getResponseBodyAsString());
//            ex.printStackTrace();
        }
        return false;
    }
}
