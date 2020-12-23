package com.simple.xrcraft.common.utils.security;

/**
 * @description:
 * @author pthahnil
 * @date 2020/12/15 14:55
 */
public class ArcFour {

	/**
	 * 加密解密 参数为String 字符串（String data, String key)
	 *
	 * @param data
	 * @param key
	 * @return String
	 */
	public static final int vactorLen = 256;

	public static byte[] process(byte[] data, byte[] key) {
		// 1.参数检查
		if (data != null && data.length > 0 && key != null && key.length > 0) {
			// 2. 初始化算法
			// 2.1定义矢量vactor
			int[] vactor = new int[vactorLen];
			// 2.2定义临时变量tempk
			byte[] tempK = new byte[vactorLen];
			// 2.3给vactor赋值ֵ0-255
			for (int i = 0; i < vactorLen; i++)
				vactor[i] = i;
			int j = 1;
			// 2.4循环给tempK赋值
			for (short i = 0; i < vactorLen; i++) {
//				tempK[i] = (byte) key.charAt((i % key.length()));
				tempK[i] = key[(i % key.length)];
			}
			j = 0;
			// 2.5 置换vactor值
			for (int i = 0; i < 255; i++) {
				j = (j + vactor[i] + tempK[i]) % vactorLen;
				int temp = vactor[i];
				vactor[i] = vactor[j];
				vactor[j] = temp;
			}
			int i = 0;
			j = 0;
			// 2.6把data定义成char[]数组dataChar
			char[] dataChar = new String(data).toCharArray();
			// 2.7计算数组dataChar[]长度，用InputCharLen接收。
			int charLen = dataChar.length;
			// 2.8 定义 长度为InputCharLen的char数组iOutputChar[]
			char[] resultChar = new char[charLen];
			// 2.9 加密解密算法
			for (short x = 0; x < charLen; x++) {
				i = (i + 1) % vactorLen;
				j = (j + vactor[i]) % vactorLen;
				int temp = vactor[i];
				vactor[i] = vactor[j];
				vactor[j] = temp;
				char tempChar = (char) vactor[(vactor[i] + (vactor[j] % vactorLen)) % vactorLen];
				resultChar[x] = (char) (dataChar[x] ^ tempChar);
			}
			// 3. 输出结果
			return new String(resultChar).getBytes();
		}
		return null;
	}

}
