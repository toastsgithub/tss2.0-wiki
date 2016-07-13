package tss2.wiki.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tss2.wiki.model.WikiSunmary;

import java.util.Map;

/**
 * Created by Administrator on 2016/7/12.
 */
@Controller
@RequestMapping(value = "/outline")
public class OutLineController {
    /**
     *
     * @return 表示大纲的map
     */
    @RequestMapping(value = "/show", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
    public @ResponseBody Map getOutline() {
        WikiSunmary wikiSunmary = new WikiSunmary();
        return wikiSunmary.create();
    }

    /**
     * request body:{
     *     father:"",
     *     addString:""
     * }
     * @param map
     * @return 表示大纲的map
     */
    @RequestMapping(value = "/add", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
    public @ResponseBody Map add(@RequestBody Map map){
        String father = map.get("father").toString();
        String addString = map.get("addString").toString();
        WikiSunmary wikiSunmary = new WikiSunmary();
        wikiSunmary.add(father,addString);
        return wikiSunmary.temp;
    }

    /**
     * request body:{
     *     father:"",
     *     delete:""
     * }
     * @param map
     * @return 表示大纲的map
     */
    @RequestMapping(value = "/delete", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
    public @ResponseBody Map delete(@RequestBody Map map){
        String father = map.get("father").toString();
        String delete = map.get("delete").toString();
        WikiSunmary wikiSunmary = new WikiSunmary();
        wikiSunmary.add(father,delete);
        System.out.println(wikiSunmary.temp);
        return wikiSunmary.temp;
    }

    /**
     * request body:{
     *     father:"",
     *     before:"",
     *     after:""
     * }
     * @param map
     * @return 表示大纲的map
     */
    @RequestMapping(value = "/modify", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
    public @ResponseBody Map modify(@RequestBody Map map){
        String father = map.get("father").toString();
        String before = map.get("before").toString();
        String after = map.get("after").toString();
        WikiSunmary wikiSunmary = new WikiSunmary();
        wikiSunmary.modify(father,before,after);
        System.out.println(wikiSunmary.temp);
        return wikiSunmary.temp;
    }


}
