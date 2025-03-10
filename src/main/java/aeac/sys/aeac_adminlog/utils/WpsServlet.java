package aeac.sys.aeac_adminlog.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kd.bos.actiondispatcher.ActionUtil;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.LocaleString;
import kd.bos.fileservice.FileItem;
import kd.bos.fileservice.FileService;
import kd.bos.fileservice.FileServiceFactory;
import kd.bos.logging.Log;
import kd.bos.logging.LogFactory;
import kd.bos.session.EncreptSessionUtils;
import kd.bos.url.UrlService;
import kd.bos.util.FileNameUtils;
import kd.bos.util.StringUtils;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WpsServlet  {

    private static final long serialVersionUID = -3684350433229591708L;


    Log logger = LogFactory.getLog(WpsServlet.class);

    /**
     * 上传文件
     * @param request
     * @param response
     * @throws IOException
     */
    public void upFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("进入wps的upFile方法");
        request.setCharacterEncoding("utf-8");

        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding("UTF-8");
        try {
            List items = upload.parseRequest(request);
            for (Object object : items) {
                org.apache.commons.fileupload.FileItem fileItem = (org.apache.commons.fileupload.FileItem) object;
                String filePath = fileItem.getFieldName();
                String fileName = fileItem.getName();
                logger.info("wps的upFile， 格式化前-filePath= " + filePath + " ,fileName= " + fileName);
                if (StringUtils.isNotEmpty(fileName)) {
                    if (fileName.contains("pdf") || fileName.contains("ofd")) {
                        String sPath = filePath.substring(0, filePath.length()- fileName.length());
                        filePath = sPath + fileName;
                    } else if (!(fileName.contains("doc") || !fileName.contains("docx"))) {
                        String[] split = filePath.split("/");
                        if (split.length> 1) {
                            fileName = split[split.length-1];
                        }
                    }
                }
                InputStream inputStream = fileItem.getInputStream();
                logger.info("wps的upFile， 格式化后-filePath= " + filePath + " ,fileName= " + fileName);
                FileItem newFileItem = new FileItem(fileName, filePath, inputStream);
                FileService service = FileServiceFactory.getAttachmentFileService();
                String newUrl = service.upload(newFileItem);
                String attachmentFullUrl = UrlService.getAttachmentFullUrl(newUrl);
                logger.info("wps的upFile， attachmentFullUrl= " + attachmentFullUrl);
                inputStream.close();
                return;
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取文件流
     * @param request
     * @param response
     * @throws IOException
     */
    public void getFileStream(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("进入wps的getFileStream方法");
        String filepath = request.getParameter("path");
        logger.info("wps的getFileStream， filePath= " + filepath);
        String[] split = filepath.split("/");
        String fileName = new LocaleString("公文").toString();
        if (split != null && split.length > 1) {
            fileName = split[split.length -1];
        }
        logger.info("wps的getFileStream， fileName= " + fileName);
        FileService service = FileServiceFactory.getAttachmentFileService();
        String name = URLEncoder.encode(fileName, "UTF-8");
        InputStream inputStream = service.getInputStream(filepath);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + name);
        ServletOutputStream sos = response.getOutputStream();
        byte[] b = new byte[1024];
        int n;
        int l = 0;
        while ((n = inputStream.read(b)) != -1) {
            l = l + n;
            sos.write(b, 0, n);
        }
        response.setContentLength(l);
        inputStream.close();
        sos.close();
    }


    /**
     * 获取套红模板信息
     * @param request
     * @param response
     * @throws IOException
     */
    public void getTemplateData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject json1 = new JSONObject();
        json1.put("name", "Title");
        json1.put("text", "填充模板测试，这是填充到书签名称为Title");
        json1.put("type", "text");

        JSONObject json2 = new JSONObject();
        json2.put("name", "Content");
        json2.put("text", "正文内容模拟填充，这是填充到书签名称为Content");
        json2.put("type", "text");
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(json1);
        jsonArray.add(json2);
        String s = jsonArray.toJSONString();
        System.out.println(s);
        ActionUtil.writeResponseJson(response, jsonArray.toJSONString());

    }


}


