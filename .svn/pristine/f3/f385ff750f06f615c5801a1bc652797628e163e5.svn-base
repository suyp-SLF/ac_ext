package aeac.sys.aeac_basetemple.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.security.interfaces.RSAPrivateKey;
import javax.crypto.Cipher;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

//解密算法
public class DecryptFileUtils {

	public static void main(String[] args) throws Exception {
		DecryptFileUtils rsa = new DecryptFileUtils();
		RSAPrivateKey privateKey = (RSAPrivateKey) rsa.readFromFile("sk.txt");
		try {
// 生成密钥
// 创建并初始化密码器
			Cipher cp = Cipher.getInstance("RSA", new BouncyCastleProvider());// 此处不能少
// BouncyCastLeProvider类在bcprov-ext-jdk16-141.jar中
			FileInputStream dataFIS = new FileInputStream("mi.txt");
// 取得要加密的数据
			int size = dataFIS.available();
			byte[] encryptByte = new byte[size];
			dataFIS.read(encryptByte);
// 如果是加密操作
// 建立文件输出流
			FileOutputStream FOS = new FileOutputStream("jiemi.txt");
			cp.init(Cipher.DECRYPT_MODE, privateKey);
// RSA算法必须采用分块加密
// 取得RSA加密的块的大小
			int blockSize = cp.getBlockSize();
			System.out.println(blockSize);
// 根据给定的输入长度 inputLen（以字节为单位），返回保存下一个 update 或 doFinal
// 操作结果所需的输出缓冲区长度（以字节为单位）。
			int j = 0;
// 分别对各块数据进行解密
			while ((encryptByte.length - j * blockSize) > 0) {
				FOS.write(cp.doFinal(encryptByte, j * blockSize, blockSize));
				j++;
			}
			FOS.close();
		} catch (Exception ex) {
// Logger.getLogger(EncryptAndDecrypt.class.getName()).log(Level.SEVERE,
// null, ex);
		}
	}

	private Object readFromFile(String fileName) throws Exception {
		ObjectInputStream input = new ObjectInputStream(new FileInputStream(fileName));
		Object obj = input.readObject();
		input.close();
		return obj;
	}
}