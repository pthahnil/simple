package com.simple.xrcraft.common.utils.security;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.X500Name;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;

/**
 * Created by pthahnil on 2019/5/13.
 */
@Slf4j
public class RsaUtil {

	/**
	 * 加密算法RSA
	 */
	public static final String ALGORITHM = "RSA";

	public static final String DEFAULT_PADDING = "RSA/ECB/PKCS1PADDING";

	/**
	 * 编码
	 */
	private static final String CHAR_SET = "UTF-8";

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * 加密
	 * @param content
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] content, byte[] key) throws Exception {
		return encrypt(content, key, DEFAULT_PADDING);
	}

	public static byte[] encrypt(byte[] content, PublicKey key) throws Exception {
		return encrypt(content, key, DEFAULT_PADDING);
	}

	/**
	 * 加密
	 * @param content
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] content, byte[] key, String paddingMod) throws Exception {
		if(null == content){
			return null;
		}
		if(null == key){
			String info = "publicKey is empty";
			log.info(info);
			throw new RuntimeException(info);
		}
		if(StringUtils.isBlank(paddingMod)){
			String info = "please choose a padding mod";
			log.info(info);
			throw new RuntimeException(info);
		}
		return process(content, key, Cipher.ENCRYPT_MODE, paddingMod);
	}

	public static byte[] encrypt(byte[] content, PublicKey key, String paddingMod) throws Exception {
		if(null == content){
			return null;
		}
		if(null == key){
			String info = "publicKey is empty";
			log.info(info);
			throw new RuntimeException(info);
		}
		if(StringUtils.isBlank(paddingMod)){
			String info = "please choose a padding mod";
			log.info(info);
			throw new RuntimeException(info);
		}
		return process(content, key, Cipher.ENCRYPT_MODE, paddingMod);
	}

	/**
	 * 解密
	 * @param content
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] content, byte[] key) throws Exception {
		return decrypt(content, key, DEFAULT_PADDING);
	}

	public static byte[] decrypt(byte[] content, PrivateKey key) throws Exception {
		return decrypt(content, key, DEFAULT_PADDING);
	}

	/**
	 * 解密
	 * @param content
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] content, byte[] key, String paddingMod) throws Exception {
		if(null == content){
			return null;
		}
		if(null == key){
			String info = "privateKey is empty";
			log.info(info);
			throw new RuntimeException(info);
		}
		if(StringUtils.isBlank(paddingMod)){
			String info = "please choose a padding mod";
			log.info(info);
			throw new RuntimeException(info);
		}

		return process(content, key, Cipher.DECRYPT_MODE, paddingMod);
	}

	public static byte[] decrypt(byte[] content, PrivateKey key, String paddingMod) throws Exception {
		if(null == content){
			return null;
		}
		if(null == key){
			String info = "privateKey is empty";
			log.info(info);
			throw new RuntimeException(info);
		}
		if(StringUtils.isBlank(paddingMod)){
			String info = "please choose a padding mod";
			log.info(info);
			throw new RuntimeException(info);
		}
		return process(content, key, Cipher.DECRYPT_MODE, paddingMod);
	}


	private static byte[] process(byte[] data, byte[] keyBytes, int opmode, String paddingMod) throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);

		Key key;
		if(opmode == Cipher.ENCRYPT_MODE){
			key = keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
		} else if(opmode == Cipher.DECRYPT_MODE){
			key = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
		} else {
			throw new Exception("不支持加解密之外的操作");
		}

		return process(data, key, opmode, paddingMod);
	}

	/**
	 * RSA算法分段加解密数据
	 * @param data
	 * @param key
	 * @param opmode
	 * @return
	 * @throws Exception
	 */
	private static byte[] process(byte[] data, Key key, int opmode, String paddingMod) throws Exception {

		int keySize = getKeySize(key);

		Cipher cipher = Cipher.getInstance(paddingMod);
		cipher.init(opmode, key);

		int maxBlock;
		if (opmode == Cipher.DECRYPT_MODE) {
			maxBlock = keySize / 8;
		} else {
			maxBlock = keySize / 8 - 11;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] buff;
		int i = 0;

		byte[] resultDatas;
		try {
			while (data.length > offSet) {
				if (data.length - offSet > maxBlock) {
					buff = cipher.doFinal(data, offSet, maxBlock);
				} else {
					buff = cipher.doFinal(data, offSet, data.length - offSet);
				}
				out.write(buff, 0, buff.length);
				i++;
				offSet = i * maxBlock;
			}
			resultDatas = out.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException("加解密阀值为[" + maxBlock + "]的数据时发生异常", e);
		} finally {
			if(null != out){
				try { out.close(); } catch (Exception e) { }
			}
		}
		return resultDatas;
	}

	/**
	 *  生成密钥对(公钥和私钥)
	 * @return
	 * @throws Exception
	 */
	public static KeyPair genKeyPair(int keySize) throws Exception {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM);
		keyPairGen.initialize(keySize);
		return keyPairGen.generateKeyPair();
	}

	/**
	 * 私钥提取公钥
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
	public static String extractPublicKey(String privateKey) throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);

		BASE64Decoder base64Decoder = new BASE64Decoder();
		byte[] keyBytes = base64Decoder.decodeBuffer(privateKey);
		Key key = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));

		RSAPrivateKeySpec priv = keyFactory.getKeySpec(key, RSAPrivateKeySpec.class);
		RSAPublicKeySpec keySpec = new RSAPublicKeySpec(priv.getModulus(), BigInteger.valueOf(65537));

		PublicKey publicKey = keyFactory.generatePublic(keySpec);

		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(publicKey.getEncoded());
	}

	/**
	 * keystore
	 * @param fis
	 * @param pwd
	 * @return
	 * @throws Exception
	 */
	public static KeyStore loadKeyStore(InputStream fis, String pwd) throws Exception {
		return loadKeyStore(fis, pwd, "PKCS12");
	}

	/**
	 * keystore
	 * @param fis
	 * @param pwd
	 * @return
	 * @throws Exception
	 */
	public static KeyStore loadKeyStore(InputStream fis, String pwd, String keyStoreType) throws Exception {
		KeyStore ks = KeyStore.getInstance(keyStoreType);
		ks.load(fis, StringUtils.isNotBlank(pwd) ? pwd.toCharArray() : null);
		fis.close();

		return ks;
	}

	/**
	 * get privateKey from keyStore
	 * @param keyStore
	 * @param keyPwd
	 * @return
	 * @throws Exception
	 */
	public static PrivateKey getPrivateKey(KeyStore keyStore, String keyPwd) throws Exception {
		Enumeration<String> enumas = keyStore.aliases();
		if (enumas.hasMoreElements()) {
			String keyAlias = enumas.nextElement();
			return (PrivateKey) keyStore.getKey(keyAlias, keyPwd.toCharArray());
		}
		return null;
	}

	/**
	 * get publicKey from keyStore
	 * @param keyStore
	 * @return
	 * @throws Exception
	 */
	public static PublicKey getPublicKey(KeyStore keyStore) throws Exception {
		Enumeration<String> enumas = keyStore.aliases();
		if (enumas.hasMoreElements()) {
			String keyAlias = enumas.nextElement();

			Certificate cert = keyStore.getCertificate(keyAlias);
			return cert.getPublicKey();
		}
		return null;
	}

	/**
	 * key长度
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static int getKeySize(Key key) throws Exception {
		if(null == key){
			throw new RuntimeException("key is null");
		} else if(key instanceof RSAPublicKey){
			RSAPublicKey pubKey = (RSAPublicKey) key;
			return pubKey.getModulus().toString(2).length();
		} else if(key instanceof RSAPrivateKey){
			RSAPrivateKey priKey = (RSAPrivateKey) key;
			return priKey.getModulus().toString(2).length();
		} else {
			throw new RuntimeException("neither a rsa private nor a public key");
		}
	}

	/**
	 * 提取证书
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public static X509Certificate loadCertificate(String filePath) throws Exception {
		return loadCertificate(new File(filePath));
	}

	/**
	 * 提取证书
	 * @param certFile
	 * @return
	 * @throws Exception
	 */
	public static X509Certificate loadCertificate(File certFile) throws Exception {
		if (!certFile.isFile()) {
			throw new IOException(String.format("The certificate file %s doesn't exist.", certFile));
		}
		InputStream stream = new FileInputStream(certFile);
		return loadCertificate(stream);
	}

	/**
	 * 提取证书
	 * @param stream
	 * @return
	 * @throws Exception
	 */
	public static X509Certificate loadCertificate(InputStream stream) throws Exception {
		if(null == stream) {
			throw new IOException("certificate file stream null");
		}
		final CertificateFactory certificateFactoryX509 = CertificateFactory.getInstance("X.509");
		final X509Certificate certificate = (X509Certificate) certificateFactoryX509.generateCertificate(stream);
		stream.close();

		return certificate;
	}

	/**
	 * 提取私钥
	 * @param keyFileName
	 * @return
	 * @throws Exception
	 */
	public static PrivateKey loadPrivateKey(final String keyFileName) throws Exception {
		return loadPrivateKey(new FileReader(keyFileName));
	}

	/**
	 * 提取私钥
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static PrivateKey loadPrivateKey(final File file) throws Exception {
		return loadPrivateKey(new FileReader(file));
	}

	/**
	 * 提取私钥
	 * @param reader
	 * @return
	 * @throws Exception
	 */
	public static PrivateKey loadPrivateKey(Reader reader) throws Exception {
		final byte[] pemContent = readContent(reader);
		return loadPrivateKey(pemContent);
	}

	/**
	 * 提取私钥
	 * @return
	 * @throws Exception
	 */
	public static PrivateKey loadPrivateKey(byte[] pemContent) throws Exception {
		final PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(pemContent);
		final KeyFactory keyFactory =  KeyFactory.getInstance("RSA");
		final PrivateKey privateKey = keyFactory.generatePrivate(encodedKeySpec);
		return privateKey;
	}

	/**
	 * 提取公钥
	 * @param keyFileName
	 * @return
	 * @throws Exception
	 */
	public static PublicKey loadPublicKey(final String keyFileName) throws Exception {
		return loadPublicKey(new FileReader(keyFileName));
	}

	/**
	 * 提取公钥
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static PublicKey loadPublicKey(final File file) throws Exception {
		return loadPublicKey(new FileReader(file));
	}

	/**
	 * 提取公钥
	 * @param reader
	 * @return
	 * @throws Exception
	 */
	public static PublicKey loadPublicKey(final Reader reader) throws Exception {
		final byte[] pemContent = readContent(reader);
		return loadPublicKey(pemContent);
	}

	/**
	 * 提取公钥
	 * @param pemContent
	 * @return
	 * @throws Exception
	 */
	public static PublicKey loadPublicKey(byte[] pemContent) throws Exception {
		final X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(pemContent);
		final KeyFactory keyFactory =  KeyFactory.getInstance("RSA");
		return keyFactory.generatePublic(encodedKeySpec);
	}

	/**
	 * 读取文件内容
	 * @param reader
	 * @return
	 * @throws Exception
	 */
	public static byte[] readContent(Reader reader) throws Exception {
		if(null == reader) {
			throw new IOException("reader null");
		}
		final PemReader pemReader = new PemReader(reader);
		final PemObject pemObject = pemReader.readPemObject();
		final byte[] pemContent = pemObject.getContent();
		pemReader.close();

		return pemContent;
	}

	/**
	 * 公私钥，证书转byte[]
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	public static byte[] toBytes(Object obj) throws IOException {
		ByteArrayOutputStream byteArrayOutputStreamKey = new ByteArrayOutputStream();
		OutputStreamWriter outputStreamWriterKey = new OutputStreamWriter(byteArrayOutputStreamKey);
		JcaPEMWriter jcaPEMWriter = new JcaPEMWriter(outputStreamWriterKey);
		jcaPEMWriter.writeObject(obj);
		jcaPEMWriter.close();
		return byteArrayOutputStreamKey.toByteArray();
	}

	/**
	 * gen pfx
	 * @param sigAlg
	 * @param keySize
	 * @param alias
	 * @param pwd
	 * @param years
	 * @return
	 * @throws Exception
	 */
	public static byte[] genPfx(String sigAlg, int keySize, String alias, String pwd, int years) throws Exception {

		char[] pwdArray = StringUtils.isNotBlank(pwd) ? pwd.toCharArray() : null;

		CertAndKeyGen keyGen=new CertAndKeyGen("RSA",sigAlg,"BC");
		keyGen.generate(keySize);
		PrivateKey privateKey =keyGen.getPrivateKey();

		X500Name name = new X500Name(
				"李小荣", //common name of a person, e.g. "Vivette Davis"
				"XRCRAFT.INC",//organizationUnit - small organization name, e.g. "Purchasing"
				"wimift",//organizationName - large organization name, e.g. "Onizuka, Inc."
				"Shenzhen",//localityName - locality (city) name, e.g. "Palo Alto"
				"Guangdong",//stateName - state name, e.g. "California"
				"CN" //country - two letter country code, e.g. "CH"
		);

		X509Certificate cert = keyGen.getSelfCertificate(name, (long) 365 * 24 * 60 * 60 * years);

		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(null, null);

		keyStore.setKeyEntry(alias, privateKey, pwdArray, new Certificate[] { cert });
		cert.verify(cert.getPublicKey());

		ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
		keyStore.store(byteArrayOutput, pwdArray);

		byteArrayOutput.flush();
		byteArrayOutput.close();

		return byteArrayOutput.toByteArray();
	}

	public static void main(String[] args) throws Exception {

		int keySize = 1024;
		KeyPair keyPair = genKeyPair(keySize);

		String info = "hello, i'm lord melon";
		/*String encrypted = encrypt(info, publicKey);
		System.out.println(encrypted);

		String decrypted = decrypt(encrypted, privateKey);
		System.out.println(decrypted);*/

		/*byte[] encoded = encrypt(info.getBytes(), keyPair.getPublic());
		byte[] decoded = decrypt(encoded, keyPair.getPrivate());
		System.out.println(new String(decoded));*/

		/*String filePath = "E:/test/upload/bill99.pfx";
		InputStream stream = new FileInputStream(new File(filePath));

		String keyStroePwd = "123456";
		String keyPwd = "123456";
		KeyStore keyStore = getKeystore(stream, keyStroePwd);
		PrivateKey privateKey = getPrivateKey(keyStore, keyPwd);
		PublicKey publicKey = getPublicKey(keyStore);

		System.out.println(privateKey.getAlgorithm());
		System.out.println(publicKey.getAlgorithm());*/
	}

}
