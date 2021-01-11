package org.linlinjava.litemall.core.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.log.StaticLog;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author 01375581
 * @version 1.0
 * @date 2020/3/24
 */
public class RsaUtils {
    public static final String CHARSET = "UTF-8";
    public static final String RSA_ALGORITHM = "RSA";
    public static final String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALE2kHl0eO4XNrCzMan2ceixVdu5P+oChq5o6apdOeQZZ/hg+IvSxpQcu2SeHpj52eypCkXKfKSZPqD+T9i5mX1kdF/jNFCR66LchNsPx8kfnTILXqT64iW56PDEeHFyFX8xLdFw29pgPY4F/5lCGT4H/5dQ20ceyWDrLc487jPzAgMBAAECgYAvn4Y73X0SHtLnsYBLOJtr+RwlrCbs8DjZbgA9dEg2cE1shkLE8KPE8eO6KnP2kE5sNxE3qC7O3Q/jm/RBPb9tbGTo1NNsPTn2bR+45QO8YQE4g53YO5nBURkXdjYkOD9Dhl9anRcGCsJN7lHtKED9ukAqaqqQqiYWmfjj1Io+AQJBAOUbDHTeu5cQyKi85eEEdGZuHAGKPDG+Pv7TT8m7nhisI7cInjDlGIRtKVkKt29kg/HkV5WfxMimVDejuVmoq0ECQQDGBBJYPJC54Mm0AvFVpqWHvWVj6nKIV0kfwHFBPaIgUs0t7+15CSSO4wv/7XKQvdOQSzTgMlUXa2z74Cn4/JYzAkA0Xg5m2FKAoTF8gFD7Mg15LEnygCpqJFY54QsnzV7BvPcc6zddbcBA5MLCu5RaPdt6+vqaFL7iLkKpxR6kw80BAkEAgG3foA8YJxbee7x03oKJVfLHMfi7dCZ4rPlZ+2CIKMLLhQHaWehOJ8am38UXyzzzXOEKWGcMvXXVZ7/KOU1J9QJAeSqqLumGDcJgNZKNr6MTmGvci8TIWPazL1a7PCHE7/YK0on6dh67rhMxplg/HvauYCjMsl+WdXTW6E0paNVkTw==";
    public static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCxNpB5dHjuFzawszGp9nHosVXbuT/qAoauaOmqXTnkGWf4YPiL0saUHLtknh6Y+dnsqQpFynykmT6g/k/YuZl9ZHRf4zRQkeui3ITbD8fJH50yC16k+uIluejwxHhxchV/MS3RcNvaYD2OBf+ZQhk+B/+XUNtHHslg6y3OPO4z8wIDAQAB";


    /*public static Map<String, String> createKeys(int keySize){
        //为RSA算法创建一个KeyPairGenerator对象
        KeyPairGenerator kpg;
        try{
            kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        }catch(NoSuchAlgorithmException e){
            throw new IllegalArgumentException("No such algorithm-->[" + RSA_ALGORITHM + "]");
        }

        //初始化KeyPairGenerator对象,密钥长度
        kpg.initialize(keySize);
        //生成密匙对
        KeyPair keyPair = kpg.generateKeyPair();
        //得到公钥
        Key publicKey = keyPair.getPublic();
        String publicKeyStr = Base64.encode(publicKey.getEncoded());
        //得到私钥
        Key privateKey = keyPair.getPrivate();
        String privateKeyStr = Base64.encode(privateKey.getEncoded());
        Map<String, String> keyPairMap = new HashMap<>(2);
        keyPairMap.put("publicKey", publicKeyStr);
        keyPairMap.put("privateKey", privateKeyStr);

        return keyPairMap;
    }*/

    /**
     * 得到公钥
     *
     * @param publicKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过X509编码的Key指令获得公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decode(publicKey));
        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
        return key;
    }

    /**
     * 得到私钥
     *
     * @param privateKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decode(privateKey));
        RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        return key;
    }

    /**
     * 公钥加密
     *
     * @param data
     * @param publicKey
     * @return
     */
    public static String publicEncrypt(String data, RSAPublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.encode(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), publicKey.getModulus().bitLength()));
        } catch (Exception e) {
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥解密
     *
     * @param data
     * @param privateKey
     * @return
     */

    public static String privateDecrypt(String data, RSAPrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decode(data), privateKey.getModulus().bitLength()), CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥加密
     *
     * @param data
     * @param privateKey
     * @return
     */

    public static String privateEncrypt(String data, RSAPrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return Base64.encode(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), privateKey.getModulus().bitLength()));
        } catch (Exception e) {
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 公钥解密
     *
     * @param data
     * @param publicKey
     * @return
     */

    public static String publicDecrypt(String data, RSAPublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decode(data), publicKey.getModulus().bitLength()), CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) {
        int maxBlock = 0;
        if (opmode == Cipher.DECRYPT_MODE) {
            maxBlock = keySize / 8;
        } else {
            maxBlock = keySize / 8 - 11;
        }
        int offSet = 0;
        byte[] buff;
        int i = 0;
        byte[] resultDatas;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            while (datas.length > offSet) {
                if (datas.length - offSet > maxBlock) {
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                } else {
                    buff = cipher.doFinal(datas, offSet, datas.length - offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
            resultDatas = out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("加解密阀值为[" + maxBlock + "]的数据时发生异常", e);
        }
        return resultDatas;
    }

    public static void main(String[] args) throws Exception {
        String appId = "ZSH_AYBTU_vMUMp1CH8KcSHRdE";
        String appSecret = "qhxfgmtJkcuq3jKCfTFe77nnOHyVKAOg";
        String json = "{\n" +
                "\t\"orderId\": \"pangu548516484\",\n" +
                "\t\"expressType\": \"1\",\n" +
                "\t\"jCompany\": \"测试\",\n" +
                "\t\"jContact\": \"王五\",\n" +
                "\t\"jTel\": \"15002766701\",\n" +
                "\t\"jMobile\": \"15002766701\",\n" +
                "\t\"jProvince\": \"广东省\",\n" +
                "\t\"jCity\": \"深圳市\",\n" +
                "\t\"jCounty\": \"福田区\",\n" +
                "\t\"jAddress\": \"新洲十一街万基商务大厦\",\n" +
                "\t\"dCompany\": \"测试\",\n" +
                "\t\"dContact\": \"王五\",\n" +
                "\t\"dTel\": \"15002766701\",\n" +
                "\t\"dMobile\": \"15002766701\",\n" +
                "\t\"dProvince\": \"湖北省\",\n" +
                "\t\"dCity\": \"武汉市\",\n" +
                "\t\"dCounty\": \"东西湖区\",\n" +
                "\t\"dAddress\": \"融园国际10F\",\n" +
                "\t\"parcelQuantity\": \"1\",\n" +
                "\t\"payMethod\": \"1\",\n" +
                "\t\"custId\": \"7551234567\",\n" +
                "\t\"remark\": \"备注\",\n" +
                "\t\"routeLabelService\": \"1\",\n" +
                "\t\"routeLabelForReturn\": \"1\",\n" +
                "\t\"needReturnTrackingNo\": \"1\",\n" +
                "\t\"isDoCall\": \"1\",\n" +
                "\t\"cargoList\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"name\": \"三只松鼠大礼包\",\n" +
                "\t\t\t\"count\": \"1\",\n" +
                "\t\t\t\"unit\": \"个\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"name\": \"牙膏\",\n" +
                "\t\t\t\"count\": \"1\",\n" +
                "\t\t\t\"unit\": \"盒\"\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}";
        String sign = "ad2f5b0ade7fb0b417a2fdc482819bab";
        String encodedAppId = RsaUtils.publicEncrypt(appId, RsaUtils.getPublicKey(PUBLIC_KEY));
        StaticLog.debug("密文appId: {}", encodedAppId);
        String decodedAppId = RsaUtils.privateDecrypt(encodedAppId, RsaUtils.getPrivateKey(PRIVATE_KEY));
        StaticLog.debug("解密appId: {}", decodedAppId);
        String encodedAppSecret = RsaUtils.publicEncrypt(appSecret, RsaUtils.getPublicKey(PUBLIC_KEY));
        StaticLog.debug("密文appSecret: {}", encodedAppSecret);
        String decodedAppSecret = RsaUtils.privateDecrypt(encodedAppSecret, RsaUtils.getPrivateKey(PRIVATE_KEY));
        StaticLog.debug("解密appSecret: {}", decodedAppSecret);
        String encodedJson = RsaUtils.publicEncrypt(json, RsaUtils.getPublicKey(PUBLIC_KEY));
        StaticLog.debug("密文json: {}", encodedJson);
        String decodedJson = RsaUtils.privateDecrypt(encodedJson, RsaUtils.getPrivateKey(PRIVATE_KEY));
        StaticLog.debug("解密json: {}", decodedJson);
        String encodedSign = RsaUtils.publicEncrypt(sign, RsaUtils.getPublicKey(PUBLIC_KEY));
        StaticLog.debug("密文sign: {}", encodedSign);
        String decodedSign = RsaUtils.privateDecrypt(encodedSign, RsaUtils.getPrivateKey(PRIVATE_KEY));
        StaticLog.debug("解密sign: {}", decodedSign);


    }
}
