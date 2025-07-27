package ttl.larku.search;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import ttl.larku.controllers.rest.RestResultWrapper;
import ttl.larku.domain.Student;

@Service
public class JPASearchSpecService<E_T, ID_T,
      RepoType extends JpaRepository<E_T, ID_T> & JpaSpecificationExecutor<E_T>> {


   public RestResultWrapper<List<E_T>> doSearch(@RequestParam Map<String, Object> queryStrings,
                                        RepoType targetRepository) {
      List<E_T> result = null;
      Object tmp = queryStrings.get("pageSize");
      int pageSize = tmp != null ? Integer.parseInt(tmp.toString()) : 10;

      //If we have a "searchParams" query parameter,
      //break it out and put it into the searchContext as
      //individual parameters.
      var searchParams = queryStrings.get("searchParams");
      if(searchParams != null)  {
         String spStr = (String) searchParams;
         if(!spStr.isBlank()) {
            var arr = searchParams.toString().split("&");
            for (String param : arr) {
               var innerArr = param.split("=");
               var t2 = innerArr.length > 1 ? innerArr[1] : "";
               queryStrings.put(innerArr[0], t2);
            }
         }
      }
      queryStrings.put("pageSize", Integer.toString(pageSize));

      result = getTracksByRequestParams(queryStrings, targetRepository);

      tmp = queryStrings.get("page");
      int page = tmp != null ? Integer.parseInt(tmp.toString()) : 0;

      tmp = queryStrings.get("totalPages");
      int totalPages = tmp != null ? Integer.parseInt(tmp.toString()) : 1;

      tmp = queryStrings.get("totalElements");
      long totalElements = tmp != null ? Long.parseLong(tmp.toString()) : result.size();

      return RestResultWrapper.ofValue(result)
            .addProp("page", page)
            .addProp("pageSize", pageSize)
            .addProp("totalElements", totalElements)
            .addProp("totalPages",totalPages);
   }


   /**
    * This one is the first point of entry.  We will filter out anything
    * that is not a SearchSpecification, e.g. sorting parameters.
    *
    * @param searchContext  A map which serves for both input and output.  It
    *                       *must* be request scoped, or it *must* be thread safe.
    *                       The map contains criteria for paging and searching on the
    *                       way in.  This code adds information to the context such
    *                       as elements on page, total elements etc. that can be read
    *                       by the caller.
    * @return
    */
   public List<E_T> getTracksByRequestParams(Map<String, Object> searchContext,
                                             RepoType repository) {
      //look for page property
      Pageable pageable = null;
      if (searchContext.containsKey("page")) {
         //int page = Math.max(0, Integer.parseInt(searchContext.get("page").toString()) - 1);
         int page = Integer.parseInt(searchContext.get("page").toString()) - 1;
         if(page >= 0) {
            int pageSize = searchContext.containsKey("pageSize") ?
                  Integer.parseInt(searchContext.get("pageSize").toString()) : 10;

            pageable = PageRequest.of(page, pageSize);
         }
      }


      var haveSearchParams = searchContext.entrySet().stream()
            .filter(entry -> entry.getKey().matches(".\\..*"))
            .findFirst();

//      Map<String, Object> searchParams = partition.get(true)
//            .stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

      List<E_T> result = null;
      if (haveSearchParams.isPresent()) {
         if (repository instanceof RepoType specExecutor) {
            result = getTracksBySearchSpec(searchContext, pageable, specExecutor);
         }

      } else if (pageable != null) {
         Page<E_T> page = repository.findAll(pageable);
         //Add info to the context
         searchContext.put("totalElements", page.getTotalElements());
         searchContext.put("totalPages", page.getTotalPages());

         result = page.getContent();
      } else {
         result = repository.findAll();
      }
      return result;
   }

   /**
    * Handle JpaSearchSpec parameters.  Look at JpaTrackSearchSpec.  For usage,
    * look at the test in the 'dao' package.
    *
    * @param rawQueryStrings
    * @return
    */
   public List<E_T> getTracksBySearchSpec(Map<String, Object> rawQueryStrings,
                                          RepoType specExecutor) {
      return getTracksBySearchSpec(rawQueryStrings, null, specExecutor);
   }

   public List<E_T> getTracksBySearchSpec(Map<String, Object> searchContext,
                                          Pageable pageAble,
                                          RepoType specExecutor) {
      Map<String, Object> queryStrings = convertToMapOfCorrectTypes(searchContext);

      List<Specification<E_T>> searchSpecs = new ArrayList<>();
      queryStrings.forEach((k, value) -> {
         //Pattern we are looking for is op.property
         //op is first character
         if (k.matches(".\\..*")) {
            var opSymbol = k.substring(0, 1);
            var searchType = JpaSearchSpecSupport.SearchType.getBySymbol(opSymbol);
            //Property name to filter by starts at position 2
            var propName = k.substring(2);

            var ss = new JpaTrackSearchSpec(searchType,
                  propName, value);
            searchSpecs.add((Specification<E_T>) ss);
         }
      });

      List<E_T> result = getTracksBySearchSpec(searchContext, searchSpecs, pageAble, specExecutor);
      return result;
   }

   public List<E_T> getTracksBySearchSpec(Map<String, Object> searchContext,
                                          List<? extends Specification<E_T>> searchSpecs,
                                          Pageable pageAble,
                                          RepoType specExecutor) {
      if (searchSpecs.isEmpty()) return List.of();

      Specification<E_T> specification = searchSpecs.get(0);
      for (int i = 1; i < searchSpecs.size(); i++) {
         specification = specification.or(searchSpecs.get(i));
      }

      List<E_T> result = null;
      if (pageAble != null) {
         var page = specExecutor.findAll(specification, pageAble);
         searchContext.put("totalElements", page.getTotalElements());
         searchContext.put("totalPages", page.getTotalPages());
         result = page.getContent();
      } else {
         result = specExecutor.findAll(specification);
      }

      return result;
   }

   //Hard coded for now
   public Map<String, Object> convertToMapOfCorrectTypes(Map<String, Object> searchContext) {
//      Map<String, Object> result = new HashMap<>();
      searchContext.forEach((k, v) -> {
         switch (k) {
            case String s when
                  s.contains("dob") || s.contains("endDate")
                        || s.contains("startDate") -> searchContext.put(k, LocalDate.parse(v.toString()));
            case String s when
                  s.contains("status") -> searchContext.put(k, Student.Status.valueOf(v.toString().toUpperCase()));

            default -> {
            }
         }
      });

      return searchContext;
   }

}
