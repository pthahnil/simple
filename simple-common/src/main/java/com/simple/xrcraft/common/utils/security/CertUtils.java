package com.simple.xrcraft.common.utils.security;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.X500Name;

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
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;

/**
 * @description:
 * @author pthahnil
 * @date 2021/4/19 17:14
 */
public class CertUtils {

	/**
	 * 加密算法RSA
	 */
	public static final String ALGORITHM = "RSA";

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
		return genPfx(privateKey, cert, pwd, alias);
	}

	/**
	 * gen pfx
	 * @param privateKey
	 * @param cert
	 * @param pwd
	 * @param alias
	 * @return
	 * @throws Exception
	 */
	public static byte[] genPfx(PrivateKey privateKey, X509Certificate cert, String pwd, String alias) throws Exception {
		char[] pwdArray = StringUtils.isNotBlank(pwd) ? pwd.toCharArray() : null;

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

}
