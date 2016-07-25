package tss2.wiki.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by coral on 16-7-25.
 */
@Controller
@RequestMapping(value = "/files")
public class UploadController {

    @RequestMapping(value = "/image", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    public @ResponseBody String getFile(@RequestParam(value = "editormd-image-file") MultipartFile file) {
        try {
            InputStream is = file.getInputStream();
            Scanner scanner = new Scanner(is);
            String result = "";
            while (scanner.hasNext()) {
                result += scanner.nextLine() + "\n";
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws ServletException {
        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
    }
}
