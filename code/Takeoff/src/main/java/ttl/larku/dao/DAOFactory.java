package ttl.larku.dao;

import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import ttl.larku.dao.inmemory.InMemoryStudentDAO;
import ttl.larku.dao.jpa.JpaStudentDAO;

public class DAOFactory {

   private static Map<String, Object> objects = new ConcurrentHashMap<>();
   private static String profile;
   static {
      ResourceBundle resourceBundle = ResourceBundle.getBundle("larkUContext");
      profile = resourceBundle.getString("larku.profile.active");
   }

   public static StudentDAO studentDAO() {

      StudentDAO dao = switch (profile) {
//         case "dev" -> {
//            StudentDAO sd = (StudentDAO) objects.get("studentDAO");
//            if(sd == null) {
//               sd = new InMemoryStudentDAO();
//               objects.put("studentDAO", sd);
//            }
//            yield sd;
//         }
         case "dev" -> (StudentDAO)objects.computeIfAbsent("studentDAO", k ->
               new InMemoryStudentDAO());

         case "prod" -> (StudentDAO)objects.computeIfAbsent("studentDAO", k ->
               new JpaStudentDAO());

         default -> throw new RuntimeException("Unknown profile: " + profile);
      };

      return dao;
   }
}
