
package com.csvParser.controllers;

import com.csvParser.models.Response;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Controller
public class FileUploadController {
    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public @ResponseBody
    Response<String> upload(HttpServletRequest request) {
        try {
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            if (!isMultipart) {
                return new Response<>(false, "Not a multipart request.", "");
            }

            ServletFileUpload upload = new ServletFileUpload();

            FileItemIterator iter = upload.getItemIterator(request);
            while (iter.hasNext()) {
                FileItemStream item = iter.next();
                String name = item.getFieldName();
                InputStream stream = item.openStream();
                if (!item.isFormField()) {
                    String filename = item.getName();
                    OutputStream out = new FileOutputStream(filename);
                    IOUtils.copy(stream, out);
                    stream.close();
                    out.close();
                }
            }
        } catch (FileUploadException e) {
            return new Response<>(false, "File upload error", e.toString());
        } catch (IOException e) {
            return new Response<>(false, "Internal server IO error", e.toString());
        }

        return new Response<>(true, "Success", "");
    }

    @RequestMapping(value = "/uploader", method = RequestMethod.GET)
    public String uploaderPage() {
        return "uploader";
    }
}