package aeac.sys.usertreelistf7.listPlugin;

import com.alibaba.fastjson.JSONArray;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.IFormView;
import kd.bos.form.events.SetFilterEvent;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.QueryServiceHelper;

import java.util.List;

public class Usertreelistf7FilterPlugin extends AbstractListPlugin {
    @Override
    public void setFilter(SetFilterEvent e) {
        super.setFilter(e);
        Object formnumber = this.getView().getEntityId();
        IFormView parentView = this.getView().getViewNoPlugin(this.getView().getFormShowParameter().getParentPageId());
        String name = parentView.getEntityId();
        if(parentView != null && ("wf_transfertohandletask".equalsIgnoreCase(name) || "wf_taskcirculation".equalsIgnoreCase(name) || "wf_taskcoordinate".equalsIgnoreCase(name) || "wf_addsignpage".equalsIgnoreCase(name))){
            Object taskid = parentView.getFormShowParameter().getCustomParam("taskid");
            if(taskid == null) {
                taskid = ((JSONArray)parentView.getFormShowParameter().getCustomParam("taskIds")).get(0);
            }
            DynamicObject task = BusinessDataServiceHelper.loadSingle(taskid, "wf_taskmonitoring");
            String formkey = (String)task.getString("formkey");
            String businesskey = task.getString("businesskey");

            DynamicObject businessDy = QueryServiceHelper.queryOne(formkey, "aeac_secret.number", new QFilter[]{new QFilter("id", QCP.equals, businesskey)});
            String secret = businessDy.getString("aeac_secret.number");
            e.getQFilters().add(new QFilter("aeac_usersecrettype.aeac_highestsecret.number", QCP.large_equals, secret));
        }
    }
}
