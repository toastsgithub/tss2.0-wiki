package tss2.wiki.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tss2.wiki.control.impl.SessionServiceimpl;
import tss2.wiki.control.service.SessionService;
import tss2.wiki.domain.OutLineResult;
import tss2.wiki.domain.CommonResult;
import tss2.wiki.model.WikiOutline;
import tss2.wiki.model.WikiSession;
import tss2.wiki.model.WikiUser;

import javax.servlet.http.HttpServletRequest;
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
    public @ResponseBody
    CommonResult updateSummary(HttpServletRequest request, @RequestBody Map map) {
        System.out.println(map);
        //DAOBase[] content = Summary.query().where("");
        //content[0].setValue("summaryJO",map.toString());
        //content[0].save();
        SessionService ss = new SessionServiceimpl();
        WikiSession session = ss.checkUser(request);
        if (session == null) return new CommonResult(1, "Authentication Failed");
        WikiUser user = session.getUser();
        if (user.getType() < WikiUser.USER_ADMIN) return new CommonResult(1, "Authentication Failed");
        WikiOutline wikiOutline = new WikiOutline();
        wikiOutline.setMap(map);
        return new CommonResult(0);
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
    public @ResponseBody
    CommonResult setSummary(HttpServletRequest request, @RequestBody Map map) {
        return updateSummary(request, map);
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
