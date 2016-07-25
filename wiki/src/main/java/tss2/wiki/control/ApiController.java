package tss2.wiki.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tss2.wiki.dao.WikiEntry;
import tss2.wiki.dao.core.DAOBase;
import tss2.wiki.domain.RecordsResult;
import tss2.wiki.model.WikiRecord;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by coral on 16-7-22.
 */
@Controller
@RequestMapping(value = "/api")
public class ApiController {
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
    public @ResponseBody List<SimplerWikiPojo> searchWikisByKey(@RequestParam(value = "q") String q) {
        RecordsResult rr = WikiRecord.recordFuzzySearch(q);
        List<SimplerWikiPojo> result = rr.getList().stream().map(entry -> new SimplerWikiPojo(entry.id, entry.title)).collect(Collectors.toList());
        return result;
    }

    @RequestMapping(value = "/keymatch", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String polish(@RequestParam(value = "content") String content) {
        return WikiRecord.polishForDisplay(content);
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
        this.url = "http://121.42.184.4/html/entry_content.html?entry=" + title;
    }
}

class SimplerWikiPojo {
    public long id;
    public String title;
    public String url;

    public SimplerWikiPojo(long id, String title) {
        this.id = id;
        this.title = title;
        this.url = "http://121.42.184.4/html/entry_content.html?entry=" + title;
    }
}