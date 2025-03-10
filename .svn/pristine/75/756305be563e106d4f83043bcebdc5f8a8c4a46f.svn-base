package aeac.sys.aeac_basetemple.operplugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.serialization.SerializationUtils;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.AddValidatorsEventArgs;
import kd.bos.exception.BosErrorCode;
import kd.bos.exception.KDBizException;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.workflow.WorkflowServiceHelper;
import kd.bos.workflow.bpmn.model.AuditTask;
import kd.bos.workflow.engine.impl.persistence.entity.runtime.VariableConstants;

public class BaseTempleValidateSafeLevelFlowAuditOperPlugin extends AbstractOperationServicePlugIn {

	@SuppressWarnings({"unchecked"})
	@Override
	public void onAddValidators(AddValidatorsEventArgs e) {
		//获取单据安全等级
		Map<String, String> variables = this.getOption().getVariables();
		String entityNumber = variables.get("entitynumber");
		String businessKey = variables.get("businesskey");
		Long taskId = Long.parseLong(variables.get("taskid"));
		DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingle(businessKey,entityNumber);
		int billSafeLevel = dynamicObject.getInt("aeac_secret.number");
		//判断下一步处理人是否有传过来
		String nextNodeDealPersonStr = this.getOption().getVariables().get(VariableConstants.NEXTNODEDEALPERSON);
		List<Long> nextDealPersons = new ArrayList<Long>();
		String nodeId = null;
		//批量审批时，需要自己取下一步处理人
		if(StringUtils.isBlank(nextNodeDealPersonStr)){
			//如果下一步参与人没有传过来，获取下一步处理人
			List<Map<String, Object>> dynParticipantList = WorkflowServiceHelper.getNextUserTaskNodeByBusinessKey(businessKey);
			for(int i=0;i<dynParticipantList.size();i++){
				Map<String, Object> dynParticipantMap = dynParticipantList.get(i);
				List<DynamicObject> users = (List<DynamicObject>) dynParticipantMap.get("users");
				AuditTask task =  (AuditTask) dynParticipantMap.get("nextNode");
				nodeId = task.getId().toString();
				for(DynamicObject user : users){
					Long userId = user.getLong("id");
					if(!nextDealPersons.contains(userId)){
						nextDealPersons.add(userId);
					}
				}
			}
		}
		//单条记录审批时，会将下一步处理人传过来
		else{
			List<Map<String,String>> nextNodeDealPersons = SerializationUtils.fromJsonString(nextNodeDealPersonStr, List.class);
			for(int i=0;i<nextNodeDealPersons.size();i++){
				Map<String,String> nextNodeDealPerson = (Map<String, String>) nextNodeDealPersons.get(i);
				String userIdStr = nextNodeDealPerson.get("userIds");
				nodeId =  nextNodeDealPerson.get("nodeId");
				String[] userIds = userIdStr.split(",");
				for(String userId : userIds){
					if(!nextDealPersons.contains(userId)){
						nextDealPersons.add(Long.parseLong(userId));
					}
				}
			}
		}
		QFilter[] qFilters = new QFilter[]{new QFilter("id", "in", nextDealPersons)};
		DynamicObject[] userObjects = BusinessDataServiceHelper.load("bos_user", "id,name,aeac_usersecrettype.number", qFilters);
		//遍历下一步处理人，获取安全等级高于单据安全等级的处理人
		List<Long> resultUserIds = new ArrayList<Long>();
		for(DynamicObject userObject : userObjects){
			int userSafeLevel = userObject.getInt("aeac_usersecrettype.aeac_highestsecret.number");
			if(userSafeLevel>=billSafeLevel){
				resultUserIds.add(userObject.getLong("id"));
			}
		}
		if(resultUserIds.size() ==0){
			throw new KDBizException(BosErrorCode.operationFailed, new Object[] { "因密级原因找不到下一步处理人，拒绝提交" });
		}
		//回传下一步节点处理人
		Map<String,String> dynParticipantMap = new HashMap<String,String>();
		dynParticipantMap.put("nodeId", nodeId);
		dynParticipantMap.put("userIds",StringUtils.join(resultUserIds.toArray(),","));
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		list.add(dynParticipantMap);
		//调接口，更新下一步处理人
		WorkflowServiceHelper.setDynPanticipant(taskId,list);
	}
}
