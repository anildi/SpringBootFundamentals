package ttl.larku.rating;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * One way to make RestClient calls succeed is to make it depend on a RestTemplate that
 * has SSL configured, like we do in .../security/clientClientSSLConfig.
 * <p>
 * The other way is to add the public key of our self-signed certificate (larkUKeyfile.p12)
 * to our jdks trusted certificate list (jdk_path/lib/security/cacerts).  Instructions on
 * how to do that are in the README.SSL file.
 * <p>
 * _Note that this test will fail if the certificate has not been added into the cacerts
 * file, since here we are working with just a RestClient that has no special ssl config
 * applied to it.
 */
@Component
@Profile("networkrating")
public class RestClientRatingProvider implements RatingProvider{


   private String password;

   private RestClient restClient;

//   String baseUrl;
   //String rootUrl = baseUrl + "/adminrest/student";
//   String rootUrl;


   public RestClientRatingProvider(@Value("${rating.provider.url}")
                       String baseUrl,
                                   @Value("${CLIENT_PASSWORD}")
                       String password) throws UnrecoverableKeyException, CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

      this.password = password;
      String basicAuthHeader = "basic " + Base64.getEncoder().encodeToString(("bobby" + ":" + password).getBytes());

//
      this.restClient = RestClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader("Accept", "application/json")
            .defaultHeader("Authorization", basicAuthHeader)
            .build();

   }

   @Override
   public BigDecimal getRating(int id) {
      return getRatingFromService(id, null);
   }

   @Override
   public BigDecimal getRating(int id, String user, String pw) {
      var encoded = Base64.getEncoder().encodeToString((user + ":" + pw).getBytes());
      String basicAuthHeader = "Basic " + encoded;
      var result = getRatingFromService(id, basicAuthHeader);
      return result;
   }

   private BigDecimal getRatingFromService(int id, String authHeader) {
      var spec = restClient.get()
            .uri("/{id}", id);
//      if (authHeader != null) {
//         spec = spec.header("Authorization", authHeader);
//      }
      var response = spec.retrieve()
            .toEntity(BigDecimal.class);

      if (response.getStatusCode() == HttpStatus.OK) {
         var rating = response.getBody();
         return rating;
      }
      return null;
   }

   public BigDecimal testGetCourseRating() {
      ResponseEntity<BigDecimal> response = restClient.get()
            //.uri("https://localhost:10043/rating/{id}", 2)
            .uri("/{id}", 2)
            .retrieve()
            .toEntity(BigDecimal.class);


      BigDecimal rating = response.getBody();

      System.out.println("rating: " + rating);
      return rating;
   }
}