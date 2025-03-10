package aeac.sys.aeac_adminlog.utils;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.LocaleString;
import kd.bos.db.DB;
import kd.bos.ext.form.control.CustomControl;
import kd.bos.fileservice.FileService;
import kd.bos.fileservice.FileServiceFactory;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.login.utils.DateUtils;
import kd.bos.util.FileNameUtils;
import kd.bos.util.StringUtils;

import java.util.*;

public class WpsBillPlugin extends AbstractBillPlugIn {



    @Override
    public void afterCreateNewData(EventObject e) {
        super.afterCreateNewData(e);
        long id = DB.genGlobalLongId();
        this.getModel().setValue("aeac_wpsid", id);
    }

    @Override
    public void itemClick(ItemClickEvent evt) {
        super.itemClick(evt);
        String name = evt.getItemKey();
        if ("aeac_open".equals(name)) {
            this.openDoc("openFile");
        }
        if ("aeac_tht".equals(name)) {
            this.openDoc("tht");
        }
        if ("aeac_excel".equals(name)) {
            this.openExcel("excel");
        }
    }

    private void openExcel(String excel) {
        CustomControl customcontrol = this.getView().getControl("aeac_customcontrolap");
        Map<String, String> data = new HashMap<>();
        String path = "/cosmic-simple/679008189806542848/202203/edge_sup/edge_wpsbill/1377684016538322944/attachments/8fc3f70366344a589789d142f859d748/宋海鑫市内交通费.xlsx";
        data.put("LOD_action", "openExcel");
        data.put("updata", UUID.randomUUID().toString());
        data.put("filePath", path);
        customcontrol.setData(data);
    }

    private void openDoc(String method) {
        long wpsId = (long) this.getModel().getValue("aeac_wpsid");
        String edgeTitle = (String) this.getModel().getValue("aeac_title");
        String fileName = edgeTitle + ".doc";
        if (StringUtils.isEmpty(edgeTitle)) {
            this.getView().showTipNotification(new LocaleString("请输入发文标题").toString());
            return;
        }
        if (wpsId != 0l) {
            String tenantId = RequestContext.get().getTenantId();
            String accountId = RequestContext.get().getAccountId();
            String path = FileNameUtils.getAttachmentFileName(tenantId, accountId, wpsId, fileName);
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
        }
    }
}
