package aeac.sys.aeac_basetemple.utils;

import kd.bos.form.control.events.attach.manager.AttachOpEvent;
import kd.bos.mservice.attachment.AttachmentInfo;
import kd.bos.mservice.attachment.AttachmentOpType;
import kd.bos.servicehelper.attachment.IAbstractAttachManagerPlugin;

import java.util.List;

public class FileBeforeOpt implements IAbstractAttachManagerPlugin {
    @Override
    public AttachOpEvent checkOpRight(AttachOpEvent event) {

        AttachmentOpType optType = event.getOpType();
        if(AttachmentOpType.PreView == optType){
            List<AttachmentInfo> info = event.getData();

        }
        return IAbstractAttachManagerPlugin.super.checkOpRight(event);
    }
}
