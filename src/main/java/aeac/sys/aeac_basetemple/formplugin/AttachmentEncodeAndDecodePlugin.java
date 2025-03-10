package aeac.sys.aeac_basetemple.formplugin;

import java.io.*;
import java.util.Date;
import java.util.Map;

import org.ehcache.shadow.org.terracotta.offheapstore.util.ByteBufferInputStream;

import aeac.sys.aeac_basetemple.utils.RsaAndAesUtils;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.fileservice.extension.FileServiceExt;
import kd.bos.logging.Log;
import kd.bos.logging.LogFactory;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.service.attachment.FilePathService;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.isc.iscb.util.script.feature.misc.test.Sleep;

public class AttachmentEncodeAndDecodePlugin extends FilePathService implements FileServiceExt {

	private static Log logger = LogFactory.getLog(AttachmentEncodeAndDecodePlugin.class);

	@Override
	public InputStream checkFile(InputStream in, String fileName) {
		// TODO Auto-generated method stub
		return super.checkFile(in, fileName);
	}

	@Override
	public InputStream encode(String orignalPath, InputStream in) {
		System.out.println(orignalPath);
		logger.info("附件加密文件路径:"+ orignalPath);
		try {

			DynamicObject relationObject = BusinessDataServiceHelper.newDynamicObject("aeac_relation");
			relationObject.set("aeac_path", orignalPath);
			Map<String, Object> initKey = RsaAndAesUtils.initKey();
			String publicKeyStr = RsaAndAesUtils.getPublicKeyStr(initKey);
			logger.info("公钥:" + publicKeyStr);
			String privateKeyStr = RsaAndAesUtils.getPrivateKeyStr(initKey);
			logger.info("私钥:" + privateKeyStr);
			relationObject.set("aeac_publickey", publicKeyStr);
			relationObject.set("aeac_privatekey", privateKeyStr);
			SaveServiceHelper.save(new DynamicObject[] { relationObject });
			String fileName = orignalPath.substring(orignalPath.lastIndexOf("/"));
			String tempPath = System.getProperty("java.io.tmpdir");
			logger.info("tempPath==" + tempPath);
			String outFilePath = tempPath + fileName;

			RsaAndAesUtils.encryptFile(in, outFilePath, publicKeyStr);

			File outFile = new File(outFilePath);
			in = new FileInputStream(outFile);

		} catch (Exception e) {
			logger.error("----附件加密失败----");
			logger.error(e);
			// TODO Auto-generated catch block
			throw new RuntimeException("附件加密失败");
		} finally {

		}
		return in;
	}

	@Override
	public InputStream decode(String orignalPath, InputStream in) {
		logger.info("附件解密文件路径:"+ orignalPath);
		try {
			while (orignalPath.contains("//")) {
				orignalPath = orignalPath.replace("//", "/");
			}
			String fileName = orignalPath.substring(orignalPath.lastIndexOf("/"));
			QFilter qFilter = new QFilter("aeac_path", QCP.equals, orignalPath);
			DynamicObject relationObject = QueryServiceHelper.queryOne("aeac_relation", "aeac_privatekey",
					new QFilter[] { qFilter });
			String privateKey = relationObject.getString("aeac_privatekey");
			logger.info("解密私钥:" + privateKey);
			String tempPath = System.getProperty("java.io.tmpdir");
			logger.info("解密路径tempPath==" + tempPath);
			String outFilePath = tempPath + fileName;

			RsaAndAesUtils.decryptFile(in, outFilePath, privateKey);
			logger.info("结束解密:" + new Date());
			in=new FileInputStream(outFilePath);
			logger.info("开始返回数据：" + new Date());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("----附件解密失败----");
			logger.error(e);
			throw new RuntimeException("附件解密失败");
		}

		return in;
	}
}
