package tss2.wiki.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tss2.wiki.dao.DAOBase;
import tss2.wiki.dao.Summary;
import tss2.wiki.dao.WikiEntry;
import tss2.wiki.domain.TagResult;
import tss2.wiki.control.service.ContentService;
import tss2.wiki.control.impl.ContentServiceImpl;
import tss2.wiki.model.WikiSession;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
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
     *     username: '123',
     *     categories: '123123',
     *     summary: '12312',
     *     entry_title: 'c++',
     *     tags: '软件工程/软件开发/需求工程',
     *     content: 'markdown正文',
     *   }
     * }
     * @param map
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces="text/plain;charset=UTF-8")
    public @ResponseBody String addEntry(HttpServletRequest request, @RequestBody Map map) {
        String operation = map.get("operation").toString();

        ContentService cs = new ContentServiceImpl();
        cs.process("", map);
        return "";
    }


    /**
     * 获取大纲信息。
     *
     * @return 大纲树的json表示。
     */
    @RequestMapping(value = "/outline", method = RequestMethod.GET, produces="text/plain;charset=UTF-8")
    public @ResponseBody String getSummary() {
        DAOBase[] content = Summary.query().where("");
        if (content.length == 0) return null;
        return content[0].get("summaryJO").toString();
    }

    /**
     * 获取所有标签信息。
     * @return {
     *     data: ['tag1', 'tag2', 'tag3']
     * }
     */
    @RequestMapping(value = "/tags", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
    public @ResponseBody TagResult getTags() {
        ContentService cs = new ContentServiceImpl();
        TagResult result = new TagResult();
        result.data = cs.getTags();
        return result;
    }
}
