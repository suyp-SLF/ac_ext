package aeac.sys.bos_user.formplugin;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.dataentity.entity.LocaleValue;
import kd.bos.dataentity.entity.OrmLocaleValue;
import kd.bos.entity.datamodel.events.PropertyChangedArgs;
import kd.bos.form.control.Control;
import kd.bos.form.control.Toolbar;
import kd.bos.form.control.events.BeforeItemClickEvent;
import kd.bos.form.plugin.AbstractFormPlugin;

import java.util.EventObject;

public class BosUserFormlugin extends AbstractFormPlugin {

    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        Toolbar toolbar = this.getControl("tbmain");
        toolbar.addItemClickListener(this);
    }

    @Override
    public void beforeItemClick(BeforeItemClickEvent evt) {
        super.beforeItemClick(evt);
        if("save".equalsIgnoreCase(evt.getOperationKey())){
            OrmLocaleValue name = (OrmLocaleValue) this.getModel().getValue("name");//姓名
            String number = (String)this.getModel().getValue("number");//8位码
            String nameCode = name.toString() + number;

            this.getModel().setValue("aeac_namecode", nameCode);
        }
    }

    @Override
    public void propertyChanged(PropertyChangedArgs e) {
//        super.propertyChanged(e);
//        String opername = e.getProperty().getName();
//        if("name".equalsIgnoreCase(opername) || "number".equalsIgnoreCase(opername)){
//            LocaleValue name = (LocaleValue) this.getModel().getValue("name");//姓名
//            String number = (String)this.getModel().getValue("number");//8位码
//        }
       
    }
}
