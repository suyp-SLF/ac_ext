package aeac.sys.aeac_adminlog.reportformplugin;

import java.util.EventObject;
import java.util.List;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.ExtendedDataEntity;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.args.BeforeOperationArgs;
import kd.bos.entity.report.IReportListModel;
import kd.bos.form.control.Control;
import kd.bos.form.control.EntryGrid;
import kd.bos.form.control.Toolbar;
import kd.bos.form.control.events.BeforeItemClickEvent;
import kd.bos.form.control.events.SelectRowsEventListener;
import kd.bos.list.BillList;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.report.ReportList;
import kd.bos.report.plugin.AbstractReportFormPlugin;

/**
 * 审计日志操作插件
 * （弃用）
 * kd.cus.aeac.adminlog.plugin.AdminlogOperPlugin
 * 
 * @author suyp
 *
 */
public class AdminlogReportFormPlugin extends AbstractListPlugin/*AbstractReportFormPlugin*/ {
	
	@Override
	public void registerListener(EventObject e) {
		// TODO Auto-generated method stub
		super.registerListener(e);
		Toolbar repairDataBtnBar = this.getControl("toolbarap");
		repairDataBtnBar.addItemClickListener(this);
	}
	
	private void addSelectRowsListener() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void beforeItemClick(BeforeItemClickEvent evt) {
		// TODO Auto-generated method stub
		super.beforeItemClick(evt);
		
		if("auditlog".equalsIgnoreCase(evt.getOperationKey())) {
			
//			EntryGrid reportlist = (EntryGrid)this.getControl("reportlistap");
//			int[] row = reportlist.getSelectRows();
			int data = this.getModel().getEntryCurrentRowIndex("reportlistap");
			
			
			
			BillList entryGrid = (BillList)this.getControl("reportlistap");
			System.out.println(1);
		}
//		DynamicObject[] dataList = e.getDataEntities();
//		
//		List<ExtendedDataEntity> selectList = e.getSelectedRows();
//		
//		e.setCancel(true);
//		e.setCancelMessage("操作取消");
	}
}
