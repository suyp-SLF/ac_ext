package aeac.sys.aeac_basetemple.utils;

import java.io.*;
import java.net.URLEncoder;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;
import kd.bos.context.RequestContext;
import kd.bos.exception.KDBizException;
import kd.bos.ext.form.control.CustomControl;
import kd.bos.form.control.Toolbar;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.fi.er.business.servicehelper.HttpServiceHelper;

public class ShushengFormPlugin extends AbstractFormPlugin{
	@Override
	public void registerListener(EventObject e) {
	    super.registerListener(e);
	    Toolbar repairDataBtnBar = this.getControl("aeac_toolbarap");
	    repairDataBtnBar.addItemClickListener(this);
	}
	@Override
    public void afterCreateNewData(EventObject e) {
    	// TODO Auto-generated method stub
    	super.afterCreateNewData(e);
		try {
			updateTable("start");
		} catch (UnsupportedEncodingException ex) {
//			ex.printStackTrace();
			throw new KDBizException("转码失败！");
		}
	}
	
	@Override
	public void itemClick(ItemClickEvent evt) {
		
		//获得当前系统的租户id 账户id
		// TODO Auto-generated method stub
		String key = evt.getItemKey();
		super.itemClick(evt);
		try {
			updateTable(key);
		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
			throw new KDBizException("转码失败！");
		}
	}
	
	private void updateTable(String action) throws UnsupportedEncodingException {
		String token;
		try {
			token = getAccessToken();
		}catch (Exception e){
			throw new RuntimeException(e.getMessage());
		}

		if("start".equalsIgnoreCase(action)) {
			CustomControl customcontrol = this.getView().getControl("aeac_customcontrolap");
	        Map<String, Object> data = new HashMap<>();
	        
	        String path = this.getView().getFormShowParameter().getCustomParam("filepath");
	        this.getModel().setValue("aeac_path", path);
			int point = path.lastIndexOf("/");
			int kdpoint = point + path.substring(point + 1).indexOf("&");

			String filename = path.substring(point + 1, kdpoint+ 1);

			String filenameencode = URLEncoder.encode(filename, "UTF-8");
			data.put("LOD_path", path.substring(0, point + 1) + filenameencode + path.substring(kdpoint + 1) + "&access_token=" + token);

	        data.put("LOD_action", "start");
	        data.put("id", UUID.randomUUID());
	        customcontrol.setData(data);
	        
		}else if("aeac_view".equalsIgnoreCase(action)) {
			
			CustomControl customcontrol = this.getView().getControl("aeac_customcontrolap");
	        Map<String, Object> data = new HashMap<>();
	        
	        String path = this.getView().getFormShowParameter().getCustomParam("filepath");
	        this.getModel().setValue("aeac_path", path);
//	        data.put("LOD_path", URLEncoder.encode(path + "&access_token=" + token, "UTF-8"));
			int point = path.lastIndexOf("/");
			String filename = URLEncoder.encode(path.substring(point + 1), "UTF-8");
	        data.put("LOD_path", path.substring(0, point) + filename + "&access_token=" + token);
	        data.put("LOD_action", "view");
	        data.put("id", UUID.randomUUID());
	        customcontrol.setData(data);
		}
	}

	private String getAccessToken() throws Exception{
		Map<String,String> map = new HashMap<>();
//		"appId": "vouchertest",
//				"appSecuret": "Kingdee@123Kingdee@123",
//				"tenantid": "jmup",
//				"accountId": accountID,
//				"language": "zh_CN"
		map.put("appId", "vouchertest");
		map.put("appSecuret", "ZHFzhf!1234567890");
		map.put("tenantid", "cosmic");
		map.put("accountId", RequestContext.get().getAccountId());
		map.put("language", "zh_CN");
		String domain = System.getProperty("domain.contextUrl");
		String request = HttpServiceHelper.doPost(domain + "/api/getAppToken.do", map);

		JSONObject json = JSONObject.parseObject(request);
		String appToken = json.getJSONObject("data").getString("app_token");

		map.clear();
//		user: "cwinterface",
//
//            apptoken: KDAPPTOKEN,
//            tenantid: "jmup ",
//            accountId: accountID,
//            usertype: "UserName"
		map.put("user", "suyoupeng");
		map.put("apptoken", appToken);
		map.put("tenantid", "cosmic");
		map.put("accountId", RequestContext.get().getAccountId());
		map.put("usertype", "UserName");
		request = HttpServiceHelper.doPost(domain + "/api/login.do", map);
		json = JSONObject.parseObject(request);
		String accessToken = json.getJSONObject("data").getString("access_token");
		return accessToken;
	}

	public static String encode(String str, String charset)
			throws UnsupportedEncodingException {
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]+");
		Matcher m = p.matcher(str);
		StringBuffer b = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(b, URLEncoder.encode(m.group(0), charset));
		}
		m.appendTail(b);
		return b.toString();
	}
}
