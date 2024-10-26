package ttl.larku.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.Duration;

 /**
 * A JPA Converter to convert from a String in the Database
 * to a Duration object in Java.
 * @author whynot
 */
@Converter(autoApply = true)
public class JPADurationConverter implements AttributeConverter<Duration, String> {

    @Override
    public String convertToDatabaseColumn(Duration duration) {
        if(duration != null) {
            String durString = duration.toString();
//            System.out.println("DurationConverter.DToS: " + duration + ", to " + durString);
            return durString;
        }
        return null;
    }

    @Override
    public Duration convertToEntityAttribute(String durString) {
        if(durString != null) {
            Duration dur = Duration.parse(durString);
//            System.out.println("DurationConverter.SToD: " + durString + ", to " + durString);
            return dur;
        }
        return null;
    }
}