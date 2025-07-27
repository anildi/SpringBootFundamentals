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
@RequestMapping("/admin/getStudents")
public class SearchController {

   @Autowired
   private JPASearchSpecService<Student, Integer, StudentRepo> searchService;

   @Autowired
   private StudentRepo targetRepository;

   //CHANGE Required.  Set this to the name of the
   //object your templating engine is going to look for.
   private String resultPropertName = "students";

   //CHANGE Required.  Set this to the name of the
   //next view you want to show.
   private String nextViewName = "showStudents";

   @GetMapping("/search")
   public ModelAndView getAllTracks(@RequestParam Map<String, Object> queryStrings,
                                    HttpSession session) {
      //If we have a "searchParams" query parameter,
      //break it out and put it into the searchContext as
      //individual parameters.
      var searchParams = queryStrings.get("searchParams");
//      if(searchParams != null)  {
//         String spStr = (String) searchParams;
//         if(!spStr.isBlank()) {
//            var arr = searchParams.toString().split("&");
//            for (String param : arr) {
//               var innerArr = param.split("=");
//               queryStrings.put(innerArr[0], innerArr[1]);
//            }
//         }
//      }

      RestResultWrapper<List<Student>> result = searchService.doSearch(queryStrings, targetRepository);

      Object tmp = queryStrings.get("page");
      int page = tmp != null ? Integer.parseInt(tmp.toString()) : 0;

      tmp = queryStrings.get("pageSize");
      int pageSize = tmp != null ? Integer.parseInt(tmp.toString()) : 0;

      tmp = queryStrings.get("totalPages");
      int totalPages = tmp != null ? Integer.parseInt(tmp.toString()) : 1;

      tmp = queryStrings.get("totalElements");
      long totalElements = tmp != null ? Long.parseLong(tmp.toString()) : result.getEntity().size();

      result.setEntity(postProcess(result.getEntity()));

      var props = Map.of(
            "page", page,
            "pageSize", pageSize,
            "totalPages", totalPages,
            "totalElements", totalElements,
            "searchParams", searchParams != null ? searchParams : "",
            "searchResult", result.getEntity(),    //This one is for the searchHeader

            resultPropertName, result.getEntity());

      ModelAndView mav = new ModelAndView(nextViewName, props);
      return mav;
   }

   public List<Student> postProcess(List<Student> result) {
      return result;
   }
}
