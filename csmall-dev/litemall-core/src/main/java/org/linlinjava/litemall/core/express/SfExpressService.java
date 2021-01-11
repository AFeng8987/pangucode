package org.linlinjava.litemall.core.express;

import cn.hutool.log.StaticLog;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.express.config.SfExpressProperties;
import org.linlinjava.litemall.core.util.HttpClientUtil;
import org.linlinjava.litemall.core.util.Md5Utils;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.util.RsaUtils;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.domain.LitemallPlant;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

public class SfExpressService {
    private final Log logger = LogFactory.getLog(SfExpressService.class);

    private SfExpressProperties sfProperties;

    public SfExpressProperties getSfProperties() {
        return sfProperties;
    }

    public void setSfProperties(SfExpressProperties sfProperties) {
        this.sfProperties = sfProperties;
    }


    public String queryExpress(String trackingNumber) {
        JSONObject json = new JSONObject();
        json.put("trackingType", 1);
        json.put("trackingNumber", trackingNumber);
        json.put("methodType", 1);
        String result = null;
        try {
            result = sfEncodedPost(json, "/api/RouteService");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return result;
        }
    }

    public String createExpress(LitemallPlant plant, LitemallOrder order) {
        JSONObject json = new JSONObject();
        json.put("orderId", order.getOrderSn());
        json.put("expressType", 1);
        /*寄件人信息*/
        //TODO
        json.put("jCompany", plant.getPlantName());//寄件方公司名称,如果需要生成电子面单,则为必填
        json.put("jContact", plant.getSendContacts());//寄件方联系人,如果需要生成电子面单,则为必填
        json.put("jTel", plant.getSendPhone());//寄件方联系电话,如果需要生成 电子面单,则为必填
        json.put("jMobile", plant.getSendPhone());//寄件方手机号
        json.put("jAddress", plant.getSendAddress());//寄件方手机号
        /*收件人信息*/
        json.put("dCompany", order.getConsignee());//到件方公司名称
        json.put("dContact", order.getConsignee());//到件方联系人
        json.put("dTel", order.getMobile());//寄件方手机号
        json.put("dMobile", order.getMobile());//寄件方手机号
        json.put("dAddress", order.getAddress());//

        json.put("parcelQuantity", "1");//String月结卡
        json.put("payMethod", "1");//付款方式:1:寄方付
        json.put("custId",sfProperties.getCustId());//String月结卡
        //json.put("remark",null);//String备注
        json.put("routeLabelService", "1");//String路由标签查询服务:默认0。1:查询,其他:不查询
        json.put("isDoCall", "1");//是否要求通过手持终端通知顺丰收派员收件:1:要求其它为不要求

        String result = null;
        try {
            result = sfEncodedPost(json, "/api/OrderService");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return result;
        }
    }

    private String sfEncodedPost(JSONObject json, String url) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String sign = Md5Utils.encrypt32(sfProperties.getAppId() + sfProperties.getAppSecret() + json.toJSONString());
        String encodedAppId = RsaUtils.publicEncrypt(sfProperties.getAppId(), RsaUtils.getPublicKey(sfProperties.getPublicKey()));
        String encodedAppSecret = RsaUtils.publicEncrypt(sfProperties.getAppSecret(), RsaUtils.getPublicKey(sfProperties.getPublicKey()));
        String encodedJson = RsaUtils.publicEncrypt(json.toJSONString(), RsaUtils.getPublicKey(sfProperties.getPublicKey()));
        String encodedSign = RsaUtils.publicEncrypt(sign, RsaUtils.getPublicKey(sfProperties.getPublicKey()));

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appId", encodedAppId);
        paramMap.put("appSecret", encodedAppSecret);
        paramMap.put("json", encodedJson);
        paramMap.put("sign", encodedSign);
        return HttpClientUtil.doPost(sfProperties.getUrl() + url, paramMap, "UTF-8");
    }
}
