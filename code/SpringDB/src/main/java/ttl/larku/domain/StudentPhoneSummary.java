package ttl.larku.domain;

/**
 * A projection to get only student id, name and phoneNumber
 *
 * @author whynot
 */
//@Projection(types = {Student.class})
public interface StudentPhoneSummary {

    int getId();

    String getName();

    String getPhoneNumber();
}
