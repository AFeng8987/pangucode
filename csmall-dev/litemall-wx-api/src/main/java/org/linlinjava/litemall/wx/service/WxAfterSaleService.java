package org.linlinjava.litemall.wx.service;

import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.LitemallAftersale;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.domain.LitemallOrderGoods;
import org.linlinjava.litemall.db.service.LitemallAftersaleService;
import org.linlinjava.litemall.db.service.LitemallOrderGoodsService;
import org.linlinjava.litemall.db.service.LitemallOrderService;
import org.linlinjava.litemall.db.util.AfterSaleUtil;
import org.linlinjava.litemall.db.util.AftersaleConstant;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.linlinjava.litemall.wx.util.WxResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

@Service
public class WxAfterSaleService {

    @Autowired
    private LitemallAftersaleService afterSaleService;

    @Autowired
    private LitemallOrderGoodsService orderGoodsService;

    @Autowired
    private LitemallOrderService orderService;


    @Transactional
    public Object createAfterSale(Integer userId, LitemallAftersale afterSale) {
        LitemallAftersale newAfterSale = new LitemallAftersale();
        // 进一步验证
        Integer orderGoodsId = afterSale.getOrderGoodsId();
        if (orderGoodsId == null) {
            return ResponseUtil.badArgument();
        }
        LitemallOrderGoods orderGoods = orderGoodsService.findById(orderGoodsId);
        LitemallOrder order = orderService.findBySn(orderGoods.getOrderSn());
        if (order == null) {
            return ResponseUtil.badArgumentValue();
        }
        if (orderGoods == null) {
            return ResponseUtil.badArgumentValue();
        }
        if (!order.getUserId().equals(userId)) {
            return ResponseUtil.badArgumentValue();
        }
        // 订单必须付款后订单关闭前申请售后流程。
        if (!OrderUtil.isRefundStatus(order) || OrderUtil.isClose(order)) {
            return ResponseUtil.fail(WxResponseCode.AFTERSALE_UNALLOWED, "不能申请售后");
        }
        BigDecimal amount = orderGoods.getPrice().multiply(new BigDecimal(orderGoods.getNumber())).subtract(orderGoods.getCouponPrice());

        //该字段需要在用户取消售后及管理员拒绝后及时扭转状态
        if (orderGoods.getIsRefund()) {
            return ResponseUtil.fail(WxResponseCode.AFTERSALE_INVALID_AMOUNT, "已申请售后");
        }

        //根据退款申请类型分开判断业务参数
        if (afterSale.getServiceType().intValue() == 1) {
            //1.仅退款,    售后状态直接到退款处审核
            if (afterSale.getCargoStatus() == 1) {
                if (afterSale.getAmount().compareTo(amount) != 0) {
                    return ResponseUtil.fail(WxResponseCode.AFTERSALE_INVALID_AMOUNT, "仅退款未收到货必须和商品总价相等");
                }
            } else if (!(afterSale.getAmount().compareTo(amount) < 1)) {
                return ResponseUtil.fail(WxResponseCode.AFTERSALE_INVALID_AMOUNT, "仅退款已收到货退款金额不能大于商品总价");
            }
            newAfterSale.setStatus(AfterSaleUtil.STATUS_RETURN_ALLOWED);
        } else {
            //2.退货退款
            if (!(afterSale.getAmount().compareTo(amount) < 1)) {
                return ResponseUtil.fail(WxResponseCode.AFTERSALE_INVALID_AMOUNT, "退款退货的售后价格不能大于商品总价");
            }
            //根据订单状态，填充售后状态
            switch (order.getOrderStatus().shortValue()) {
                case OrderUtil.STATUS_PAY:
                    //如果还没发货，直接到审核退款处
                    newAfterSale.setStatus(AfterSaleUtil.STATUS_RETURN_ALLOWED);
                    break;
                case OrderUtil.STATUS_SHIP:
                    //已发货，售后状态未确认收货申请，该状态需要把货退回
                    newAfterSale.setStatus(AfterSaleUtil.STATUS_GOODS_NOT_RECEIVED);
                    break;
                default:
                    //已收货，售后状态确认收货申请,该状态需要把货退回
                    newAfterSale.setStatus(AfterSaleUtil.STATUS_GOODS_RECEIVED);
                    break;
            }
        }
        newAfterSale.setUserId(userId);
        newAfterSale.setAftersaleSn(afterSaleService.generateAfterSaleSn(userId));
        newAfterSale.setOrderId(order.getId());
        newAfterSale.setPlantId(orderGoods.getPlantId());
        newAfterSale.setOrderGoodsId(orderGoods.getId());

        newAfterSale.setServiceType(afterSale.getServiceType());
        newAfterSale.setCargoStatus(afterSale.getCargoStatus());
        newAfterSale.setReason(afterSale.getReason());
        newAfterSale.setAmount(afterSale.getAmount());
        newAfterSale.setPictures(afterSale.getPictures());
        newAfterSale.setComment(afterSale.getComment());
        newAfterSale.setPayId(order.getPayId());

        afterSaleService.add(newAfterSale);
        //售后数据添加后，还需要更新订单商品表的状态
        orderGoods.setAftersaleStatus(newAfterSale.getStatus());
        orderGoods.setIsRefund(true);
        orderGoodsService.updateById(orderGoods);
        return ResponseUtil.ok();
    }

    public Object cancel(Integer userId, LitemallAftersale afterSale) {
        LitemallAftersale afterSaleOne = afterSaleService.findById(userId, afterSale.getId());
        if (afterSaleOne == null) {
            return ResponseUtil.badArgument();
        }
        Integer afterStatus = afterSaleOne.getStatus();
        //状态3-7 同意退款后，不允许取消，已取消的也不可以再取消了
        if (afterStatus >= AfterSaleUtil.STATUS_REJECT_RETURN && afterStatus <= AfterSaleUtil.STATUS_CANCEL) {
            return ResponseUtil.fail(WxResponseCode.AFTERSALE_INVALID_STATUS, "不能取消售后");
        }
        LitemallOrderGoods orderGoods = orderGoodsService.findById(afterSaleOne.getOrderGoodsId());
        LitemallOrder order = orderService.findBySn(orderGoods.getOrderSn());
        if (!order.getUserId().equals(userId)) {
            return ResponseUtil.badArgumentValue();
        }
        // 售后表订单状态修改为已取消
        afterSale.setStatus(AfterSaleUtil.STATUS_CANCEL);
        if (afterSaleService.updateById(afterSale) == 0)
            return ResponseUtil.updatedDateExpired();

        // 订单商品中数据修改为未退款,售后状态为未申请
        orderGoods.setIsRefund(false);
        orderGoods.setAftersaleStatus(AfterSaleUtil.STATUS_CANCEL);
        orderGoodsService.updateById(orderGoods);
        return ResponseUtil.ok();
    }

}
