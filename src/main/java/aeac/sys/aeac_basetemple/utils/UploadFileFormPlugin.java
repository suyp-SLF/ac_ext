package aeac.sys.aeac_basetemple.utils;

import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.entity.LocaleString;
import kd.bos.ext.form.control.CustomControl;
import kd.bos.fileservice.FileService;
import kd.bos.fileservice.FileServiceFactory;
import kd.bos.form.BindingContext;
import kd.bos.form.FormShowParameter;
import kd.bos.form.IClientViewProxy;
import kd.bos.form.ShowType;
import kd.bos.form.control.AttachBtnOption;
import kd.bos.form.control.AttachmentPanel;
import kd.bos.form.control.Control;
import kd.bos.form.control.events.AttachmentOperaClickEvent;
import kd.bos.form.control.events.AttachmentOperaClickListener;
import kd.bos.form.field.AttachmentEdit;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.orm.util.CollectionUtils;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.session.EncreptSessionUtils;
import kd.bos.url.UrlService;
import kd.bos.util.FileNameUtils;
import kd.bos.util.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

public class UploadFileFormPlugin extends AbstractFormPlugin implements AttachmentOperaClickListener {
    private static final String KEY_ATTACHMENTPENAL = "attachmentfield";

    //书生浏览器所需要对应的格式
    private static final List<String> shushengCusType = new ArrayList<String>(){{
        add("ofd");
    }};
    //wps所需要对应的格式
    private static final List<String> wpsCusType = new ArrayList<String>(){{
        add("doc");
        add("docx");
    }};

    @Override
    public void registerListener(EventObject e) {
        AttachmentEdit panel = this.getControl(KEY_ATTACHMENTPENAL);
        panel.addOperaClickListener(this);;
    }

    @Override
    public void afterCreateNewData(EventObject e) {
        super.afterCreateNewData(e);
//        AttachmentEdit panel = this.getControl(KEY_ATTACHMENTPENAL);
//
//
//        List<AttachBtnOption> btns = new ArrayList<AttachBtnOption>();
//        panel.getAttachmentModel();
//        btns.add(new AttachBtnOption("shusheng_preview", new LocaleString("书生预览")));
//        btns.add(new AttachBtnOption("wps_preview", new LocaleString("wps预览")));
//
//        panel.addAttachOperaBtn(btns);
    }

    @Override
    public void attachmentOperaClick(AttachmentOperaClickEvent e) {
        /**控件面板
        List<Map<String, Object>> atts = panel.getAttachmentData();
        Map<String, Object> att = null;
        for (Map<String, Object> map : atts) {
            String uid = (String) map.get("uid");
            if (uid.equals(attinfo.get("uid"))) {
                att = map;
                continue;
            }
        }
        if ("attoption_1".contentEquals(e.getOperaKey())) {
            System.out.println("分享附件："+att.get("name")+att.get("url"));
        }*/

        //控件字段
        AttachmentEdit panel = (AttachmentEdit) e.getSource();
        Map<String, String> attinfo = (Map<String, String>) e.getAttachmentInfo();
        DynamicObject query = QueryServiceHelper.queryOne("bd_attachment","url", new QFilter[]{new QFilter("uid", QCP.equals, attinfo.get("uid"))});
        String url = query.getString("url");
        int pointindex = url.lastIndexOf(".");
        String filetype = url.substring(pointindex+1);
        if ("cus_preview".contentEquals(e.getOperaKey()) && shushengCusType.contains(filetype)) {
            String attachmentFullUrl = UrlService.getAttachmentFullUrl(url);
            String path = EncreptSessionUtils.encryptSession(attachmentFullUrl);
            FormShowParameter parameter = new FormShowParameter();
            parameter.setFormId("aeac_shusheng");
            parameter.setCaption("书生阅读器");
            parameter.setCustomParam("filepath",path);
            parameter.getOpenStyle().setTargetKey("tabap");
            parameter.getOpenStyle().setShowType(ShowType.MainNewTabPage);
            this.getView().showForm(parameter);
        }else if ("cus_preview".contentEquals(e.getOperaKey()) && wpsCusType.contains(filetype)){
            this.openDoc("openFile",url,"123.doc");
        }
    }

    @Override
    public void afterBindData(EventObject e) {
        AttachmentEdit attachmentfield = this.getControl(KEY_ATTACHMENTPENAL);

        List<AttachBtnOption> btns = new ArrayList<AttachBtnOption>();
        attachmentfield.getAttachmentModel();
        btns.add(new AttachBtnOption("cus_preview", new LocaleString("预览"), 0));
        attachmentfield.addAttachOperaBtn(btns);

        IClientViewProxy clientViewProxy = this.getView().getService(IClientViewProxy.class);
        DynamicObject dataEntity = this.getModel().getDataEntity();
        BindingContext bindingContext = new BindingContext(dataEntity, 0);
        Object attachmentValue = attachmentfield.getBindingValue(bindingContext);
        if(attachmentValue == null || !(attachmentValue instanceof List) || CollectionUtils.isEmpty((List)attachmentValue)){
            //附件字段数据为空，不处理可见性。
            return;
        }
        List<Map<String,Object>> attachmentDataList = (List<Map<String, Object>>) attachmentfield.getBindingValue(bindingContext);
        /*List<String> hiddenBtnList = new ArrayList<>();
        hiddenBtnList.add("print");//加入需要隐藏的自定义按钮标识*/
        for (Map<String, Object> attachmentMap : attachmentDataList) {

            if (shushengCusType.contains(attachmentMap.get("type")) || wpsCusType.contains(attachmentMap.get("type"))) {
//                String visiablePreview = "1";//是否显示预览按钮
//                String visiableDownload = "1";//是否显示下载按钮
//                String visiableDelete = "1";//是否显示删除按钮
//                String visiableRename = "1";//是否显示重命名按钮
                //visible参数规则为三位数字组成,0代表不可见,1代表可见
                attachmentMap.put("visible", "0" + "1" + "1" + "1");
                //隐藏自定义按钮
                //attachmentMap.put("hiddenBtn",hiddenBtnList);
            }else {
                attachmentMap.put("visible", "1" + "1" + "1" + "1");
            }
        }
        //向前端发送指令更新附件属性。
        clientViewProxy.setFieldProperty("attachmentfield", "v", attachmentDataList);}

    private void openExcel(String excel) {
        CustomControl customcontrol = this.getView().getControl("aeac_customcontrolap");
        Map<String, String> data = new HashMap<>();
        String path = "/cosmic-simple/679008189806542848/202203/edge_sup/edge_wpsbill/1377684016538322944/attachments/8fc3f70366344a589789d142f859d748/宋海鑫市内交通费.xlsx";
        data.put("LOD_action", "openExcel");
        data.put("updata", UUID.randomUUID().toString());
        data.put("filePath", path);
        customcontrol.setData(data);
    }

    private void openDoc(String method, String path, String fileName) {
//        long wpsId = (long) this.getModel().getValue("aeac_wpsid");
//        String edgeTitle = (String) this.getModel().getValue("aeac_title");
//        String fileName = edgeTitle + ".doc";
//        if (StringUtils.isEmpty(edgeTitle)) {
//            this.getView().showTipNotification(new LocaleString("请输入发文标题").toString());
//            return;
//        }

//            String tenantId = RequestContext.get().getTenantId();
//            String accountId = RequestContext.get().getAccountId();
//            String path = FileNameUtils.getAttachmentFileName(tenantId, accountId, wpsId, fileName);
            FileService service = FileServiceFactory.getAttachmentFileService();
            boolean exists = service.exists(path);


            CustomControl customcontrol = this.getView().getControl("aeac_customcontrolap");
            Map<String, String> data = new HashMap<>();
            if (exists) {
                data.put("LOD_action", method);
                if ("tht".equals(method)) {
                    data.put("tempUrl", "/zhf/1355813106848104448/202203/aeac_modeldemo/aeac_modeldemo/1370901714193879040/attachments/60e937dde050418b8b9cd51ba7345545/东蓝红头文件.docx");
                }
            } else {
                data.put("LOD_action", "newFile");
            }
            // updata无实际意义，只为可以刷新数据
            data.put("updata", UUID.randomUUID().toString());
            data.put("filePath", path);
            data.put("fileName", fileName);
            customcontrol.setData(data);
            //
    }
}
