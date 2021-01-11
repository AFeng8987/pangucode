package org.linlinjava.litemall.wx.web;

import com.alipay.api.internal.util.AlipaySignature;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.pay.AlipayConfig;
import org.linlinjava.litemall.wx.service.WxOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 用户收货地址服务
 */
@RestController
@RequestMapping("/wx/notify")
@Validated
public class NotifyController {
    private final Log logger = LogFactory.getLog(NotifyController.class);


    @Autowired
    private AlipayConfig alipayConfig;

    @Autowired
    private WxOrderService wxOrderService;


    /**
     * 接收支付中心通知
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @PostMapping("/alipay")
    @ApiOperation(value = "支付回调通知", notes = "支付回调通知")
    public void payNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String logPrefix = "【处理支付宝支付回调】";
        logger.info("====== 开始处理支付宝支付回调通知 ======");
        String resStr = "fail";
        //获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }
        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
        //商户订单号
        String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
        //支付宝交易号
        String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");

        //交易状态
        String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");

        //交易金额
        String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");

        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
        //计算得出通知验证结果
        //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
        boolean verify_result = AlipaySignature.rsaCheckV1(params, alipayConfig.getRsaPublicKey(), AlipayConfig.CHARSET, "RSA2");

        if (verify_result) {//验证成功

            //////////////////////////////////////////////////////////////////////////////////////////
            //请在这里加上商户的业务逻辑程序代码
            logger.info(logPrefix+"验证支付通知数据及签名通过");
            //——请根据您的业务逻辑来编写程序（以下代码仅作参考）——

            if (trade_status.equals("TRADE_FINISHED")||trade_status.equals("TRADE_SUCCESS")) {
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                //如果有做过处理，不执行商户的业务程序
                try{
                    wxOrderService.alipayNotify(out_trade_no,trade_no,total_amount);
                    logger.info(logPrefix+"=======订单payOrderSn="+out_trade_no+"====处理业务成功=========");
                }catch (RuntimeException e){
                    logger.info(e.getMessage());
                    outResult(response, resStr);
                    return;
                }
                //注意：
                //如果签约的是可退款协议，退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
                //如果没有签约可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
            } else {
                logger.info(logPrefix+"回调参数：｛"+requestParams.toString()+"｝");
                logger.info(logPrefix+"支付状态trade_status="+trade_status+",不做业务处理");
            }
            //——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
            resStr="success";
            outResult(response, resStr);//请不要修改或删除

            //////////////////////////////////////////////////////////////////////////////////////////
        } else {//验证失败
            logger.info(logPrefix+"验签失败");
            outResult(response, resStr);
        }
    }

    @PostMapping("/wxpay")
    @ApiOperation(value = "WX支付回调通知", notes = "WX支付回调通知")
    public Object wxPayNotifyRes(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("====== 开始接收微信支付回调通知 ======");
        //String notifyRes = doWxPayRes(request);
        //logger.info("响应给微信:{}", notifyRes);
        logger.info("====== 完成接收微信支付回调通知 ======");
        return wxOrderService.payNotify(request, response);
        //return "notifyRes";
    }


    /**
     * 阿里响应回调通知
     * @param response
     * @param content
     */
    void outResult(HttpServletResponse response, String content) {
        response.setContentType("text/html");
        PrintWriter pw;
        try {
            pw = response.getWriter();
            pw.print(content);
            logger.error("response pay complete.");
        } catch (IOException e) {
            logger.error(e);
            logger.error("response pay write exception.");
        }
    }
}
