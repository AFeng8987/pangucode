package org.linlinjava.litemall.core.pay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AlipayService {

    private final Log logger = LogFactory.getLog(AlipayService.class);

    @Autowired
    private AlipayConfig alipayConfig;

    /**
     * 支付宝APP支付下单
     * @param payOrderSn  下单付款的订单号
     * @param totalAmount  总付款金额
     * @param body  //商品名
     * @param subject  描述
     * @return
     */
    public AlipayTradeAppPayResponse doAliPayMobileReq(String payOrderSn, BigDecimal totalAmount,String body,String subject) {
        String logPrefix = "【支付宝APP支付下单】";
        logger.info(logPrefix);
        AlipayClient client = new DefaultAlipayClient(alipayConfig.getUrl(), alipayConfig.getAppId(), alipayConfig.getRsaPrivateKey(), AlipayConfig.FORMAT, AlipayConfig.CHARSET, alipayConfig.getRsaPublicKey(), AlipayConfig.SIGNTYPE);
        AlipayTradeAppPayRequest  request  = new AlipayTradeAppPayRequest();
        // 封装请求支付信息
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setOutTradeNo(payOrderSn);
        model.setTimeoutExpress("90m");
        model.setSubject(subject);
        model.setTotalAmount(totalAmount.toString());
        model.setBody(body);
        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setBizModel(model);
        // 设置异步通知地址
        request.setNotifyUrl(alipayConfig.getNotifyUrl());
        // 设置同步地址
        //request.setReturnUrl(alipayConfig.getReturnUrl());
        AlipayTradeAppPayResponse response = new AlipayTradeAppPayResponse();
        try {
             response = client.sdkExecute(request);
        } catch (AlipayApiException e) {
            logger.error(logPrefix+"异常："+e.getErrMsg());
            e.printStackTrace();
        }
        logger.info(logPrefix+"生成请求支付宝数据,payParams="+response.getBody());
        if(response.isSuccess()){
            logger.info(logPrefix+"调用成功");
            return response;
        } else {
            logger.info(logPrefix+"调用失败");
            return null;
        }

    }


    /**
     * 支付宝退款
     * @param afterOrderSn 退款单号
     * @param payId     支付交易号
     * @param payOrderSn   订单号
     * @param refundAmount  退款金额
     * @return
     */
    public AlipayTradeRefundResponse doAliRefundReq(String afterOrderSn,String payId,String payOrderSn,BigDecimal refundAmount) {
        String logPrefix = "【支付宝退款】";
        logger.info(logPrefix);
        AlipayClient client = new DefaultAlipayClient(alipayConfig.getUrl(), alipayConfig.getAppId(), alipayConfig.getRsaPrivateKey(), AlipayConfig.FORMAT, AlipayConfig.CHARSET, alipayConfig.getRsaPublicKey(), AlipayConfig.SIGNTYPE);

        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        model.setOutTradeNo(payOrderSn);//
        model.setTradeNo(payId);
        model.setOutRequestNo(afterOrderSn);//标识一次退款请求，同一笔交易多次退款需要保证唯一。
        model.setRefundAmount(refundAmount.toString());
        model.setRefundReason("正常退款");
        request.setBizModel(model);
        AlipayTradeRefundResponse response=new AlipayTradeRefundResponse();
        try {
             response = client.execute(request);
        } catch (AlipayApiException e) {
            logger.error(logPrefix+"异常："+e.getErrMsg());
        }
        logger.info(logPrefix+"响应信息,Msg ： "+response.getMsg());
        logger.info(logPrefix+"响应体参数, json ： "+response.getBody());
   /*     if(response.isSuccess()){
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }*/
        return response;
    }

  /*  public Map doAliPayNotify(String jsonParam) {
        String logPrefix = "【处理支付宝支付回调】";
        logger.info("====== 开始处理支付宝支付回调通知 ======");
        BaseParam baseParam = JsonUtil.getObjectFromJson(jsonParam, BaseParam.class);
        Map<String, Object> bizParamMap = baseParam.getBizParamMap();
        if (ObjectValidUtil.isInvalid(bizParamMap)) {
            _log.warn("处理支付宝支付回调失败, {}. jsonParam={}", RetEnum.RET_PARAM_NOT_FOUND.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_NOT_FOUND);
        }
        Map params = baseParam.isNullValue("params") ? null : (Map) bizParamMap.get("params");
        if (ObjectValidUtil.isInvalid(params)) {
            _log.warn("处理支付宝支付回调失败, {}. jsonParam={}", RetEnum.RET_PARAM_INVALID.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_INVALID);
        }
        Map<String, Object> payContext = new HashMap();
        PayOrder payOrder;
        payContext.put("parameters", params);
        if(!verifyAliPayParams(payContext)) {
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_BIZ_PAY_NOTIFY_VERIFY_FAIL);
        }
        _log.info("{}验证支付通知数据及签名通过", logPrefix);
        String trade_status = params.get("trade_status").toString();		// 交易状态
        // 支付状态成功或者完成
        if (trade_status.equals(PayConstant.AlipayConstant.TRADE_STATUS_SUCCESS) ||
                trade_status.equals(PayConstant.AlipayConstant.TRADE_STATUS_FINISHED)) {
            int updatePayOrderRows;
            payOrder = (PayOrder)payContext.get("payOrder");
            byte payStatus = payOrder.getStatus(); // 0：订单生成，1：支付中，-1：支付失败，2：支付成功，3：业务处理完成，-2：订单过期
            if (payStatus != PayConstant.PAY_STATUS_SUCCESS && payStatus != PayConstant.PAY_STATUS_COMPLETE) {
                updatePayOrderRows = super.baseUpdateStatus4Success(payOrder.getPayOrderId(), StrUtil.toString(params.get("trade_no"), null));
                if (updatePayOrderRows != 1) {
                    _log.error("{}更新支付状态失败,将payOrderId={},更新payStatus={}失败", logPrefix, payOrder.getPayOrderId(), PayConstant.PAY_STATUS_SUCCESS);
                    _log.info("{}响应给支付宝结果：{}", logPrefix, PayConstant.RETURN_ALIPAY_VALUE_FAIL);
                    return RpcUtil.createBizResult(baseParam, PayConstant.RETURN_ALIPAY_VALUE_FAIL);
                }
                _log.info("{}更新支付状态成功,将payOrderId={},更新payStatus={}成功", logPrefix, payOrder.getPayOrderId(), PayConstant.PAY_STATUS_SUCCESS);
                payOrder.setStatus(PayConstant.PAY_STATUS_SUCCESS);
            }
        }else{
            // 其他状态
            _log.info("{}支付状态trade_status={},不做业务处理", logPrefix, trade_status);
            _log.info("{}响应给支付宝结果：{}", logPrefix, PayConstant.RETURN_ALIPAY_VALUE_SUCCESS);
            return RpcUtil.createBizResult(baseParam, PayConstant.RETURN_ALIPAY_VALUE_SUCCESS);
        }
        doNotify(payOrder, true);
        _log.info("====== 完成处理支付宝支付回调通知 ======");
        return RpcUtil.createBizResult(baseParam, PayConstant.RETURN_ALIPAY_VALUE_SUCCESS);
    }
*/

  /*@Override
    public Map getAliRefundReq(String jsonParam) {
        String logPrefix = "【支付宝退款查询】";
        BaseParam baseParam = JsonUtil.getObjectFromJson(jsonParam, BaseParam.class);
        Map<String, Object> bizParamMap = baseParam.getBizParamMap();
        if (ObjectValidUtil.isInvalid(bizParamMap)) {
            _log.warn("{}失败, {}. jsonParam={}", logPrefix, RetEnum.RET_PARAM_NOT_FOUND.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_NOT_FOUND);
        }
        JSONObject refundOrderObj = baseParam.isNullValue("refundOrder") ? null : JSONObject.parseObject(bizParamMap.get("refundOrder").toString());
        RefundOrder refundOrder = JSON.toJavaObject(refundOrderObj, RefundOrder.class);
        if (ObjectValidUtil.isInvalid(refundOrder)) {
            _log.warn("{}失败, {}. jsonParam={}", logPrefix, RetEnum.RET_PARAM_INVALID.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_INVALID);
        }
        String refundOrderId = refundOrder.getRefundOrderId();
        String mchId = refundOrder.getMchId();
        String channelId = refundOrder.getChannelId();
        PayChannel payChannel = baseService4PayOrder.baseSelectPayChannel(mchId, channelId);
        alipayConfig.init(payChannel.getParam());
        AlipayClient client = new DefaultAlipayClient(alipayConfig.getUrl(), alipayConfig.getApp_id(), alipayConfig.getRsa_private_key(), AlipayConfig.FORMAT, AlipayConfig.CHARSET, alipayConfig.getAlipay_public_key(), AlipayConfig.SIGNTYPE);
        AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
        AlipayTradeFastpayRefundQueryModel model = new AlipayTradeFastpayRefundQueryModel();
        model.setOutTradeNo(refundOrder.getPayOrderId());
        model.setTradeNo(refundOrder.getChannelPayOrderNo());
        model.setOutRequestNo(refundOrderId);
        request.setBizModel(model);
        Map<String, Object> map = new HashMap<>();
        map.put("refundOrderId", refundOrderId);
        try {
            AlipayTradeFastpayRefundQueryResponse response = client.execute(request);
            if(response.isSuccess()){
                map.putAll((Map) JSON.toJSON(response));
                map.put("isSuccess", true);
            }else {
                _log.info("{}返回失败", logPrefix);
                _log.info("sub_code:{},sub_msg:{}", response.getSubCode(), response.getSubMsg());
                map.put("channelErrCode", response.getSubCode());
                map.put("channelErrMsg", response.getSubMsg());
            }
        } catch (AlipayApiException e) {
            _log.error(e, "");
        }
        return RpcUtil.createBizResult(baseParam, map);
    }

 */

}
