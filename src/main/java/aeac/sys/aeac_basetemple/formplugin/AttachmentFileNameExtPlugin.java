package aeac.sys.aeac_basetemple.formplugin;

import kd.bos.service.attachment.extend.action.IFileActionExtension;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;

public class AttachmentFileNameExtPlugin implements IFileActionExtension {

	@Override
	public String getFileName(String arg0, String arg1) {
		// TODO Auto-generated method stub
		System.out.println(arg0);
		//arg0是附件路径，根据附件路径获取密级
		QFilter qFilter = new QFilter("aeac_path", QCP.equals, arg0);
		DynamicObject relationObject = BusinessDataServiceHelper.loadSingle("aeac_relation", "aeac_secret",
				new QFilter[] { qFilter });
		//如果获取不为空则去修改文件名（arg1），在文件名后面+密级
		if(relationObject!=null) {
			//获取文件名（不带后缀）
			String fileName=arg1.substring(0,arg1.lastIndexOf("."));
			//获取文件类型
			String fileType=arg1.substring(arg1.lastIndexOf("."));
			//获取附件密级
			DynamicObject secretObject=relationObject.getDynamicObject("aeac_secret");
			if(secretObject!=null) {
				arg1=fileName+"_"+secretObject.getString("name")+fileType;
			}
		}
		System.out.println(arg1);
		return arg1;
	}
}
