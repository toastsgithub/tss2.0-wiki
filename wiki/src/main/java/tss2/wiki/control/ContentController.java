package tss2.wiki.control;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tss2.wiki.dao.DAOBase;
import tss2.wiki.dao.impl.Summary;
import tss2.wiki.domain.TagResult;
import tss2.wiki.service.ContentService;
import tss2.wiki.service.impl.ContentServiceImpl;

import java.util.Map;

/**
 * Created by 羊驼 on 2016/7/8.
 */
@Controller
@RequestMapping(value = "/content")
public class ContentController {

    /**
     * request body:
     * {
     *   operation: 'add',
     *   data: {
     *     time: '2015-01-01 11:11:11',
     *     username: '123',
     *     entry_title: 'c++',
     *     tags: '软件工程/软件开发/需求工程',
     *     content: 'markdown正文',
     *   }
     * }
     * @param map
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces="text/plain;charset=UTF-8")
    public @ResponseBody String addEntry(@RequestBody Map map) {
        String operation = map.get("operation").toString();
        return operation;
    }

    @RequestMapping(value = "/summary", method = RequestMethod.GET, produces="text/plain;charset=UTF-8")
    public @ResponseBody String getSummary() {
        DAOBase[] content = Summary.query().where("");
        if (content.length == 0) return null;
        return content[0].get("summaryJO").toString();
    }

    @RequestMapping(value = "/tags", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
    public @ResponseBody TagResult getTags() {
        ContentService cs = new ContentServiceImpl();
        TagResult result = new TagResult();
        result.data = cs.getTags();
        return result;
    }

    private void process(Map map) {
        String opr = map.get("operation").toString();
    }
}
