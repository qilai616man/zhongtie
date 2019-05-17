package com.brainsoon.utils;

import android.annotation.SuppressLint;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * <dl>
 * <dt>AESAlgorithmUtil</dt>
 * <dd>Description:ASE工具封装</dd>
 * <dd>Copyright: Copyright (C) 2010</dd>
 * <dd>Company: 博云易讯科技有限公司</dd>
 * <dd>CreateDate: Sep 28, 2011</dd>
 * </dl>
 * 
 * @author jijiaqi
 */
public class AESAlgorithmUtil {
	private String PASSWORD_KEY = "wktX4YuI7hXlHRhC";

	public AESAlgorithmUtil(String PASSWORD_KEY) {
		this.PASSWORD_KEY = PASSWORD_KEY;
	}

	public AESAlgorithmUtil() {
	}

	@SuppressWarnings("static-access")
	public String encrypt(String content) {
		byte[] byteContent;
		try {
			byteContent = encrypt(content.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		return this.parseByte2HexStr(byteContent);
	}

	public String decrypt(String content) {
		byte[] result = this.decrypt(parseHexStr2Byte(content));
		try {
			if (result == null) {
				return "";
			}
			return new String(result, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将二进制转换成16进制
	 * 
	 * @param buf
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	private static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * 对二进制进行加密
	 * 
	 * @param hexStr
	 * @return
	 */

	public byte[] encrypt(byte[] byteContent) {
		try {
			byte[] enCodeFormat = MD5Util.encrypt(PASSWORD_KEY);
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");

			Cipher cipher = Cipher.getInstance("AES");// 创建密码器
			cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
			byte[] result = cipher.doFinal(byteContent);
			return result;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 对二进制进行解密
	 * 
	 * @param byteContent
	 * @return
	 */
	public byte[] decrypt(byte[] byteContent) {
		try {
			byte[] enCodeFormat = MD5Util.encrypt(PASSWORD_KEY);
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");

			Cipher cipher = Cipher.getInstance("AES");// 创建密码器
			cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
			byte[] result = cipher.doFinal(byteContent);

			return result;
		} catch (NoSuchAlgorithmException e) {
			// e.printStackTrace();
			return null;
		} catch (NoSuchPaddingException e) {
			// e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			// e.printStackTrace();
			return null;
		} catch (IllegalBlockSizeException e) {
			// e.printStackTrace();
			return null;
		} catch (BadPaddingException e) {
			// e.printStackTrace();
			return null;
		}
	}

	private static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1) {
			return null;
		}

		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
					16);
			result[i] = (byte) (high * 16 + low);
		}

		return result;
	}

	public String getPasswordKey() {
		return PASSWORD_KEY;
	}

	public String getPASSWORD_KEY() {
		return PASSWORD_KEY;
	}

	public void setPASSWORD_KEY(String pASSWORDKEY) {
		PASSWORD_KEY = pASSWORDKEY;
	}

	/**
	 * 获得指定文件的byte数组
	 */
	public static byte[] getBytes(String filePath) {
		byte[] buffer = null;
		try {
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	/**
	 * 根据byte数组，生成文件
	 */
	public static void getFile(byte[] bfile, String filePath, String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);
			if (!dir.exists() && dir.isDirectory()) {// 判断文件目录是否存在
				dir.mkdirs();
			}
			file = new File(filePath + "\\" + fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bfile);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		// String sourcePath = "e:\\a.pdf";
		// String targetPath = "e:\\b.dcf";
		// String decryptPth = "e:\\c.pdf";
		//
		// //解密
		// FileInputStream fis = new FileInputStream(new File(targetPath));
		// byte[] b = new byte[fis.available()];
		// fis.read(b);
		// fis.close();
		//
		// AESAlgorithmUtil aes = new AESAlgorithmUtil("ZXT8TGjE2QT2t56P");
		//
		// byte[] decryptByte = aes.decrypt(b);
		// FileOutputStream fos = new FileOutputStream(new File(decryptPth));
		// fos.write(decryptByte);
		// fos.close();
		// AESAlgorithmUtil aes = new AESAlgorithmUtil();
		// aes.setPASSWORD_KEY("a");
		// //System.out.println(aes.decrypt("12DC1632ED6D88CA5E72D505CDA00D3E4F602C20C4416DA28B78598D96903ED4C9692A0EB48E0708E5B5C52E6D601792"));
		// String encryptstr = aes.encrypt("b");
		// System.out.println("加密:"+encryptstr);
		// System.out.println("解密:"+aes.decrypt(encryptstr));

		AESAlgorithmUtil aes = new AESAlgorithmUtil();
		aes.setPASSWORD_KEY("v_1.0_123000000004153aec00141d99f25d20002");
		String content = "9FC3892832D0FBB5EB018581C7BFCCD2A4C44BE760D05B038C3B9F69F1D681003390D698996AD40E7E3D7FACD87E4453614A204597ED8C9B618BE4747F325970EF6B57F0CB016CA39158FE70BAB82E3A94CB2160FC7DF3EAD8701DD0E0CFF6DA10EB1A26E1BB35B3572A766DBD6534CFACB4E0EBCA376A88CA82F9C434127348FDF16F104C21B79511A96B7CE8E93CDC7188D708E6FE2B09A9BD3159FF0F68ABBB561FBCA8B0B0755E32055F6B9B3599E685BA2670F8101C4974D96F046E6F1A33A4D946558C169A3B805B3567849B50E372D0A34F48A8B808D4DA1182B5B007996AF276C68D79C0F7C9CCA15A325FD85ACA7D1508F79F7B0A81534D0288F3E68E51B3F5900F07B68CE77838E12E4629895142CF4C194408F018D541D9FD70C071A5AC425F826C07F0E85C1BB00AA82D41E5CEDE84F03E626672B1A5D4FFE2DB41887E68070CA8871755617989610EB6EC10F90BCC271EBF30FC3658B300300D4AA5B4835AA58EED100C59258C4F7DF822700EECF465E50C39AB250592B916746FCA8C25D299B6603E5EE51B8BCEF79502AB441BE96694513BBAC9791965D3A937A2E85E22D069DD982328AF965F72D5CCEE7EF19BD0B15010C37D6E0FC1C63BE933E2F622F8C353FF97E4668AF3351271DEC3BEFED906F7256DE08596E7F85D9A50EDD8AC0C72C6D5DDB49932D5BE1D0F909320F04ADADC86D608D0EDF038AC501233CCF127DEA0C4220807D6DCD488099C8FD47AF6F5D031A202C166256C4B2BC3B040F00F95091DAA13A6EC9FF32C1241A8D54A9989CD12E88446740AA487BB8B55F46C39C25BE49A82C1FF8A0A752F3F02F2FC254D99C58649367AB9BF2B8EB587432DE4729F271CE083F192155F615B112B68A367BFEA91422D88D6183C17163C59060DD3D4D089EC520C223B37FAB2ED3F2AB5E1DDDF7AEA93460EB58B93EC3D789F1620C3B060FFAFD2C9E2430AFDD0949A7E734534BE51A121FC35E9";
		System.out.println(aes.decrypt(content));

		// byte result[] = aes.encrypt(getBytes("d://test.txt"));
		// //
		// getFile(result,"d://","test_2.txt");
		// FileOutputStream fos = new FileOutputStream(new
		// File("D:\\test_1.txt"));
		// fos.write(result);
		// fos.close();
		// byte[] enCodeFormat = "ce8d474787d17903f5d490bc3449b77b".getBytes();
		// // System.out.println("密钥"+StringUtil.printHexString(enCodeFormat));
		// AESAlgorithmUtil aes = new AESAlgorithmUtil();
		// aes.setPASSWORD_KEY("hsylgwk-20120101");
		// byte[] mi = aes.encrypt(enCodeFormat);
		// System.out.println(StringUtil.printHexString(mi));
		// byte[] bd = aes.encrypt("123abcABC".getBytes("GBK"));
		// // System.out.println("加密结果：" + new String(bd.));
		// System.out.println(StringUtil.printHexString(bd));
	}
}
