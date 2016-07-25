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

/**
 * Created by coral on 16-7-25.
 */
@Controller
@RequestMapping(value = "/files")
public class UploadController {

    @RequestMapping(value = "/image", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    public @ResponseBody String getFile(@RequestParam(value = "editormd-image-file") MultipartFile multipartFile) {
        WikiImage image = new WikiImage(multipartFile);
        return image.getImageUrl();
    }

    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws ServletException {
        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
    }
}
