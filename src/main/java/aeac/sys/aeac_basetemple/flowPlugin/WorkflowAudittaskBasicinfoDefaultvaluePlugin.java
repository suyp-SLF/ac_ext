package aeac.sys.aeac_basetemple.flowPlugin;

import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.workflow.design.plugin.AbstractWorkflowConfigurePlugin;

import java.util.EventObject;

public class WorkflowAudittaskBasicinfoDefaultvaluePlugin extends AbstractWorkflowConfigurePlugin {
    @Override
    public void afterCreateNewData(EventObject e) {
//        super.afterCreateNewData(e);
        this.getModel().setValue("operationwhensubmit", "审批时安全等级校验");
        this.setProperty("operationwhensubmit", "validatesafelevelflowaudit");
    }
}
