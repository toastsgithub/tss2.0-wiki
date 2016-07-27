package tss2.wiki.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tss2.wiki.dao.WikiEntry;
import tss2.wiki.dao.core.DAOBase;
import tss2.wiki.domain.RecordsResult;
import tss2.wiki.model.WikiRecord;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by coral on 16-7-22.
 */
@Controller
@RequestMapping(value = "/api")
public class ApiController {

    public static final String HOST_URL = "http://121.42.184.4:8080/";

    @RequestMapping(value = "/entry/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody
    SimpleWikiPojo getWikiEntryById(@PathVariable long id) {
        DAOBase[] wikis = WikiEntry.query().where("id = " + id);
        if (wikis.length > 0) {
            WikiEntry entry = (WikiEntry) wikis[0];
            return new SimpleWikiPojo(entry.id, entry.title, entry.summery);
        }
        return null;
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody List<SimpleWikiPojo> searchWikisByKey(@RequestParam(value = "q") String q) {
        RecordsResult rr = WikiRecord.recordFuzzySearch(q);
        List<SimpleWikiPojo> result = rr.getList().stream().map(entry -> new SimpleWikiPojo(entry.id, entry.title, entry.summery)).collect(Collectors.toList());
        return result;
    }

    @RequestMapping(value = "/keymatch", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    public @ResponseBody String polish(HttpServletRequest request, @RequestParam(value = "type") String type, @RequestParam(value = "content") String content) {
        return WikiRecord.polishForDisplay(type, content);
    }
}

class SimpleWikiPojo {
    public long id;
    public String title;
    public String summary;
    public String url;

    public SimpleWikiPojo(long id, String title, String summary) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.url = ApiController.HOST_URL + "html/entry_content.html?entry=" + title;
    }
}
