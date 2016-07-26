package tss2.wiki.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import tss2.wiki.model.WikiImage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by coral on 16-7-25.
 */
@Controller
@RequestMapping(value = "/files")
public class UploadController {

    @RequestMapping(value = "/image", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody
    Map getFile(@RequestParam(value = "editormd-image-file") MultipartFile multipartFile) {
        WikiImage image = new WikiImage(multipartFile);
        Map result = new HashMap();
        result.put("success", 1);
        result.put("message", "上传成功");
        result.put("url", image.getImageUrl());
        return result;
    }

    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws ServletException {
        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
    }
}
