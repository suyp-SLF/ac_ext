package aeac.sys.wf_taskmonitorin.listplugin;

import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.form.control.Toolbar;
import kd.bos.form.control.events.BeforeItemClickEvent;
import kd.bos.form.field.events.BeforeF7SelectEvent;
import kd.bos.form.field.events.BeforeF7SelectListener;
import kd.bos.list.plugin.AbstractListPlugin;

import java.util.EventObject;

public class BtntransferAndBtncirculatedOnlyOneListPlugin extends AbstractListPlugin implements BeforeF7SelectListener {

    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        Toolbar toolbar = this.getView().getControl("_toolbar_");
        toolbar.addItemClickListener(this);
    }

    @Override
    public void beforeItemClick(BeforeItemClickEvent evt) {
        super.beforeItemClick(evt);
        String key = evt.getItemKey();
        if("btntransfer".equalsIgnoreCase(key) || "btncirculated".equalsIgnoreCase(key)) {
            int number = this.getSelectedMainOrgIds().size();
            if (number > 1 && "btntransfer".equalsIgnoreCase(key)) {
                this.getView().showErrorNotification("转交操作只能操作一条数据！");
                evt.setCancel(true);
            }else if (number > 1 && "btncirculated".equalsIgnoreCase(key)){
                this.getView().showErrorNotification("传阅操作只能操作一条数据！");
                evt.setCancel(true);
            }
        }
    }

    @Override
    public void beforeF7Select(BeforeF7SelectEvent beforeF7SelectEvent) {
        System.out.println(1);
    }
}
