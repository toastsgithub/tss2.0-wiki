package tss2.wiki.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tss2.wiki.dao.DAOBase;
import tss2.wiki.dao.Summary;
import tss2.wiki.domain.OutLineResult;
import tss2.wiki.model.WikiOutline;

import java.util.ArrayList;
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
    @RequestMapping(value = "", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
    public @ResponseBody Map getOutline() {
        WikiOutline wikiOutline = new WikiOutline();
        System.out.println(wikiOutline.getSummary());
        return wikiOutline.getSummary();
    }

    @RequestMapping(value = "", method = RequestMethod.PUT, produces="application/json;charset=UTF-8")
    public @ResponseBody void setSummary(@RequestBody Map map) {
        System.out.println(map);
        //DAOBase[] content = Summary.query().where("");
        //content[0].setValue("summaryJO",map.toString());
        //content[0].save();
        WikiOutline wikiOutline = new WikiOutline();
        wikiOutline.setMap(map);
    }


    @RequestMapping(value = "/list", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
    public @ResponseBody
    OutLineResult getOutlineList() {
        OutLineResult result = new OutLineResult();
        WikiOutline wikiOutline = new WikiOutline();
        System.out.println(wikiOutline.getSummary());
        visit(wikiOutline.getSummary(), result);
        return result;
    }

    private void visit(Map src, OutLineResult tar) {
        if (src == null) return;
        tar.addOutLine(src.get("text").toString());
        for (Map map: (ArrayList<Map>) src.get("children")) {
            visit(map, tar);
        }
    }
}
