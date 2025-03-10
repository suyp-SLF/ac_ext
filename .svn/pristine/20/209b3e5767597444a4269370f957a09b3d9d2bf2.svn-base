package aeac.sys.aeac_algorithm.operplugin;

import org.apache.commons.lang3.StringUtils;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.ExtendedDataEntity;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.AddValidatorsEventArgs;
import kd.bos.entity.validate.AbstractValidator;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;

public class AlgorithmEnableAnddisableOperPlugin extends AbstractOperationServicePlugIn {
	
	private static final String ALGORITHM = "aeac_algorithm";
	
	@Override
    public void onAddValidators(AddValidatorsEventArgs e) {
        // TODO 在此添加业务逻辑
		e.addValidator(new onAddValidator());
    }
	
	class onAddValidator extends AbstractValidator {
		@Override
	    public void validate() {
			String operKey = this.getOperateKey();
			ExtendedDataEntity[] data = this.getDataEntities();
			if(data.length == 1) {
				ExtendedDataEntity dataEntity = data[0]; 
				String enable = (String)dataEntity.getValue("enable");
				if("0".equalsIgnoreCase(enable) && "enable".equalsIgnoreCase(operKey)){
					DynamicObject algorithm = BusinessDataServiceHelper.loadSingle(ALGORITHM, "", new QFilter[] {new QFilter("enable", QCP.equals, "1")});
					if(algorithm != null) {
						this.addErrorMessage(dataEntity, "已存在为启用状态的加密方法，请先禁用！");
					}
				}
				
			}else if(data.length > 1) {
				ExtendedDataEntity dataEntity = data[0]; 
				this.addErrorMessage(dataEntity, "无法操作多条数据！");
			}
		}
	}
}
