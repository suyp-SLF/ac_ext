package aeac.sys.aeac_basetemple.operplugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.ExtendedDataEntity;
import kd.bos.entity.MainEntityType;
import kd.bos.entity.datamodel.IDataModel;
import kd.bos.entity.formula.RowDataModel;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.AddValidatorsEventArgs;
import kd.bos.entity.plugin.args.BeforeOperationArgs;
import kd.bos.entity.validate.AbstractValidator;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.user.UserServiceHelper;

public class BaseTempleSaveOperPlugin extends AbstractOperationServicePlugIn {
	
	private static final String USER_ENTITYNAME = "bos_user";
	
	@Override
    public void onAddValidators(AddValidatorsEventArgs e) {
        // TODO 在此添加业务逻辑
		e.addValidator(new onAddValidator());
    }
	
	class onAddValidator extends AbstractValidator {
		@Override
	    public void validate() {
			for(ExtendedDataEntity rowDataEntity : this.getDataEntities()){
//				RowDataModel rowDataModel = new RowDataModel(this.entityKey, this.getValidateContext().getSubEntityType());
//				rowDataModel.setRowContext(rowDataEntity.getDataEntity());
				String message = checkSectrity(rowDataEntity);
				if(StringUtils.isNotBlank(message)) {
					this.addErrorMessage(rowDataEntity, message);
				}
			}
		}
	}
	
	private String checkSectrity(ExtendedDataEntity dataEntity) {
//		MainEntityType mainType = (MainEntityType)dataEntity.getDataEntity().getDataEntityType();
//        DynamicObjectCollection entryRows = dataEntity.getDataEntity().getDynamicObjectCollection(KEY_ENTRYENTITY);
		
		String message_bill = "";
		String message_file = "";
		//获得当前人员密级
		RequestContext content = RequestContext.get();
		long userid = UserServiceHelper.getCurrentUserId();
		
		DynamicObject user_dy = QueryServiceHelper.queryOne(USER_ENTITYNAME, "aeac_usersecrettype.aeac_highestsecret.number", new QFilter[] {new QFilter("id", QCP.equals, userid)});
		DynamicObject bill_secret = (DynamicObject)dataEntity.getValue("aeac_secret");
		DynamicObjectCollection bill_files = (DynamicObjectCollection) dataEntity.getDataEntity().getDynamicObjectCollection("attachmententity");;
		
		List<DynamicObject> file_secret = bill_files.stream().map(bill_file->bill_file.getDynamicObject("aeac_attsecret")).collect(Collectors.toList());
		
		Long user_secret_number = 0L;
		Long bill_secret_number = 0L;
		List<Long> file_secret_numbers = new ArrayList<>();
		Long file_secret_number = 0L;
		if(user_dy != null) {
			user_secret_number = Long.parseLong(user_dy.getString("aeac_usersecrettype.aeac_highestsecret.number"));
		}
		if(bill_secret != null) {
			bill_secret_number = Long.parseLong(bill_secret.getString("number"));
		}
		for(DynamicObject item : file_secret) {
			if(item != null) {
				String number = item.getString("number");
				if(Long.parseLong(number) > file_secret_number) {
					file_secret_number = Long.parseLong(number);
				}
			}
		}
		
		
		if(bill_secret_number > user_secret_number) {
			message_bill ="单据不符合密级规则！";
		}
		if(file_secret_number > bill_secret_number) {
			message_file = " 当前附件无法设置高于单据的密级";
		}
		
		return message_bill + message_file;
	}
}
