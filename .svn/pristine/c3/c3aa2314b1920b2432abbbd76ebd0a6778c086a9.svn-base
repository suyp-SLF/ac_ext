package aeac.sys.wf_tasknodehandl;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.datamodel.ListSelectedRowCollection;
import kd.bos.form.events.SetFilterEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.list.BillList;
import kd.bos.list.plugin.AbstractTreeListPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.QueryServiceHelper;

import java.util.EventObject;
import java.util.List;

public class TasknodePersonF7SelectFilterPlugin extends AbstractTreeListPlugin {

    public void registerListener(EventObject e) {
        BillList billList = (BillList)this.getControl("billlistap");

    }//imageap_showchoosepage

    @Override
    public void setFilter(SetFilterEvent e) {
        BillList billList = (BillList)this.getControl("billlistap");
        Object taskId = this.getView().getParentView().getFormShowParameter().getCustomParam("taskId");

//            DynamicObject dataEntity = this.getView().getParentView().getModel().getDataEntity();
        if(taskId != null){
            DynamicObject task = BusinessDataServiceHelper.loadSingle(taskId, "wf_taskmonitoring");
            String formkey = (String)task.getString("formkey");
            String businesskey = task.getString("businesskey");

            DynamicObject businessDy = QueryServiceHelper.queryOne(formkey, "aeac_secret.number", new QFilter[]{new QFilter("id", QCP.equals, businesskey)});
            String secret = businessDy.getString("aeac_secret.number");
            List<QFilter> cusQfilter = e.getCustomQFilters();
            cusQfilter.add(new QFilter("aeac_usersecrettype.aeac_highestsecret.number", QCP.large_equals, secret));
            e.setCustomQFilters(cusQfilter);
        }
    }

    @Override
    public void afterCreateNewData(EventObject e) {
        System.out.println(1);
        super.afterCreateNewData(e);
    }

}
