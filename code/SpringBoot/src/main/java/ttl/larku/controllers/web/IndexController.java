package ttl.larku.controllers.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {

    @GetMapping("/")
    public String slash() {
        return "redirect:/admin";
    }

    @GetMapping({"/admin"})
    public ModelAndView index() {
        return new ModelAndView("startPage");
    }
}
