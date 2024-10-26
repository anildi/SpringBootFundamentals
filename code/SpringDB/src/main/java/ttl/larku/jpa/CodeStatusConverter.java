package ttl.larku.jpa;

//package ttl.larku.jpa;
//
//import ttl.larku.domain.Student;
//
//import javax.persistence.AttributeConverter;
//import javax.persistence.Converter;
//
//
///**
// * @author whynot
// *
// * A converter for Student.Status with Int values.
// * An example of Student.Status which will work with this is
// * at the bottom of this file.
// * IMPORTANT: For this to come into play, you HAVE to remove
// * the @Enumerated annotaion on the Enum.
// *
// * autoapply = true will apply this everywhere you use a Student.Status.
// * You can also selectively apply it to individual fields instead.
// */
//@Converter(autoApply = true)
//public class CodeStatusConverter implements AttributeConverter<Student.Status, String> {
//
//    @Override
//    public String convertToDatabaseColumn(Student.Status status) {
//        if (status == null) {
//            return null;
//        }
//        return String.valueOf(status.getCode());
//    }
//
//    @Override
//    public Student.Status convertToEntityAttribute(String code) {
//        if (code == null) {
//            return null;
//        }
//
//        var result = Student.Status.of(code);
//        return result;
//    }
//}
//public enum Status {
//    FULL_TIME(17),
//    PART_TIME(210),
//    HIBERNATING(0);
//
//    private int code;
//    Status(int code) {
//        this.code = code;
//    }
//
//    public int getCode() {
//        return code;
//    }
//
//    public static Student.Status of(String codeStr) {
//        return of(Integer.parseInt(codeStr));
//    }
//
//    public static Student.Status of(int code) {
//        var result = Arrays.stream(Student.Status.values())
//                .filter(es -> es.code == code)
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException(("No Status with code: " + code)));
//
//        return result;
//    }
//}
