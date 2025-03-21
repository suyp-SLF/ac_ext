package aeac.sys.aeac_adminlog.formplugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import kd.bos.algo.Row;
import kd.bos.algo.RowMeta;
import kd.bos.entity.BillEntityType;
import kd.bos.servicehelper.user.UserServiceHelper;
import kd.mmc.mrp.model.table.RowData;
import org.apache.commons.lang3.StringUtils;


import kd.bos.algo.DataSet;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.metadata.dynamicobject.DynamicObjectType;
import kd.bos.entity.datamodel.events.PropertyChangedArgs;
import kd.bos.form.control.EntryGrid;
import kd.bos.form.control.Label;
import kd.bos.form.control.Toolbar;
import kd.bos.form.control.events.BeforeItemClickEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.login.utils.DateUtils;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.bos.servicehelper.workflow.WorkflowServiceHelper;
import kd.bos.workflow.engine.dynprocess.freeflow.WFFlowElement;

/*
 * 修改数据库默认值   cosmic-log t_log_app  fk_aeac_adustatus = 'A'
 * 修改数据库默认值  cosmic-sys t_bas_attachment_oplog fk_aeac_adustatus = 'A'
 *
 * 扩展bos_log_operation 添加下拉框 aeac_adustatus 未审计 A，已审计B
 * 扩展bos_attachment_oplog 添加下拉框 aeac_adustatus 未审计 A，已审计B
 *
 * 报表数据源插件 aeac_adminlog
 * kd.cus.aeac.adminlog.plugin.AdminlogReportListDataPlugin
 * 用于将操作日志以及附件操作日志进行合并，并根据当前操作人显示不同的操作日志，以及审计功能
 *
 * 安全管理员
 *
 * */

public class AdminlogsFormPlugin extends AbstractFormPlugin {
	private static final String OPER_ENTITYNAME = "bos_log_operation";
	private static final String FILE_ENTITYNAME = "bos_attachment_oplog";
	
	private static final String[] OPER_FIELDS = {
			"id aeac_operid",//id
			"0 aeac_fileid",
			"user.name aeac_user",//操作用户名
			"user.number aeac_usernumber",//操作用户名
			"clientip aeac_ipaddr",//IP地址
			"bizobj.name aeac_operobj",//操作对象
			"bizapp.name aeac_appname",//应用名
			"optime aeac_optime",//操作时间
			"opnamee aeac_opername_oper",//操作操作种类
			"'' aeac_opername_file",//附件操作种类
			"opdescriptione aeac_opercontent",//操作内容
			"opdescriptione aeac_operresult",//操作结果
			"aeac_adustatus aeac_adustatus",
			"modifycontent modify",
			"'' filename",
			"'' billkeyfield"
	};
	private static final String[] FILE_FIELDS = {
			"'0' aeac_operid",
			"id aeac_fileid",//id
			"user.name aeac_user",//操作用户名
			"user.number aeac_usernumber",//操作用户名
			"clientip aeac_ipaddr",//IP地址
			"bizobj.name aeac_operobj",//操作对象
			"'' aeac_appname",//应用名
			"optime aeac_optime",//操作时间
			"'' aeac_opername_oper",//操作操作种类
			"optype aeac_opername",//附件操作种类
			"description aeac_opercontent",//操作内容
			"description aeac_operresult",//操作结果
			"aeac_adustatus aeac_adustatus",
			"'' modify",
			"filename filename",
			"billkeyfield billkeyfield"
	};

	/**
	 * 日志过滤
	 * @param log
	 * @return
	 */
	private DynamicObject logfilter(DynamicObject log){
		DynamicObjectCollection dys_cols = QueryServiceHelper.query("aeac_admnlogs_filter", "aeac_colname,aeac_keyname,aeac_type,aeac_replace", new QFilter[]{new QFilter("aeac_enable", QCP.equals, true)});

		Map<String, List<DynamicObject>> filter_group = dys_cols.stream().collect(Collectors.groupingBy(i -> i.getString("aeac_colname")));
		Iterator<Map.Entry<String, List<DynamicObject>>> it = filter_group.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, List<DynamicObject>> entry = it.next();
			String key = entry.getKey();
			List<DynamicObject> rules = entry.getValue();
			String str = log.getString(key);

			for (DynamicObject item : rules){
				String keyname = item.getString("aeac_keyname");//关键字
				String replace = item.getString("aeac_replace");//替换值
				String type = item.getString("aeac_type");//类型

				if("A".equalsIgnoreCase(type)){
					if(str.contains(keyname)){
						str = str.replace(keyname, replace);
					}
				}else if ("B".equalsIgnoreCase(type)){
					if(str.contains(keyname)){
						return null;
					}
				}
			}
			log.set(key, str);
		}
		return log;
	}

	/**
	 * 监听按钮
	 * @param e
	 */
	@Override
	public void registerListener(EventObject e) {
		// TODO Auto-generated method stub
		super.registerListener(e);
		Toolbar repairDataBtnBar = this.getControl("aeac_toolbarap");
		repairDataBtnBar.addItemClickListener(this);
	}

	/**
	 * 值改变事件
	 *
	 * 改变用户是管理员还是用户
	 * @param e
	 */
	@Override
	public void propertyChanged(PropertyChangedArgs e) {
		// TODO Auto-generated method stub
 		super.propertyChanged(e);
		String usertype = (String)this.getModel().getValue("aeac_usertype");
		if("A".equalsIgnoreCase(usertype)) {
			this.getView().setVisible(true, "aeac_userfield");
			this.getView().setVisible(false, "aeac_adminuser");
		}else if("B".equalsIgnoreCase(usertype)){
			this.getView().setVisible(false, "aeac_userfield");
			this.getView().setVisible(true, "aeac_adminuser");
		}else {
			this.getView().setVisible(false, "aeac_userfield");
			this.getView().setVisible(false, "aeac_adminuser");
		}
		
	}

	/**
	 * 审计按钮
	 *
	 * 点击审计按钮，将未审计改为已审计
	 * @param evt
	 */
	@Override
	public void beforeItemClick(BeforeItemClickEvent evt) {
		// TODO Auto-generated method stub
		super.beforeItemClick(evt);
		String operkey = evt.getOperationKey();
		if("auditlog".equalsIgnoreCase(operkey)) {
			EntryGrid reportlist = (EntryGrid) this.getControl("aeac_entryentity");
			int[] logsids = reportlist.getSelectRows();
			
			List<String> operids = new ArrayList<>();
			List<String> fileids = new ArrayList<>();
			
			DynamicObjectCollection logs = this.getModel().getEntryEntity("aeac_entryentity");
			for(int logsid : logsids) {
				DynamicObject temp = logs.get(logsid);
				temp.set("aeac_adustatus", "B");
				String operid = temp.getString("aeac_operid");
				String fileid = temp.getString("aeac_fileid");
				
				if(!"0".equalsIgnoreCase(operid)) {
					operids.add(operid);
				}else if(!"0".equalsIgnoreCase(fileid)) {
					fileids.add(fileid);
				}
			}
			
			DynamicObject[] operlogs = BusinessDataServiceHelper.load(OPER_ENTITYNAME, "id,aeac_adustatus", new QFilter[] {new QFilter("id", QCP.in, operids)});
			DynamicObject[] filelogs = BusinessDataServiceHelper.load(FILE_ENTITYNAME, "id,aeac_adustatus", new QFilter[] {new QFilter("id", QCP.in, fileids)});
			
			for(DynamicObject operlog : operlogs) {
				operlog.set("aeac_adustatus", "B");
			}
			for(DynamicObject filelog : filelogs) {
				filelog.set("aeac_adustatus", "B");
			}
			SaveServiceHelper.save(operlogs);
			SaveServiceHelper.save(filelogs);
		}else if("searchlog".equalsIgnoreCase(operkey)) {
			String msg = search();
			if(StringUtils.isNotBlank(msg)){
				this.getView().showErrorNotification(msg);
				evt.setCancel(true);
			}
		}
	}

	/**
	 * 初始化，判断当前日志数量
	 * @param e
	 */
	@Override
	public void afterCreateNewData(EventObject e) {
		// TODO Auto-generated method stub
		super.afterCreateNewData(e);
		this.getView().setVisible(false, "aeac_userfield");
		this.getView().setVisible(false, "aeac_adminuser");
		//当日的开始时间
		Calendar todayStart = Calendar.getInstance();
		todayStart.set(Calendar.HOUR_OF_DAY, 0);
		todayStart.set(Calendar.MINUTE, 0);
		todayStart.set(Calendar.SECOND, 0);
		Date dateStart = todayStart.getTime();
//		search();
		this.getModel().setValue("aeac_daterangefield_startdate",dateStart);
		this.getModel().setValue("aeac_daterangefield_enddate",dateStart);
		
		DynamicObject[] opernumber = BusinessDataServiceHelper.load(OPER_ENTITYNAME, "", null);
		DynamicObject[] filenumber = BusinessDataServiceHelper.load(FILE_ENTITYNAME, "", null);
		
		int numer = opernumber.length + filenumber.length;
		
		String condition = (String) this.getModel().getValue("aeac_conditions");
	 	Long threshold = Long.parseLong((String)this.getModel().getValue("aeac_number"));
	 	
	 	if(">".equalsIgnoreCase(condition)) {
	 		if(numer > threshold) {
//	 			this.getView().showErrorNotification();
	 			Label message = this.getControl("aeac_message");
	 			message.setText(" 当前日志：" + numer + "条！大于" + threshold);
	 		}else {
	 			this.getView().setVisible(false, "aeac_flexpanelap3");
	 		}
	 		
	 	}else if("<".equalsIgnoreCase(condition)) {
	 		if(numer < threshold) {
//	 			this.getView().showErrorNotification("当前日志：" + numer + "条！小于" + threshold);
	 			Label message = this.getControl("aeac_message");
	 			message.setText(" 当前日志：" + numer + "条！小于" + threshold);
	 		}else {
	 			this.getView().setVisible(false, "aeac_flexpanelap3");
	 		}
	 	}
	}

	/**
	 * 查询按钮
	 * @return
	 */
	private String search() {
		String usertype = (String) this.getModel().getValue("aeac_usertype");
 		DynamicObject user_field = (DynamicObject)this.getModel().getValue("aeac_userfield");
 		String adminuser = (String)this.getModel().getValue("aeac_adminuser");
		Date starttime_field = (Date)this.getModel().getValue("aeac_daterangefield_startdate");
		Date endtime_field = (Date)this.getModel().getValue("aeac_daterangefield_enddate");
		DynamicObject operobj_field = (DynamicObject) this.getModel().getValue("aeac_operobjfield");
		String ipaddr_field = (String) this.getModel().getValue("aeac_ipaddrfield");
		String logtype_field = (String) this.getModel().getValue("aeac_logtypefield");

		DynamicObject aeac_orgfield = (DynamicObject) this.getModel().getValue("aeac_orgfield");
		String  aeac_opertype = (String) this.getModel().getValue("aeac_opertype");
		String aeac_result = (String) this.getModel().getValue("aeac_result");
		String  aeac_auditstatus = (String) this.getModel().getValue("aeac_auditstatus");

		RequestContext requestContent = RequestContext.get();
		String userId = requestContent.getAccountId();
		
		QFilter[] oper_qFilters = new QFilter[13];
		QFilter[] file_qFilters = new QFilter[13];
		if("A".equalsIgnoreCase(usertype)) {
			if(null != user_field) {
				oper_qFilters[0] = new QFilter("user.id", QCP.equals, user_field.getString("id")) ;
				file_qFilters[0] = new QFilter("user.id", QCP.equals, user_field.getString("id")) ;
			}else {
				return "用户不能为空，请选择用户！";
			}
		}else if("B".equalsIgnoreCase(usertype)) {
			if(null != adminuser) {
				oper_qFilters[0] = new QFilter("user.id", QCP.equals, adminuser) ;
				file_qFilters[0] = new QFilter("user.id", QCP.equals, adminuser) ;
			}else {
				return "管理员不能为空，请选择管理员！";
			}
		}

		if(null != starttime_field) {
			oper_qFilters[1] = new QFilter("optime", QCP.large_equals, starttime_field) ;
			file_qFilters[1] = new QFilter("optime", QCP.large_equals, starttime_field) ;
		}


		if(null != endtime_field) {
			endtime_field = DateUtils.addDays(endtime_field, 1);
			oper_qFilters[2] = new QFilter("optime", QCP.less_than, endtime_field) ;
			file_qFilters[2] = new QFilter("optime", QCP.less_than, endtime_field) ;
		}

		if(null != operobj_field) {
			oper_qFilters[3] = new QFilter("bizobj.id", QCP.equals, operobj_field.getString("id"));
			file_qFilters[3] = new QFilter("bizobj.id", QCP.equals, operobj_field.getString("id"));
		}
		if(StringUtils.isNotBlank(ipaddr_field)) {
			oper_qFilters[4] = new QFilter("clientip", QCP.equals, ipaddr_field);
			file_qFilters[4] = new QFilter("clientip", QCP.equals, ipaddr_field);
		}

		if(null != aeac_orgfield){
			Object id = aeac_orgfield.getPkValue();
			List<Long> ids = new ArrayList<>();
			ids.add((Long)id);
			Set<Long> userids = UserServiceHelper.getAllUsersOfOrg(3, ids, true, true);
			oper_qFilters[8] = new QFilter("user.id", QCP.in, userids);
			file_qFilters[8] = new QFilter("user.id", QCP.in, userids);
		}
		if(StringUtils.isNotBlank(aeac_opertype)){
			oper_qFilters[9] = new QFilter("opnamee", QCP.like, "%"+aeac_opertype+"%");
			file_qFilters[9] = new QFilter("optype", QCP.like, "%"+aeac_opertype+"%");
		}
		if(StringUtils.isNotBlank(aeac_result)){
			if("A".equalsIgnoreCase(aeac_result)) {
				oper_qFilters[10] = new QFilter("opdescriptione", QCP.like, "%成功%").or(new QFilter("bizapp.name", QCP.equals, "工作流服务"));
				file_qFilters[10] = new QFilter("description", QCP.like, "%成功%");
			}else if ("B".equalsIgnoreCase(aeac_result)){
				oper_qFilters[10] = new QFilter("opdescriptione", QCP.not_like, "%成功%").and(new QFilter("bizapp.name", QCP.not_equals, "工作流服务"));
				file_qFilters[10] = new QFilter("description", QCP.not_like, "%成功%");
			}
		}
		if(StringUtils.isNotBlank(aeac_auditstatus)){
			if("B".equalsIgnoreCase(aeac_auditstatus)){
				oper_qFilters[11] = new QFilter("aeac_adustatus", QCP.equals, aeac_auditstatus);
				file_qFilters[11] = new QFilter("aeac_adustatus", QCP.equals, aeac_auditstatus);
			}else if("A".equalsIgnoreCase(aeac_auditstatus)){
				oper_qFilters[11] = new QFilter("aeac_adustatus", QCP.equals, "A").and(new QFilter("aeac_adustatus", QCP.is_null, null));
				file_qFilters[11] = new QFilter("aeac_adustatus", QCP.equals, "A").and(new QFilter("aeac_adustatus", QCP.is_null, null));
			}
		}

		oper_qFilters[7] = new QFilter("user.id", QCP.not_equals, "10");
		file_qFilters[7] = new QFilter("user.id", QCP.not_equals, "10");
		if(StringUtils.isNotBlank(logtype_field)) {
			if("A".equals(logtype_field)) {
				oper_qFilters[5] = new QFilter("user.id", QCP.not_in, new String[] {"1","2","3"});
				file_qFilters[5] = new QFilter("user.id", QCP.not_in, new String[] {"1","2","3"});
			} else if("B".equals(logtype_field)) {
				oper_qFilters[5] = new QFilter("user.id", QCP.equals, "1");
				file_qFilters[5] = new QFilter("user.id", QCP.equals, "1");
			} else if("C".equals(logtype_field)) {
				oper_qFilters[5] = new QFilter("user.id", QCP.equals, "2");
				file_qFilters[5] = new QFilter("user.id", QCP.equals, "2");
			} else if("D".equals(logtype_field)) {
				oper_qFilters[5] = new QFilter("user.id", QCP.equals, "3");
				file_qFilters[5] = new QFilter("user.id", QCP.equals, "3");
			}
		}

		String personType = this.getView().getFormShowParameter().getCustomParam("type");
		System.out.println(personType);

		if("audit".equalsIgnoreCase(personType)) {
			oper_qFilters[6] = new QFilter("user.id", QCP.in, new String[] {"1","3"}) ;
			file_qFilters[6] = new QFilter("user.id", QCP.in, new String[] {"1","3"}) ;
		}else if("security".equalsIgnoreCase(personType)) {
			oper_qFilters[6] = new QFilter("user.id", QCP.not_in, new String[] {"3", "1"})  ;
			file_qFilters[6] = new QFilter("user.id", QCP.not_in, new String[] {"3", "1"})  ;
		}else {
			oper_qFilters[6] = new QFilter("user.id", QCP.equals, "-1");
			file_qFilters[6] = new QFilter("user.id", QCP.equals, "-1");
		}
		
		
		DynamicObject this_dy = this.getModel().getDataEntity(true);
		DynamicObjectCollection logs = this_dy.getDynamicObjectCollection("aeac_entryentity");
		logs.clear();

		String algoKey = getClass().getName() + ".query";
		
		DataSet Oper_AdminLog_DataSet = QueryServiceHelper.queryDataSet(algoKey,OPER_ENTITYNAME, StringUtils.join(OPER_FIELDS, ","), oper_qFilters, "");
	 	DataSet File_AdminLog_dataSet = QueryServiceHelper.queryDataSet(algoKey,FILE_ENTITYNAME, StringUtils.join(FILE_FIELDS, ","), file_qFilters, "");
	 	//合并日志
	 	DataSet AdminLog_dataSet = Oper_AdminLog_DataSet.union(File_AdminLog_dataSet);
	 	AdminLog_dataSet = AdminLog_dataSet.copy().orderBy(new String[] {"aeac_optime desc"});
	 	DynamicObjectType type = logs.getDynamicObjectType();
	 	List<DynamicObject> loglist = new ArrayList<>();
	 	AdminLog_dataSet.forEach(m-> {
			DynamicObject log = new DynamicObject(type);
			String user = (String) m.get("aeac_usernumber");
			if ("administrator".equalsIgnoreCase(user) || "auditor".equalsIgnoreCase(user) || "security".equalsIgnoreCase(user)) {
				user = "";
			}
			String content = (String) m.get("aeac_operresult");
			content = content.contains("成功") || "工作流服务".equalsIgnoreCase(m.getString("aeac_appname")) ? "成功" : "失败";
			StringBuffer contentmsg = new StringBuffer();
			//关键字段
			if (StringUtils.isNotBlank(m.getString("billkeyfield"))){
				contentmsg.append(m.getString("billkeyfield"));
			}else {
				if (StringUtils.isNotBlank(m.getString("modify"))) {
					contentmsg.append(m.get("aeac_opercontent") + "，" + m.getString("modify"));
				} else if (StringUtils.isNotBlank(m.getString("filename"))) {
					contentmsg.append(m.getString("aeac_opercontent") + "附件名:" + m.getString("filename"));
				} else {
					contentmsg.append(m.getString("aeac_opercontent"));
				}
			}
			String opername = "未知操作";
			if (StringUtils.isNotBlank(m.getString("aeac_opername_oper"))) {
				opername = m.getString("aeac_opername_oper");
			} else if (StringUtils.isNotBlank(m.getString("aeac_opername_file"))) {
				switch (m.getString("aeac_opername_file")) {
					case "0":
						opername = "附件保存";
						break;
					case "1":
						opername = "上传";
						break;
					case "2":
						opername = "预览";
						break;
					case "3":
						opername = "下载";
						break;
					case "4":
						opername = "删除";
						break;
					case "5":
						opername = "备注";
						break;
					case "6":
						opername = "重命名";
						break;
					default:
						opername = m.getString("aeac_opername_file");
						break;
				}
			}
			//[29HLZVOWBN4G, 0, 1, 106.120.123.140, 人员, 基础服务, 2022-02-25 14:59:35.052, 登录, 密码错误，登录失败, 密码错误，登录失败, A]
			//[aeac_operid, aeac_fileid, aeac_user, aeac_ipaddr, aeac_operobj, aeac_appname, aeac_optime, aeac_opername, aeac_opercontent, aeac_operresult, aeac_adustatus]
			log.set("aeac_operid", m.get("aeac_operid"));
			log.set("aeac_fileid", m.get("aeac_fileid"));
			log.set("aeac_user", m.get("aeac_user") + " " + user);
			log.set("aeac_ipaddr", m.get("aeac_ipaddr"));
			log.set("aeac_operobj", m.get("aeac_operobj"));
			log.set("aeac_appname", m.get("aeac_appname"));
			log.set("aeac_optime", m.get("aeac_optime"));
			log.set("aeac_opername", opername);
			log.set("aeac_opercontent", contentmsg.toString());
			log.set("aeac_operresult", content);
			log.set("aeac_adustatus", StringUtils.isBlank(m.getString("aeac_adustatus")) ? "A" : m.getString("aeac_adustatus"));
			log = logfilter(log);
			if (log != null) {
				loglist.add(log);
			}

		});
	 	logs.addAll(loglist);
		return "";
	}
}
