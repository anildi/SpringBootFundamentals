package ttl.larku.jpa;

import ttl.larku.domain.Student;

import jakarta.persistence.AttributeConverter;


/**
 * @author whynot
 *
 * A "default" converter for Student.Status.  This will do exactly what
 * the @Enumerated(EnumType.String) annotation will do.
 *
 * IMPORTANT: For this to come into play, you HAVE to remove
 * the @Enumerated annotaion on the Enum.
 *
 * autoapply = true will apply this everywhere you use a Student.Status.
 * You can also selectively apply it to individual fields instead.
 */
//@Converter(autoApply = true)
public class NullStatusConverter implements AttributeConverter<Student.Status, String> {

    @Override
    public String convertToDatabaseColumn(Student.Status status) {
        if (status == null) {
            return null;
        }
        return status.toString();
    }

    @Override
    public Student.Status convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }

        var result = Student.Status.valueOf(code);
        return result;
    }
}