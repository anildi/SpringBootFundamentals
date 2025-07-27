package ttl.larku.search;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import ttl.larku.controllers.rest.RestResultWrapper;
import ttl.larku.dao.repository.StudentRepo;
import ttl.larku.domain.Student;

@RestController
@RequestMapping("/adminrest/student")
public class SearchRestController {

   @Autowired
   private JPASearchSpecService<Student, Integer, StudentRepo> searchService;

   @Autowired
   private StudentRepo targetRepository;

   @GetMapping("/search")
   public RestResultWrapper<?> getAllTracks(@RequestParam Map<String, Object> queryStrings) {

      RestResultWrapper<List<Student>> result = searchService.doSearch(queryStrings, targetRepository);

      return result;
   }

   public List<Student> postProcess(List<Student> result) {
      return result;
   }
}
