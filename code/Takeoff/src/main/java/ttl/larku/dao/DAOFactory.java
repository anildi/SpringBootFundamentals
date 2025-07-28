package ttl.larku.dao;

import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import ttl.larku.dao.inmemory.InMemoryStudentDAO;
import ttl.larku.dao.inmemory.StudentDAO;
import ttl.larku.dao.jpa.JPAStudentDAO;
import ttl.larku.domain.Student;
import ttl.larku.service.StudentService;

public class DAOFactory {

   private static Map<String, Object> objects = new ConcurrentHashMap<String, Object>();

   private static String profile;

   static {
      ResourceBundle bundle = ResourceBundle.getBundle("larkUContext");
      profile = bundle.getString("larku.profile.active");
   }


   public static StudentDAO studentDAO() {

      StudentDAO result = switch(profile) {
         case "dev" -> {
//            StudentDAO obj = (StudentDAO) objects.get("studentDAO");
//            if(obj == null) {
//               obj = new InMemoryStudentDAO();
//               objects.put("studentDAO", obj);
//            }
            StudentDAO obj = (StudentDAO) objects.computeIfAbsent("studentDAO", (k) -> new InMemoryStudentDAO());
            yield obj;

         }
         case "prod" -> {
            StudentDAO obj = (StudentDAO) objects.computeIfAbsent("studentDAO", (k) -> new JPAStudentDAO());
            yield obj;

         }
         default -> throw new RuntimeException("Unknow profile: " + profile);
      };
      return result;
   }

   public static StudentService studentService() {
      StudentService ss = new StudentService();

      var dao = studentDAO();
      ss.setStudentDAO(dao);

      return ss;
   }

   public static StudentDAO oldstudentDAO() {

      switch(profile) {
         case "dev":
            return new InMemoryStudentDAO();
         case "prod":
            return new JPAStudentDAO();
         default:
            throw new RuntimeException("Unknow profile: " + profile);
      }
   }
}
