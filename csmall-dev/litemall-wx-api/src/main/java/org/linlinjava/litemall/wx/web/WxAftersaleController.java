package org.linlinjava.litemall.wx.web;

import afu.org.checkerframework.checker.oigj.qual.O;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.system.SystemConfig;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.service.*;
import org.linlinjava.litemall.db.util.AfterSaleUtil;
import org.linlinjava.litemall.db.util.AftersaleConstant;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.linlinjava.litemall.wx.service.WxAfterSaleService;
import org.linlinjava.litemall.wx.util.WxResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 售后服务
 * <p>
 * 目前只支持订单整体售后，不支持订单商品单个售后
 * <p>
 * 一个订单只能有一个售后记录
 */
@RestController
@RequestMapping("/wx/aftersale")
@Validated
public class WxAftersaleController {
    private final Log logger = LogFactory.getLog(WxAftersaleController.class);

    @Autowired
    private LitemallAftersaleService afterSaleService;
    @Autowired
    private LitemallOrderService orderService;
    @Autowired
    private LitemallOrderGoodsService orderGoodsService;

    @Autowired
    private WxAfterSaleService wxAfterSaleService;

    @Autowired
    private LitemallPlantService plantService;

    /**
     * 售后客服
     *
     * @param userId 用户ID
     * @return 售后列表
     */
    @GetMapping("customer")
    @ApiOperation("售后客服")
    public Object list(@LoginUser Integer userId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        Map<String, Object> data = new HashMap<>();
        data.put("Phone", SystemConfig.getLitemallCustomerPhone());
        data.put("Wx", SystemConfig.getLitemallCustomerWx());
        data.put("QQ", SystemConfig.getLitemallCustomerQq());
        data.put("Email", SystemConfig.getLitemallCustomerEmail());
        return ResponseUtil.ok(data);
    }

    /**
     * 售后详情
     *
     * @param orderGoodsId 订单ID
     * @return 售后详情
     */
    @GetMapping("detail")
    @ApiOperation("售后详情")
    public Object detail(@LoginUser Integer userId, @NotNull Integer orderGoodsId) {
        logger.info("用户："+userId+"获取售后详情");
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        LitemallOrderGoods orderGoods = orderGoodsService.findById(orderGoodsId);
        if (orderGoods==null){
            return ResponseUtil.badArgumentValue("参数错误");
        }
        LitemallOrder order = orderService.findBySn(orderGoods.getOrderSn());
        List<LitemallAftersale> afterSaleList = afterSaleService.queryByOgIdAndStatus(userId, orderGoods.getId(),orderGoods.getAftersaleStatus());
        if (CollectionUtils.isEmpty(afterSaleList)){
            return ResponseUtil.badArgumentValue("该商品暂无售后信息");
        }
        LitemallAftersale afterSale=afterSaleList.get(0);
        Map<String, String> address = new HashMap<>();
        if (afterSale.getStatus()>=AfterSaleUtil.STATUS_RETURN_ALLOWED
                &&afterSale.getStatus()<=AfterSaleUtil.STATUS_REFUND_FAILED&&afterSale.getServiceType().equals(2)
                &&!afterSale.getStatus().equals(AfterSaleUtil.STATUS_REJECT_RETURN)) {
            LitemallPlant plant = plantService.findById(orderGoods.getPlantId());
            address.put("receiveContacts", plant.getReceiveContacts());
            address.put("receivePhone", plant.getReceivePhone());
            address.put("receiveAddress", plant.getReceiveAddress());
            logger.info("退货地址"+userId+"获取售后详情");
        }
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("afterSale", afterSale);
        data.put("orderGoods", orderGoods);
        data.put("order", order);
        data.put("returnAddress", address);
        return ResponseUtil.ok(data);
    }

    /**
     * 申请售后
     *
     * @param userId            用户ID
     * @param litemallAftersale 用户售后信息
     * @return 操作结果
     */
    @PostMapping("submit")
    @ApiOperation("申请售后")
    public Object submit(@LoginUser Integer userId, @RequestBody LitemallAftersale litemallAftersale) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        Object error = validate(litemallAftersale);
        if (error != null) {
            return error;
        }
        return wxAfterSaleService.createAfterSale(userId, litemallAftersale);
    }


    /**
     * 取消售后
     * <p>
     * 如果管理员还没有审核，用户可以取消自己的售后申请
     *
     * @param userId    用户ID
     * @param aftersale 用户售后信息
     * @return 操作结果
     */
    @PostMapping("cancel")
    @ApiOperation("取消售后")
    public Object cancel(@LoginUser Integer userId, @RequestBody LitemallAftersale aftersale) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        Integer id = aftersale.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        return wxAfterSaleService.cancel(userId, aftersale);
    }


    /**
     * 更新售后申请
     * 如果管理员审核退货后，用户填写退货单
     *
     * @param userId    用户ID
     * @param afterSale 用户售后信息
     * @return 操作结果
     */
    @PostMapping("update")
    @ApiOperation("更新售后申请")
    public Object update(@LoginUser Integer userId, @RequestBody LitemallAftersale afterSale) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        Integer id = afterSale.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        LitemallAftersale afterSaleOne = afterSaleService.findById(userId, id);
        if (afterSaleOne == null) {
            return ResponseUtil.badArgument();
        }
        if (StringUtils.isAnyBlank(afterSale.getCourierCompany(), afterSale.getCourierNumber())) {
            return ResponseUtil.badArgumentValue("请填写退货物流信息");
        }
        afterSaleOne.setCourierCompany(afterSale.getCourierCompany());
        afterSaleOne.setCourierNumber(afterSale.getCourierNumber());
        afterSaleService.updateById(afterSaleOne);
        //更新
        return ResponseUtil.ok();
    }


    private Object validate(LitemallAftersale aftersale) {
        Integer type = aftersale.getServiceType();
        if (type == null || (type != 1 && type != 2)) {
            return ResponseUtil.badArgument();
        }
        //货物状态（1.未收到货，2已收到货）
        Integer cargoStatus = aftersale.getCargoStatus();
        if (cargoStatus == null || (cargoStatus != 1 && cargoStatus != 2)) {
            return ResponseUtil.badArgument();
        }
        //列几条出来，用户选完直接传字符串
        String reason = aftersale.getReason();
        if (StringUtils.isBlank(reason)) {
            return ResponseUtil.badArgument();
        }
        BigDecimal amount = aftersale.getAmount();
        if (amount == null) {
            return ResponseUtil.badArgument();
        }
        return null;
    }


}
