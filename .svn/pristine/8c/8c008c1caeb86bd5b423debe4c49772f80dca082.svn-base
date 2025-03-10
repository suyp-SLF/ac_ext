package aeac.sys.aeac_basetemple.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.security.interfaces.RSAPublicKey;
import javax.crypto.Cipher;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

//http://polydistortion.net/bc/index.html，这个包，可以从这里下载
//加密算法
public class EncryptFileUtils {
	public static void main(String[] args) throws Exception {
		EncryptFileUtils rsa = new EncryptFileUtils();
		RSAPublicKey publickKey = (RSAPublicKey) rsa.readFromFile("pk.txt");
		try {
// 生成密钥
// 创建并初始化密码器
			Cipher cp = Cipher.getInstance("RSA", new BouncyCastleProvider());// 此处不能少
// BouncyCastLeProvider类在bcprov-ext-jdk16-141.jar中
			FileOutputStream FOS = new FileOutputStream("mi.txt");
			FileInputStream dataFIS = new FileInputStream("原文.txt");
// 取得要加密的数据
			int size = dataFIS.available();
			byte[] encryptByte = new byte[size];
			dataFIS.read(encryptByte);
// 如果是加密操作
// 建立文件输出流
			cp.init(Cipher.ENCRYPT_MODE, publickKey);
// RSA算法必须采用分块加密
// 取得RSA加密的块的大小
			int blockSize = cp.getBlockSize();
			System.out.println(blockSize);
// 根据给定的输入长度 inputLen（以字节为单位），返回保存下一个 update 或 doFinal
// 操作结果所需的输出缓冲区长度（以字节为单位）。
			int outputBlockSize = cp.getOutputSize(encryptByte.length);
// 确定要加密的次数(加密块的个数)
			int leavedSize = encryptByte.length % blockSize;
			int blocksNum = leavedSize == 0 ? encryptByte.length / blockSize : encryptByte.length / blockSize + 1;
			byte[] cipherData = new byte[blocksNum * outputBlockSize];
// 对每块数据分别加密
			for (int i = 0; i < blocksNum; i++) {
				if ((encryptByte.length - i * blockSize) > blockSize) {
					cp.doFinal(encryptByte, i * blockSize, blockSize, cipherData, i * outputBlockSize);
				} else {
					cp.doFinal(encryptByte, i * blockSize, encryptByte.length - i * blockSize, cipherData,
							i * outputBlockSize);
				}
			}
			FOS.write(cipherData);
			FOS.close();
		} catch (Exception ex) {
// Logger.getLogger(EncryptAndDecrypt.class.getName()).log(Level.SEVERE,
// null, ex);
			System.out.println(ex);
		}
	}

	private Object readFromFile(String fileName) throws Exception {
		ObjectInputStream input = new ObjectInputStream(new FileInputStream(fileName));
		Object obj = input.readObject();
		input.close();
		return obj;
	}
}