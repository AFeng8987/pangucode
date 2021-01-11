package org.linlinjava.litemall.core.express;

import cn.hutool.log.StaticLog;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.linlinjava.litemall.core.util.HttpClientUtil;
import org.linlinjava.litemall.core.util.Md5Utils;
import org.linlinjava.litemall.core.util.RsaUtils;

import java.util.HashMap;
import java.util.Map;

public class SfExpressTest {
    public static final String CHARSET = "UTF-8";
    public static final String RSA_ALGORITHM = "RSA";
    public static final String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALE2kHl0eO4XNrCzMan2ceixVdu5P+oChq5o6apdOeQZZ/hg+IvSxpQcu2SeHpj52eypCkXKfKSZPqD+T9i5mX1kdF/jNFCR66LchNsPx8kfnTILXqT64iW56PDEeHFyFX8xLdFw29pgPY4F/5lCGT4H/5dQ20ceyWDrLc487jPzAgMBAAECgYAvn4Y73X0SHtLnsYBLOJtr+RwlrCbs8DjZbgA9dEg2cE1shkLE8KPE8eO6KnP2kE5sNxE3qC7O3Q/jm/RBPb9tbGTo1NNsPTn2bR+45QO8YQE4g53YO5nBURkXdjYkOD9Dhl9anRcGCsJN7lHtKED9ukAqaqqQqiYWmfjj1Io+AQJBAOUbDHTeu5cQyKi85eEEdGZuHAGKPDG+Pv7TT8m7nhisI7cInjDlGIRtKVkKt29kg/HkV5WfxMimVDejuVmoq0ECQQDGBBJYPJC54Mm0AvFVpqWHvWVj6nKIV0kfwHFBPaIgUs0t7+15CSSO4wv/7XKQvdOQSzTgMlUXa2z74Cn4/JYzAkA0Xg5m2FKAoTF8gFD7Mg15LEnygCpqJFY54QsnzV7BvPcc6zddbcBA5MLCu5RaPdt6+vqaFL7iLkKpxR6kw80BAkEAgG3foA8YJxbee7x03oKJVfLHMfi7dCZ4rPlZ+2CIKMLLhQHaWehOJ8am38UXyzzzXOEKWGcMvXXVZ7/KOU1J9QJAeSqqLumGDcJgNZKNr6MTmGvci8TIWPazL1a7PCHE7/YK0on6dh67rhMxplg/HvauYCjMsl+WdXTW6E0paNVkTw==";
    public static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCxNpB5dHjuFzawszGp9nHosVXbuT/qAoauaOmqXTnkGWf4YPiL0saUHLtknh6Y+dnsqQpFynykmT6g/k/YuZl9ZHRf4zRQkeui3ITbD8fJH50yC16k+uIluejwxHhxchV/MS3RcNvaYD2OBf+ZQhk+B/+XUNtHHslg6y3OPO4z8wIDAQAB";


    @Test
    public void SfExpressAuery() throws Exception  {
        String appId ="ZSH_AYBTU_vMUMp1CH8KcSHRdE";
        String appSecret ="qhxfgmtJkcuq3jKCfTFe77nnOHyVKAOg";
       /* String json ="{\n" +
                "\t\"trackingType\": \"1\",\n" +
                "\t\"trackingNumber\": \"SF7444419029950\",\n" +
                "\t\"methodType\": \"1\"\n" +
                "\t\n" +
                "}";*/
        JSONObject json=new JSONObject();
        json.put("trackingType",1);
        json.put("trackingNumber","SF7444419034191,SF7444506197167");
        json.put("methodType",1);
        String sign = Md5Utils.encrypt32(appId+appSecret+json.toJSONString());
        StaticLog.info("签名sign:{}",sign);
        String encodedAppId = RsaUtils.publicEncrypt(appId, RsaUtils.getPublicKey(PUBLIC_KEY));
        StaticLog.debug("密文appId: {}", encodedAppId);
        String encodedAppSecret = RsaUtils.publicEncrypt(appSecret, RsaUtils.getPublicKey(PUBLIC_KEY));
        StaticLog.debug("密文appSecret: {}", encodedAppSecret);
        String encodedJson = RsaUtils.publicEncrypt(json.toJSONString(), RsaUtils.getPublicKey(PUBLIC_KEY));
        StaticLog.debug("密文json: {}", encodedJson);
        String encodedSign = RsaUtils.publicEncrypt(sign, RsaUtils.getPublicKey(PUBLIC_KEY));
        StaticLog.debug("密文sign: {}", encodedSign);
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("appId",encodedAppId);
        paramMap.put("appSecret",encodedAppSecret);
        paramMap.put("json",encodedJson);
        paramMap.put("sign",encodedSign);
        String result= HttpClientUtil.doPost("https://ewm-order-miniarch-sit.sf-express.com/api/RouteService",paramMap,"UTF-8");

        JSONObject jsonObject=  JSONObject.parseObject(result);
        Integer code = jsonObject.getInteger("code");
        if (200==code){
            StaticLog.debug("响应码："+code+"响应消息: {}", jsonObject.get("msg"));
            StaticLog.debug("Json: {}", jsonObject.get("json"));
            JSONArray jsonArray=jsonObject.getJSONArray("json");
            for (int i = 0; i < jsonArray.size(); i++) {
                StaticLog.debug("快递单号: {}", jsonArray.getJSONObject(i).getString("mailNo"));
                StaticLog.debug("商城订单号: {}",jsonArray.getJSONObject(i).getString("orderId"));
                StaticLog.debug("物流轨迹: {}",jsonArray.getJSONObject(i).getString("routeResponseRouteList").toString());
            }

        }
        else
            StaticLog.debug("失败原因: {}", jsonObject.get("msg"));

/*
               Map<String, Object> retMap = JSON.parseObject(result);
            if (200==(int)retMap.get("code")){
            StaticLog.debug("响应参数: {}", retMap.get("msg"));
            List<Map> data= (List<Map>) retMap.get("json");
            for (Map map:data) {
                StaticLog.debug("快递单号: {}", map.get("mailNo"));
                StaticLog.debug("商城订单号: {}", map.get("orderId"));
                StaticLog.debug("轨迹: {}", map.get("routeResponseRouteList").toString());
            }

        }else
            StaticLog.debug("返回参数: {}", retMap.get("msg"));*/
    }



    @Test
    public void SfExpressCreatOrder() throws Exception  {
        String appId ="ZSH_AYBTU_vMUMp1CH8KcSHRdE";
        String appSecret ="qhxfgmtJkcuq3jKCfTFe77nnOHyVKAOg";
        String json = "{\n" +
                "\t\"orderId\": \"PG202010161639\",\n" +
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
                "\t\"parcelQuantity\": \"2\",\n" +   //包裹数
                "\t\"payMethod\": \"1\",\n" +
                "\t\"custId\": \"7551234567\",\n" +
                "\t\"remark\": \"备注\",\n" +
                "\t\"routeLabelService\": \"1\",\n" +
                "\t\"routeLabelForReturn\": \"1\",\n" +
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
        String sign = Md5Utils.encrypt32(appId+appSecret+json);
        StaticLog.info("签名sign:{}",sign);
        String encodedAppId = RsaUtils.publicEncrypt(appId, RsaUtils.getPublicKey(PUBLIC_KEY));
        StaticLog.debug("密文appId: {}", encodedAppId);
        String encodedAppSecret = RsaUtils.publicEncrypt(appSecret, RsaUtils.getPublicKey(PUBLIC_KEY));
        StaticLog.debug("密文appSecret: {}", encodedAppSecret);
        String encodedJson = RsaUtils.publicEncrypt(json, RsaUtils.getPublicKey(PUBLIC_KEY));
        StaticLog.debug("密文json: {}", encodedJson);
        String encodedSign = RsaUtils.publicEncrypt(sign, RsaUtils.getPublicKey(PUBLIC_KEY));
        StaticLog.debug("密文sign: {}", encodedSign);
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("appId",encodedAppId);
        paramMap.put("appSecret",encodedAppSecret);
        paramMap.put("json",encodedJson);
        paramMap.put("sign",encodedSign);
        String result= HttpClientUtil.doPost("https://ewm-order-miniarch-sit.sf-express.com/api/OrderService",paramMap,"UTF-8");
        JSONObject jsonObject = JSONObject.parseObject(result);
        Integer code = jsonObject.getInteger("code");
        if (200==code){
            StaticLog.debug("响应码："+code+"响应消息: {}", jsonObject.get("msg"));
            StaticLog.debug("JSON: {}", jsonObject.get("json"));
            StaticLog.debug("快递单号: {}", jsonObject.getJSONObject("json").get("mailNo"));
            StaticLog.debug("商城订单号: {}", jsonObject.getJSONObject("json").get("orderId"));
        }else
            StaticLog.debug("返回参数: {}", jsonObject.get("msg"));
    }




}
