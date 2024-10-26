package ttl.larku.controllers.rest.subresource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mainstuff")
public class MainResource {

    @Autowired
    private SubResource subResource;

    @GetMapping("/{subid}/*")
    public SubResource getSubResource(@PathVariable("subid") int id) {
        return subResource;
    }
}
