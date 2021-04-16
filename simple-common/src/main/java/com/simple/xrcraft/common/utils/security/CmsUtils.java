package com.simple.xrcraft.common.utils.security;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.encoders.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class CmsUtils {

    /**
     * 签名
     * @param dataToSign
     * @param cert
     * @param priKey
     * @param algorithm
     * @param encapsulate
     * @return
     * @throws Exception
     */
    public static byte[] generateSignature(byte[] dataToSign, X509Certificate cert, PrivateKey priKey, String algorithm, boolean encapsulate)
            throws Exception {
        CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
        JcaCertStore certstore = new JcaCertStore(Collections.singletonList(cert));
        generator.addCertificates(certstore);
        JcaContentSignerBuilder csBuilder = (new JcaContentSignerBuilder(algorithm)).setProvider("BC");
        ContentSigner cs = csBuilder.build(priKey);
        generator.addSignerInfoGenerator((new JcaSignerInfoGeneratorBuilder((new JcaDigestCalculatorProviderBuilder()).setProvider("BC").build())).build(cs, cert));
        CMSTypedData cmsdata = new CMSProcessableByteArray(dataToSign);
        CMSSignedData signeddata = generator.generate(cmsdata, encapsulate);
        return signeddata.getEncoded();
    }

    /**
     * 验签
     * @param p7Signed
     * @param dataBeSigned
     * @param certs
     * @return
     * @throws Exception
     */
    public static boolean verifySignature(byte[] p7Signed, byte[] dataBeSigned, List<X509Certificate> certs) throws Exception {
        CMSTypedData cmsdata = new CMSProcessableByteArray(dataBeSigned);
        CMSSignedData signedData = new CMSSignedData(cmsdata, p7Signed);

        Store<X509CertificateHolder> store = signedData.getCertificates();

        Collection<SignerInformation> signerInformations = signedData.getSignerInfos().getSigners();
        if(CollectionUtils.isEmpty(signerInformations)){
            return false;
        }

        boolean verifySucc = false;
        outer: for (SignerInformation signerInfo : signerInformations) {
            Collection<X509CertificateHolder> certCollection = store.getMatches(signerInfo.getSID());
            X509CertificateHolder certHolder = certCollection.iterator().next();
            SignerInformationVerifier verifier = (new JcaSimpleSignerInfoVerifierBuilder()).setProvider("BC").build(certHolder);
            boolean verified = signerInfo.verify(verifier);
            if(!verified){
                continue;
            }
            X509Certificate cert = (new JcaX509CertificateConverter()).setProvider("BC").getCertificate(certHolder);
            for (X509Certificate certificate : certs) {
                if(isSame(cert, certificate)){
                    verifySucc = true;
                    break outer;
                }
            }
        }
        return verifySucc;
    }

    /**
     * 加密
     * @param data
     * @param symAlg
     * @param cer
     * @return
     * @throws Exception
     */
    public static byte[] envelop(byte[] data, String symAlg, X509Certificate cer) throws Exception {
        ASN1ObjectIdentifier algId = null;
        String alg = symAlg.replaceAll("_", "").replaceAll("-", "").replace("/", "").toUpperCase();
        if ("AES128CBC".equals(alg)) {
            algId = CMSAlgorithm.AES128_CBC;
        } else if ("AES256CBC".equals(alg)) {
            algId = CMSAlgorithm.AES256_CBC;
        } else if ("AES192CBC".equals(alg)) {
            algId = CMSAlgorithm.AES192_CBC;
        } else if ("SM4CBC".equals(alg)) {
            algId = GMObjectIdentifiers.sms4_cbc;
        } else {
            throw new Exception("not supported sysAlg");
        }

        CMSTypedData msg = new CMSProcessableByteArray(data);
        CMSEnvelopedDataGenerator edGen = new CMSEnvelopedDataGenerator();
        edGen.addRecipientInfoGenerator((new JceKeyTransRecipientInfoGenerator(cer)).setProvider("BC"));
        CMSEnvelopedData ed = edGen.generate(msg, (new JceCMSContentEncryptorBuilder(algId)).setProvider("BC").build());
        return ed.getEncoded();
    }

    /**
     * 解密
     * @param envelopedData
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static byte[] openEnvelope(byte[] envelopedData, PrivateKey privateKey) throws Exception {
        CMSEnvelopedData ed = new CMSEnvelopedData(envelopedData);
        RecipientInformationStore recipients = ed.getRecipientInfos();
        Collection<RecipientInformation> c = recipients.getRecipients();
        Iterator<RecipientInformation> it = c.iterator();
        byte[] recData = null;
        if (it.hasNext()) {
            RecipientInformation recipient = it.next();
            recData = recipient.getContent((new JceKeyTransEnvelopedRecipient(privateKey)).setProvider("BC"));
        }
        return recData;
    }

    /**
     * 相同证书
     * @param certA
     * @param certB
     * @return
     * @throws Exception
     */
    public static boolean isSame(X509Certificate certA, X509Certificate certB) throws Exception {
        if(null == certA || null == certB){
            return false;
        }

        String fa = new String(Hex.encode(digest(certA.getEncoded(), "SHA1")), "utf-8");
        String fb = new String(Hex.encode(digest(certB.getEncoded(), "SHA1")), "utf-8");

        return fa.equals(fb);
    }

    private static byte[] digest(byte[] input, String algo) throws NoSuchAlgorithmException, NoSuchProviderException {
        MessageDigest mdInst = MessageDigest.getInstance(algo, "BC");
        mdInst.update(input);
        byte[] md = mdInst.digest();
        return md;
    }
}
