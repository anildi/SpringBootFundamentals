package ttl.larku.controllers.rest;

import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
public class UriCreator {
	
	public URI getURI(int id) {
		
        URI newResource = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
		
        return newResource;
	}

   public URI getURI() {

      URI newResource = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .build()
            .toUri();

      return newResource;
   }

   public ProblemDetail getProblemDetail(HttpStatus status, String detail) {
      var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
            detail);
      var instance = getURI();
      problemDetail.setInstance(instance);

      return problemDetail;
   }
}
