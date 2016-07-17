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


    private SessionService sessionService = new SessionServiceimpl();

    /**
     *获取大纲列表
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
    public @ResponseBody Map getOutline() {
        WikiOutline wikiOutline = new WikiOutline();
        System.out.println(wikiOutline.getSummary());
        return wikiOutline.getSummary();
    }

    /**
     * 更改条目
     * @param map
     */
    @RequestMapping(value = "", method = RequestMethod.PUT, produces="application/json;charset=UTF-8")
    public @ResponseBody
    CommonResult updateSummary(HttpServletRequest request, @RequestBody Map map) {
        System.out.println(map);
        WikiSession session = sessionService.checkUser(request);
        if (!session.isValid()) return new CommonResult(1, "Authentication Failed");
        WikiUser user = session.getUser();
        if (user.getType() < WikiUser.USER_ADMIN) return new CommonResult(1, "Authentication Failed");
        WikiOutline wikiOutline = new WikiOutline();
        wikiOutline.setMap(map);
        return new CommonResult(0);
    }

    /**
     * 更改条目
     * @param map
     */
    @RequestMapping(value = "", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
    public @ResponseBody
    CommonResult setSummary(HttpServletRequest request, @RequestBody Map map) {
        return updateSummary(request, map);
    }


    /**
     * 获取大纲中的所有节点
     * @return
     */
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
