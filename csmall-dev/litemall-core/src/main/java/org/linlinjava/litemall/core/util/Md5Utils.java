package org.linlinjava.litemall.core.util;

import cn.hutool.log.StaticLog;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author 01375581
 * @version 1.0
 * @date 2020/3/19
 */
public class Md5Utils {
    /**
     * 十六进制
     */
    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
            'b', 'c', 'd', 'e', 'f'};

    /**
     * 对字符串进行MD5加密
     *
     * @param text 明文
     * @return 密文
     */
    public static String encrypt32(String text) {
        MessageDigest msgDigest = null;

        try {
            msgDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("System doesn't support MD5 algorithm.");
        }
        try {
            msgDigest.update(text.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("System doesn't support your  EncodingException.");
        }
        byte[] bytes = msgDigest.digest();
        String md5Str = new String(encodeHex(bytes));

        return md5Str;
    }

    private static char[] encodeHex(byte[] data) {
        int l = data.length;
        char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS[0x0F & data[i]];
        }
        return out;
    }

    public static void main(String[] args){
        String appId ="ZSH_AYBTU_vMUMp1CH8KcSHRdE";
        String appSecret ="qhxfgmtJkcuq3jKCfTFe77nnOHyVKAOg";
        String json ="{\n" +
                "\t\"trackingType\": \"1\",\n" +
                "\t\"trackingNumber\": \"SF7444405206778,SF7444405206778\",\n" +
                "\t\"methodType\": \"1\"\n" +
                "\t\n" +
                "}";
        String jsona = "{\n" +
                "\t\"orderId\": \"biGQfwfvD4NIYMFZ\",\n" +
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
                "\t\"addedServiceList\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"name\": \"COD\",\n" +
                "\t\t\t\"value\": \"1\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"name\": \"INSURE\",\n" +
                "\t\t\t\"value\": \"123\"\n" +
                "\t\t}\n" +
                "\t],\n" +
                "\t\"cargoList\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"name\": \"牙刷\",\n" +
                "\t\t\t\"count\": \"1\",\n" +
                "\t\t\t\"unit\": \"只\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"name\": \"牙膏\",\n" +
                "\t\t\t\"count\": \"1\",\n" +
                "\t\t\t\"unit\": \"盒\"\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}";
        String sign = Md5Utils.encrypt32(appId+appSecret+jsona);
        StaticLog.info("签名sign:{}",sign);
    }

}
