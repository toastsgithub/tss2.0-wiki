package tss2.wiki.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Default controller.
 * Mapping '/' & '/index' to Outline.html.
 *
 * Created by 羊驼 on 2016/7/8.
 */
@Controller
public class DefaultController {

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index() {
        return "/html/Outline.html";
    }
}
