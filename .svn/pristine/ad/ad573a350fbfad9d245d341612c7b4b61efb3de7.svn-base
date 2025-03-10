package aeac.sys.aeac_basetemple.listplugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.datamodel.AbstractFormDataModel;
import kd.bos.entity.datamodel.IDataModel;
import kd.bos.entity.datamodel.ListSelectedRow;
import kd.bos.form.events.BeforeExportFileEvent;
import kd.bos.form.events.ExportFileEvent;
import kd.bos.form.events.SetFilterEvent;
import kd.bos.list.IListView;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.mvc.form.FormDataModel;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.user.UserServiceHelper;

public class BaseTempleListPlugin extends AbstractListPlugin {
	
	private static final String USER_ENTITYNAME = "bos_user";
	
	/**
	 * 添加过滤
	 */
	@Override
	public void setFilter(SetFilterEvent e) {
		// TODO Auto-generated method stub
		super.setFilter(e);
		long userid = UserServiceHelper.getCurrentUserId();
		
		DynamicObject user_dy = QueryServiceHelper.queryOne(USER_ENTITYNAME, "aeac_usersecrettype.aeac_highestsecret.number", new QFilter[] {new QFilter("id", QCP.equals, userid)});
		Long user_secret_number =  -1L;
		if(user_dy != null) {
			user_secret_number = Long.parseLong(user_dy.getString("aeac_usersecrettype.aeac_highestsecret.number"));
		}
		e.getQFilters().add(new QFilter("aeac_secret.number", QCP.less_equals, user_secret_number));
	}
	/**
	 * 导出文件带单据密级
	 */
	@Override
	public void beforeExportFile(BeforeExportFileEvent e) {
		// TODO Auto-generated method stub
		super.beforeExportFile(e);
		List<Object> selectid = new ArrayList<>();
		for(ListSelectedRow dataEntity : this.getSelectedRows()) {
			Object pkid = dataEntity.getPrimaryKeyValue();
			selectid.add(pkid);
		}
		IListView listview = (IListView)this.getView();
		String formnumber = listview.getBillFormId();
		
		DynamicObjectCollection select_cols = QueryServiceHelper.query(formnumber, "aeac_secret.number,aeac_secret.name", new QFilter[] {new QFilter("id", QCP.in, selectid)});
		DynamicObject max = select_cols.stream().max((a,b)->a.getLong("aeac_secret.number") > b.getLong("aeac_secret.number")? 1:-1).get();
		String securtname = max.getString("aeac_secret.name");
		String filename = e.getFileName() + "(" + securtname + ")";
		e.setFileName(filename);
	}
}
