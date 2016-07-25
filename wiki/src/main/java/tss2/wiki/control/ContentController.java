package tss2.wiki.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tss2.wiki.control.impl.SessionServiceimpl;
import tss2.wiki.control.service.SessionService;
import tss2.wiki.domain.*;
import tss2.wiki.control.service.ContentService;
import tss2.wiki.control.impl.ContentServiceImpl;
import tss2.wiki.model.*;
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


    private ContentService contentService = new ContentServiceImpl();
    private SessionService sessionService = new SessionServiceimpl();
    /**
     * request body:
     * {
     *   operation: 'add',
     *   data: {
     *     id: '0',
     *     username: '123',
     *     categories: '123123',
     *     summary: '12312',
     *     entry_title: 'c++',
     *     tags: '软件工程/软件开发/需求工程',
     *     alias: '[]',
     *     content: 'markdown正文',
     *     reference: '[]',
     *   }
     * }
     * @param map
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
    public @ResponseBody
    CommonResult addEntry(HttpServletRequest request, @RequestBody Map map) {
        WikiSession session = sessionService.checkUser(request);
        if (!session.isValid()) return new CommonResult(1, "Authentication Failed");
        WikiUser user = session.getUser();
        if (user.getType() < WikiUser.USER_ADMIN) return new CommonResult(1, "Poor Privilege");
        return contentService.process(new WikiEntryVO(session, map));
    }

    /**
     *增加条目
     * @param request
     * @param map
     * @return
     */
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
        TagResult result = new TagResult();
        result.data = contentService.getTags();
        return result;
    }

    /**
     * 根据标签categories获取该标签下所有条目的title
     * @param categories
     * @return  title的ArrayList
     */
    @RequestMapping(value = "/searchByCategories", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
    public @ResponseBody OutLineTitleResult searchByCategories(@RequestParam(value = "categories") String categories) {
        ArrayList<TitleAndSummary> stringArrayList = getContentByCategories(categories);
        if(stringArrayList.size()==0){
            return new OutLineTitleResult(0);
        }
        return new OutLineTitleResult(1,stringArrayList);
    }

    @RequestMapping(value = "/getAllTitleAndSummery", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
    public @ResponseBody ArrayList<OutLineContentResult> getAllTitleAndSummery(){
        WikiOutline wikiOutline = new WikiOutline();
        return wikiOutline.getTitleAndSummery();
    }


    /**
     * 返回所有条目title和alias
     * @return
     */
    @RequestMapping(value = "/getTitleAndAlias", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
    public @ResponseBody ArrayList<String> getTitleAndAlias() {
        WikiAlias wikiAlias = new WikiAlias();
        return wikiAlias.getAlias();
    }

    /**
     * 关键字搜索
     * @param search
     * @return 获取不带条目具体内容的条目列表（排好序）
     */
    @RequestMapping(value = "/fuzzysearch", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
    public @ResponseBody RecordsResult fuzzySearch(@RequestParam(value = "search") String search) {
        RecordsResult recordsResult = recordFuzzySearch(search);
        if(recordsResult.getList().size()==0){
            return new RecordsResult(0);
        }
        return recordsResult;
    }

    /**
     * 根据title获取带条目具体内容的单个条目
     * @param title
     * @return
     */
    @RequestMapping(value = "/wiki/{title}", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
    public @ResponseBody WikiResult doGet(HttpServletRequest request, @PathVariable String title) {
        title = getTitle(request);
        WikiRecord wikiRecord = new WikiRecord(title);
        WikiReference wikiReference = new WikiReference(title);
        if (wikiRecord.getContent() == null) {
            return new WikiResult(0);
        }
        wikiRecord.addVisit();
        return new WikiResult(1, wikiRecord,wikiReference);
    }

    private String getTitle(HttpServletRequest request) {
        String url = request.getRequestURI();
        return url.substring("/content/wiki/".length());
    }

    /**
     * 根据title删除条目
     * @param request
     * @param title
     * @return
     */
    @RequestMapping(value = "/wiki/{title}", method = RequestMethod.DELETE, produces="application/json;charset=UTF-8")
    public @ResponseBody CommonResult deleteEntry(HttpServletRequest request, @PathVariable String title) {
        WikiSession session = sessionService.checkUser(request);
        if (!session.isValid()) {
            return new CommonResult(1, "Authentication Failed");
        }
        WikiUser user = session.getUser();
        if (user.getType() != WikiUser.USER_ADMIN) {
            return new CommonResult(1, "Authentication Failed");
        }
        WikiRecord wikiRecord = new WikiRecord(title);
        if (wikiRecord.getContent() == null) {
            wikiRecord.delete();
            return new CommonResult(2, "Entry Not Exist");
        }
        wikiRecord.delete();
        return new CommonResult(0);
    }

    @RequestMapping(value = "/keyword", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    public @ResponseBody String markKeyWord(@RequestBody String content) {
        return WikiRecord.polish(content);
    }
}
