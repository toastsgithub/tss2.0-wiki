package tss2.wiki.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tss2.wiki.control.impl.SessionServiceimpl;
import tss2.wiki.control.service.SessionService;
import tss2.wiki.domain.*;
import tss2.wiki.control.service.ContentService;
import tss2.wiki.control.impl.ContentServiceImpl;
import tss2.wiki.model.WikiRecord;
import tss2.wiki.model.WikiSession;
import tss2.wiki.model.WikiUser;
import tss2.wiki.vo.WikiEntryVO;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Map;

import static tss2.wiki.model.WikiRecord.getContentByCategories;
import static tss2.wiki.model.WikiRecord.recordFuzzySearch;

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
    @RequestMapping(value = "", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
    public @ResponseBody
    CommonResult addEntry(HttpServletRequest request, @RequestBody Map map) {
        String operation = map.get("operation").toString();
        // TODO session control
        ContentService cs = new ContentServiceImpl();
        SessionService ss = new SessionServiceimpl();
        WikiSession session = ss.checkUser(request);
        if (session == null) return new CommonResult(1, "Authentication Failed");
        WikiUser user = session.getUser();
        if (user.getType() < WikiUser.USER_ADMIN) return new CommonResult(1, "Poor Privilege");
        return cs.process(new WikiEntryVO(session, map));
    }

    @RequestMapping(value = "", method = RequestMethod.PUT, produces="application/json;charset=UTF-8")
    public @ResponseBody
    CommonResult putEntry(HttpServletRequest request, @RequestBody Map map) {
        return addEntry(request, map);
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

    @RequestMapping(value = "/searchByCategories", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
    public @ResponseBody OutLineTitleResult searchByCategories(@RequestParam(value = "categories") String categories) {
        ArrayList<String> stringArrayList = getContentByCategories(categories);
        if(stringArrayList.size()==0){
            return new OutLineTitleResult(0);
        }
        return new OutLineTitleResult(1,stringArrayList);
    }


    @RequestMapping(value = "/fuzzysearch", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
    public @ResponseBody
    RecordsResult fuzzySearch(@RequestParam(value = "search") String search) {
        RecordsResult recordsResult = recordFuzzySearch(search);
        if(recordsResult.getList().size()==0){
            return new RecordsResult(0);
        }
        return recordsResult;
    }

    @RequestMapping(value = "/wiki/{title}", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
    public @ResponseBody WikiResult doGet(@PathVariable String title) {
        WikiRecord wikiRecord = new WikiRecord(title);
        if (wikiRecord.getContent() == null) {
            return new WikiResult(0);
        }
        return new WikiResult(1, wikiRecord);
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public @ResponseBody
    CommonResult deleteEntry(HttpServletRequest request, @RequestParam(value = "title") String title) {
        SessionService ss = new SessionServiceimpl();
        WikiSession session = ss.checkUser(request);
        if (session == null) {
            return new CommonResult(1, "Authentication Failed");
        }
        WikiUser user = session.getUser();
        if (user.getType() != WikiUser.USER_ADMIN) {
            return new CommonResult(1, "Authentication Failed");
        }
        WikiRecord wikiRecord = new WikiRecord(title);
        wikiRecord.delete();
        return new CommonResult(0);
    }
}
