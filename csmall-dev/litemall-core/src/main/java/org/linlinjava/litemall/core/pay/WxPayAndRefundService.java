package org.linlinjava.litemall.core.pay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.result.BaseWxPayResult;
import com.github.binarywang.wxpay.bean.result.WxPayRefundQueryResult;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.config.WxProperties;
import org.linlinjava.litemall.db.domain.LitemallAftersale;
import org.linlinjava.litemall.db.domain.LitemallOrderGoods;
import org.linlinjava.litemall.db.service.LitemallAftersaleService;
import org.linlinjava.litemall.db.service.LitemallOrderGoodsService;
import org.linlinjava.litemall.db.service.LitemallOrderService;
import org.linlinjava.litemall.db.util.AfterSaleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class WxPayAndRefundService {

    private final Log logger = LogFactory.getLog(WxPayAndRefundService.class);

    @Autowired
    private WxPayService wxPayService;


    /**
     * 微信退款
     *
     * @param payId     支付时的单号
     * @param afterSale 售后对象
     * @param totalFee  支付订单总金额  分
     * @return
     */
    public Map<String, Object> refund(String payId, LitemallAftersale afterSale, Integer totalFee) {
        String logPrefix = "【微信退款】";
        logger.info(logPrefix);
        WxPayRefundRequest wxPayRefundRequest = buildWxPayRefundRequest(payId, afterSale, totalFee);
        Map<String, Object> map = new HashMap<>();
        WxPayRefundResult result;
        try {
            result = wxPayService.refund(wxPayRefundRequest);
            logger.info(logPrefix + " >>> 下单成功");
            map.put("isSuccess", true);
            map.put("afterSaleSn", afterSale.getAftersaleSn());
            map.put("refundId", result.getRefundId());
        } catch (WxPayException e) {
            logger.error(logPrefix + " >>> 下单失败 >>> " + e.toString());
            //出现业务错误
            logger.info(logPrefix + "下单返回失败");
            logger.info("err_code:" + e.getErrCode());
            logger.info("err_code_des:" + e.getErrCodeDes());
            map.put("isSuccess", false);
            map.put("channelErrCode", e.getErrCode());
            map.put("channelErrMsg", e.getErrCodeDes());
        }
        return map;
    }


    public Map<String, Object> getWxRefundReq(String refundId,String afterSaleSn) {
        String logPrefix = "【微信退款查询】";
        Map<String, Object> map = new HashMap<>();
        WxPayRefundQueryResult result;
        try {
            result = wxPayService.refundQuery(null,null,null,refundId);
            logger.info(logPrefix + " >>> 成功");
            logger.info(logPrefix + " >>> 参数："+result.toString());
            map.putAll((Map) JSON.toJSON(result));
            map.put("isSuccess", true);
            map.put("afterSaleSn", afterSaleSn);
        } catch (WxPayException e) {
            logger.error(e);
            //出现业务错误
            logger.info(logPrefix+"返回失败");
            logger.info("err_code: "+e.getErrCode());
            logger.info("err_code_des: "+ e.getErrCodeDes());
            map.put("channelErrCode", e.getErrCode());
            map.put("channelErrMsg", e.getErrCodeDes());
            map.put("isSuccess", false);
        }
        return map;
    }

    /**
     * @param afterSale 售后对象
     * @param totalFee  订单支付的总金额，分为单位
     * @return
     */
    WxPayRefundRequest buildWxPayRefundRequest(String payId, LitemallAftersale afterSale, Integer totalFee) {
        // 微信退款请求对象
        WxPayRefundRequest request = new WxPayRefundRequest();
        request.setTransactionId(payId); //微信支付单号 payid
        // request.setOutTradeNo(refundOrder.getPayOrderId());  //商户订单号  payorderSn  与TransactionId 二选一
        request.setOutRefundNo(afterSale.getAftersaleSn());  //退款单号
        request.setRefundDesc("用户申请退款，原因：" + afterSale.getReason());  //退款描述
        int refundFee = afterSale.getAmount().multiply(new BigDecimal(100)).intValue();
        request.setRefundFee(refundFee);  //退款金额
        request.setRefundFeeType("CNY");
        request.setTotalFee(totalFee);  //订单总金额
        return request;
    }
}
