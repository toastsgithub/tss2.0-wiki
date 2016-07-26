package tss2.wiki.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tss2.wiki.control.impl.SessionServiceimpl;
import tss2.wiki.control.service.SessionService;
import tss2.wiki.domain.Entry;
import tss2.wiki.domain.EntryResult;
import tss2.wiki.domain.HistoryListResult;
import tss2.wiki.domain.ModifyHistory;
import tss2.wiki.model.WikiModifyInfor;
import tss2.wiki.model.WikiSession;
import tss2.wiki.model.WikiUser;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/25.
 */

@Controller
@RequestMapping(value = "/modify")
public class ModifyController {
    private SessionService sessionService = new SessionServiceimpl();
    WikiModifyInfor wikiModifyInfor = new WikiModifyInfor();
    /**
     * 获取当前用户所有修改记录列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
    public @ResponseBody HistoryListResult midifyHistory(HttpServletRequest request) {
        WikiSession session = sessionService.checkUser(request);

        if (!session.isValid()) return new HistoryListResult(1, "Authentication Failed");
        WikiUser user = session.getUser();
        ArrayList<ModifyHistory> modifyHistoryArrayList = wikiModifyInfor.getModifyList(user);

        return new HistoryListResult(0,modifyHistoryArrayList);
    }

    /**
     * 获取当前用户所有修改记录列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/singleHistory", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
    public @ResponseBody EntryResult singleMidifyHistory(HttpServletRequest request,@RequestParam(value = "state") int state,@RequestParam(value = "id") long id) {
        WikiSession session = sessionService.checkUser(request);
        WikiModifyInfor wikiModifyInfor = new WikiModifyInfor();
        if (!session.isValid()) return new EntryResult(1, "Authentication Failed");
        WikiUser user = session.getUser();
        Entry entry = wikiModifyInfor.getEntry(state,id);
        if(entry==null){
            return new EntryResult(1,"No Such Entry");
        }
        return new EntryResult(0,entry);
    }

}
