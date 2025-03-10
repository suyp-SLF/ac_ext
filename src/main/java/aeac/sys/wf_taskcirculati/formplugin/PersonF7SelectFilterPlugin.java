package aeac.sys.wf_taskcirculati.formplugin;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.datamodel.ListSelectedRowCollection;
import kd.bos.form.container.Container;
import kd.bos.form.control.Control;
import kd.bos.form.field.BasedataEdit;
import kd.bos.form.field.FieldEdit;
import kd.bos.form.field.MulBasedataEdit;
import kd.bos.form.field.events.BeforeF7SelectEvent;
import kd.bos.form.field.events.BeforeF7SelectListener;
import kd.bos.list.BillList;
import kd.bos.list.ListShowParameter;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.epm.eb.formplugin.AbstractFormPlugin;

import java.util.EventObject;

public class PersonF7SelectFilterPlugin extends AbstractFormPlugin implements BeforeF7SelectListener {

    @Override
    public void registerListener(EventObject e) {
        String formNumber = this.getView().getEntityId();
        super.registerListener(e);
        if("wf_transfertohandletask".equalsIgnoreCase(formNumber)){
            BasedataEdit basedataEdit = this.getControl("transferto");
            basedataEdit.addBeforeF7SelectListener(this);
        }else if("wf_taskcirculation".equalsIgnoreCase(formNumber)){
            MulBasedataEdit basedataEdit = this.getControl("circulationperson");
            basedataEdit.addBeforeF7SelectListener(this);
        }else if("wf_taskcoordinate".equalsIgnoreCase(formNumber)){
            MulBasedataEdit basedataEdit = this.getControl("coordinater");
            basedataEdit.addBeforeF7SelectListener(this);
        }
    }

    @Override
    public void addF7SelectListener(BeforeF7SelectListener listener, String... keys) {
        super.addF7SelectListener(listener, keys);
    }

    @Override
    public void beforeF7Select(BeforeF7SelectEvent beforeF7SelectEvent) {
        //通过此方法可以获得是哪个控件触发的弹窗
        String fieldKey = "";
        if(beforeF7SelectEvent.getSource() instanceof  MulBasedataEdit){
            MulBasedataEdit basedataEdit = (MulBasedataEdit) beforeF7SelectEvent.getSource();
            fieldKey = basedataEdit.getFieldKey();
        }else if(beforeF7SelectEvent.getSource() instanceof  BasedataEdit){
            BasedataEdit basedataEdit = (BasedataEdit) beforeF7SelectEvent.getSource();
            fieldKey = basedataEdit.getFieldKey();
        }
        if("circulationperson".equalsIgnoreCase(fieldKey) || "transferto".equalsIgnoreCase(fieldKey) || "coordinater".equalsIgnoreCase(fieldKey)) {
            BillList billList = this.getView().getParentView().getControl("billlistap");
            Object taskId = this.getView().getParentView().getFormShowParameter().getCustomParam("taskId");

//            DynamicObject dataEntity = this.getView().getParentView().getModel().getDataEntity();
            if(taskId == null){
                ListSelectedRowCollection select = billList.getSelectedRows();
                taskId = select.get(0).getPrimaryKeyValue();
            }
            DynamicObject task = BusinessDataServiceHelper.loadSingle(taskId, "wf_taskmonitoring");
            String formkey = (String)task.getString("formkey");
            String businesskey = task.getString("businesskey");

            DynamicObject businessDy = QueryServiceHelper.queryOne(formkey, "aeac_secret.number", new QFilter[]{new QFilter("id", QCP.equals, businesskey)});
            String secret = businessDy.getString("aeac_secret.number");
            ListShowParameter listShowParameter = (ListShowParameter) beforeF7SelectEvent.getFormShowParameter();// 获取f7页面打开参数
            listShowParameter.getListFilterParameter().getQFilters().add(new QFilter("aeac_usersecrettype.aeac_highestsecret.number", QCP.large_equals, secret));// 将过滤条件加入
        }
    }
}
