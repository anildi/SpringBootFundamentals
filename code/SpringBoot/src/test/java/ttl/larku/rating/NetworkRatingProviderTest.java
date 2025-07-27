package ttl.larku.rating;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@EnabledIf(expression = "#{environment.matchesProfiles('networkrating')}", loadContext = true)
public class NetworkRatingProviderTest {

   @Autowired
   private RestClientRatingProvider provider;

   @Value("${CLIENT_PASSWORD}")
   private String pw;

   private static String adminUser = "bobby";
   private static String justUser = "manoj";

   //The CourseRatingService *has* to be running
   //for this test to succeed.
   @Test
   public void testProviderRatingReturns401WithNoAuthorization() {
      assertThrows(HttpClientErrorException.Unauthorized.class, () -> {
         var result = provider.getRating(10);

         System.out.println("result: " + result);

         assertTrue(Double.parseDouble(result) >= 0.0);
      });
   }

   @ParameterizedTest
   @MethodSource("userNameSource")
   public void testGetProviderRatingReturnsSuccessWithAuthorizedUser(String userName) {

      var result = provider.getRating(10, userName, pw);

      System.out.println("result: " + result);

      assertTrue(Double.parseDouble(result) >= 0.0);
   }

   static Stream<String> userNameSource() {
      return Stream.of(justUser, adminUser);
   }
}
