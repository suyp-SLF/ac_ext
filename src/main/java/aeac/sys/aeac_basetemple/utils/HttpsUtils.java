package aeac.sys.aeac_basetemple.utils;

import java.util.Iterator;

import kd.tmc.fbp.service.ebservice.utils.SSLClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.alibaba.fastjson.JSONObject;

public class HttpsUtils {
	public String doPost(String url, String map, String charset) {
        org.apache.http.client.HttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        try {
            httpClient = new SSLClient();
            httpPost = new HttpPost(url);
            //设置参数
            httpPost.getMethod();
            httpPost.addHeader("Accept", "application/json");
            httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
            StringEntity stringEntity = new StringEntity(map);
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            HttpResponse response = httpClient.execute(httpPost);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, charset);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
	
	 public static void main(String[] args) throws Exception {
//		 	HttpsUtils h = new HttpsUtils();
//	    	JSONObject json = new JSONObject();
//	    	json.put("fingerprint", "gnNDZRFw1ygITfJRWFKjRtOV2P0=");
//	    	json.put("app_id", "b13188");
//	    	Object rse = h.doPost("https://166.111.60.65:443/v1.0/qrcode/login",json.toString(),"utf-8");
//	    	System.out.println(rse);
		 
		 String text = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
		 		"<message>\r\n" + 
		 		"    <head>\r\n" + 
		 		"        <version>1.0</version>\r\n" + 
		 		"        <serviceType>AuthenService</serviceType>\r\n" + 
		 		"        <messageState>false</messageState>\r\n" + 
		 		"    </head>\r\n" + 
		 		"    <body>\r\n" + 
		 		"        <accessControlResult>Permit</accessControlResult>\r\n" + 
		 		"        <authResultSet allFailed=\"true\">\r\n" + 
		 		"            <authResult authMode=\"cert\" success=\"true\" />\r\n" + 
		 		"        </authResultSet>\r\n" + 
		 		"        <attributes>\r\n" + 
		 		"            <attr name=\"dnname\" namespace=\"http://www.jit.com.cn/cinas/ias/ns/saml/saml11/X.509\">CN=岳旭平,T=yxp,OU=00,O=NORINCO,C=cn</attr>\r\n" + 
		 		"        </attributes>\r\n" + 
		 		"        <token>*******-****-****-****-************</token>\r\n" + 
		 		"    </body>\r\n" + 
		 		"</message>";
//		 SAXReader reader = new SAXReader();
		 Document doc = DocumentHelper.parseText(text);
		 Element root = doc.getRootElement();
		 Element body = root.element("body");
		 Element attributes = body.element("attributes");
		 Element attr = attributes.element("attr");
		 String value = attr.getText();
		 System.out.println(1);
	}
}
