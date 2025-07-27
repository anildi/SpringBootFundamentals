package ttl.larku.beandiscovery;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ttl.larku.beandiscovery.BeanNode;
import ttl.larku.beandiscovery.DependancyDiscoverer;

import java.util.List;
import java.util.Map;

/**
 * @author whynot
 */
@RestController
@RequestMapping("/discovery")
public class BeanDiscoveryController {

    private DependancyDiscoverer discoverer;
    public BeanDiscoveryController(DependancyDiscoverer discoverer) {
        this.discoverer = discoverer;
    }

    @GetMapping("/dependencies")
    public List<BeanNode> getDependencies(@RequestParam Map<String, String> queryStrings) {
        var result = queryStrings.keySet().stream()
                .flatMap(key -> discoverer.getDependencies(key).stream())
                .toList();

        return result;
    }

    @GetMapping("/dependants")
    public List<BeanNode> getDependants(@RequestParam Map<String, String> queryStrings) {
        var result = queryStrings.keySet().stream()
                .flatMap(key -> discoverer.getDependants(key).stream())
                .toList();

        return result;
    }
}
