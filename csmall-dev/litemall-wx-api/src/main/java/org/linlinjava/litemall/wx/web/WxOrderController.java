package org.linlinjava.litemall.wx.web;

import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.linlinjava.litemall.wx.service.WxOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/wx/order")
@Validated
public class WxOrderController {

    private final Log logger = LogFactory.getLog(WxOrderController.class);

    @Autowired
    private WxOrderService wxOrderService;

    /**
     * 订单列表
     *
     * @param userId   用户ID
     * @param showType 显示类型，如果是0则是全部订单
     * @param page     分页页数
     * @param limit     分页大小

     * @return 订单列表
     */
    @GetMapping("list")
    public Object list(@LoginUser Integer userId,
                       @RequestParam(defaultValue = "0") Integer showType,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit) {
        return wxOrderService.list(userId, showType, page, limit);
    }

    /**
     * 订单详情
     *
     * @param userId  用户ID
     * @param payOrderSn 订单ID
     * @return 订单详情
     */
    @GetMapping("detail")
    public Object detail(@LoginUser Integer userId, String payOrderSn,Integer orderId) {
        if (StringUtils.isBlank(payOrderSn)&&null==orderId){
            return ResponseUtil.badArgumentValue("参数必传");
        }
        if (StringUtils.isNotBlank(payOrderSn)&&null!=orderId){
            return ResponseUtil.badArgumentValue("参数二选一");
        }
        if (null==orderId){
            return wxOrderService.detailByPayOrderSn(userId, payOrderSn);
        }
        return wxOrderService.detailByOrderId(userId, orderId);
    }

    /**
     * 提交订单
     *
     * @param userId 用户ID
     * @param body   订单信息，{ cartId：xxx, addressId: xxx, couponId: xxx, message: xxx, grouponRulesId: xxx,  grouponLinkId: xxx}
     * @return 提交订单操作结果
     */
    @PostMapping("submit")
    public Object submit(@LoginUser Integer userId, @RequestBody String body) {
        logger.info("用户id:"+userId+"提交订单");
        return wxOrderService.submit(userId, body);
    }

    /**
     * 取消订单
     *
     * @param userId 用户ID
     * @param body   订单信息，{ orderId：xxx }
     * @return 取消订单操作结果
     */
    @PostMapping("cancel")
    public Object cancel(@LoginUser Integer userId, @RequestBody String body) {
        return wxOrderService.cancel(userId, body);
    }

    /**
     * 付款订单的预支付会话标识
     *
     * @param userId 用户ID
     * @param body   订单信息，{ orderId：xxx }
     * @return 支付订单ID
     */
    @PostMapping("prepay")
    public Object prepay(@LoginUser Integer userId, @RequestBody String body, HttpServletRequest request) {
        return wxOrderService.prepay(userId, body, request);
    }

    /**
     * APP支付宝支付
     *
     * @param userId 用户ID
     * @param body   订单信息，{ orderId：xxx }
     * @return 支付订单ID
     */
    @PostMapping("alipay")
    public Object alipay(@LoginUser Integer userId, @RequestBody String body, HttpServletRequest request) {
        logger.info("用户id:"+userId+"请求APP支付宝支付");
        return wxOrderService.alipay(userId, body, request);
    }

    /**
     * 微信H5支付
     * @param userId
     * @param body
     * @param request
     * @return

    @PostMapping("h5pay")
    public Object h5pay(@LoginUser Integer userId, @RequestBody String body, HttpServletRequest request) {
        return wxOrderService.h5pay(userId, body, request);
    }
     */



    /**
     * 确认收货
     *
     * @param userId 用户ID
     * @param body   订单信息，{ orderId：xxx }
     * @return 订单操作结果
     */
    @PostMapping("confirm")
    public Object confirm(@LoginUser Integer userId, @RequestBody String body) {
        return wxOrderService.confirm(userId, body);
    }



    /**
     * 查物流轨迹
     *
     * @param orderId 订单id
     * @return 订单操作结果
     */
    @GetMapping("logistics")
    public Object logistics(@LoginUser Integer userId, @NotNull Integer orderId) {
        return wxOrderService.queryExpressInfo(userId,orderId);
    }



    /**
     * 提醒发货
     *
     * @param userId 用户ID
     * @param body   订单信息，{ orderId：xxx }
     * @return 订单操作结果
     */
    @PostMapping("reminderShipment")
    @ApiOperation(value = "提醒发货", notes = "提醒发货")
    public Object reminderShipment(@LoginUser Integer userId, @RequestBody String body) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        String payOrderSn = JacksonUtil.parseString(body, "payOrderSn");
        if (StringUtils.isBlank(payOrderSn)) {
            return ResponseUtil.badArgument();
        }
        return wxOrderService.reminderShipment(userId, payOrderSn);
    }



}