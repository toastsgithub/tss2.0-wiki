package tss2.wiki.control;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tss2.wiki.dao.DAOBase;
import tss2.wiki.dao.impl.Summary;
import tss2.wiki.db.DBAdmin;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by 羊驼 on 2016/7/8.
 */
@Controller
@RequestMapping(value = "/content")
public class ContentController {
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody String addEntry(@RequestBody Map map) {
        Map mapObject = (Map) map.get("operation");
        mapObject = (Map) mapObject.get("a");
        ArrayList ar = (ArrayList) mapObject.get("c");
        return ar.toString();
    }

    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    public @ResponseBody String getSummary() {
        DAOBase[] content = Summary.query().where("");
        if (content.length == 0) return null;
        return content[0].get("summaryJO").toString();
    }
}
