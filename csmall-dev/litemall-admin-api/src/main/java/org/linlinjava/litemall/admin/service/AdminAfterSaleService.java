package org.linlinjava.litemall.admin.service;

import com.alipay.api.response.AlipayTradeRefundResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.linlinjava.litemall.core.pay.AlipayService;
import org.linlinjava.litemall.core.pay.WxPayAndRefundService;
import org.linlinjava.litemall.core.util.PayType;
import org.linlinjava.litemall.core.util.PoiExcelExport;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.dao.AftersaleMapper;
import org.linlinjava.litemall.db.domain.LitemallAdmin;
import org.linlinjava.litemall.db.domain.LitemallAftersale;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.domain.LitemallOrderGoods;
import org.linlinjava.litemall.db.service.LitemallAftersaleService;
import org.linlinjava.litemall.db.service.LitemallAllianceSaleService;
import org.linlinjava.litemall.db.service.LitemallOrderGoodsService;
import org.linlinjava.litemall.db.service.LitemallOrderService;
import org.linlinjava.litemall.db.util.AfterSaleUtil;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
public class AdminAfterSaleService {
    private final Log logger = LogFactory.getLog(AdminAfterSaleService.class);

    @Resource
    private AftersaleMapper aftersaleMapper;

    @Autowired
    private LitemallOrderGoodsService orderGoodsService;

    @Autowired
    private LitemallOrderService orderService;

    @Autowired
    private LitemallAftersaleService afterSaleService;

    @Autowired
    private LitemallAllianceSaleService allianceSaleService;

    @Autowired
    private AlipayService alipayService;


    @Autowired
    private WxPayAndRefundService wxPayService;


    /**
     * 退货审批
     *
     * @param afterSale
     */
    @Transactional(rollbackFor = Exception.class)
    public void returnApproval(LitemallAftersale afterSale) {
        //更新售后审核数据
        Subject currentUser = SecurityUtils.getSubject();
        LitemallAdmin admin = (LitemallAdmin) currentUser.getPrincipal();
        afterSale.setLastOperator(admin.getId());
        afterSale.setHandleTime(LocalDateTime.now());
        afterSaleService.updateById(afterSale);
        // order表不需要更新，orderGoods表退款状态需要同步更新
        LitemallOrder order = orderService.findById(afterSale.getOrderId());
        LitemallOrderGoods orderGoods = orderGoodsService.findById(afterSale.getOrderGoodsId());
        //驳回时需要更新订单商品表商品退款的数据，
        if (AfterSaleUtil.STATUS_REJECT_RETURN == afterSale.getStatus()) {
            orderGoods.setIsRefund(false);
            //如果驳回，该订单已关闭，需要分润把单个商品的分润添加到分润表
            if (order.getOrderStatus() == OrderUtil.STATUS_CLOSE&&order.getIsBenefit()) {
                allianceSaleService.addAllianceSale(order, orderGoods);
            }
        }
        orderGoods.setAftersaleStatus(afterSale.getStatus());
        orderGoodsService.updateById(orderGoods);

    }

    /**
     * 退款审核
     *
     * @param afterSale
     */
    @Transactional(rollbackFor = Exception.class)
    public void refundApproval(LitemallAftersale afterSale) {

        //更新售后审核数据
        Subject currentUser = SecurityUtils.getSubject();
        LitemallAdmin admin = (LitemallAdmin) currentUser.getPrincipal();
        afterSale.setLastOperator(admin.getId());
        afterSale.setHandleTime(LocalDateTime.now());
        afterSaleService.updateById(afterSale);
        // order表不需要更新，orderGoods表退款状态需要同步更新
        LitemallOrder order = orderService.findById(afterSale.getOrderId());
        LitemallOrderGoods orderGoods = orderGoodsService.findById(afterSale.getOrderGoodsId());
        orderGoods.setAftersaleStatus(afterSale.getStatus());
        //驳回时需要更新订单商品表商品退款的数据，
        if (AfterSaleUtil.STATUS_REFUSE_REFUND == afterSale.getStatus()) {
            orderGoods.setIsRefund(false);
            //如果驳回，该订单已关闭，需要分润把单个商品的分润添加到分润表
            if (order.getOrderStatus() == OrderUtil.STATUS_CLOSE&&order.getIsBenefit()) {
                allianceSaleService.addAllianceSale(order, orderGoods);
            }
        } else {

            //退款审批通过时，反还库存，调支付通道的退款功能
            //当前订单为关闭，不用处理订单状态，只需要退还库存，调支付通道的退款功能
            if (order.getOrderStatus() != OrderUtil.STATUS_CLOSE) {
                //订单未关闭，需要判断当前售后是不是最后一笔在审核中的售后，是那么就需要把订单关闭
                List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOsnIsNotEqualOGId(order.getOrderSn(), afterSale.getOrderGoodsId());
                if (CollectionUtils.isEmpty(orderGoodsList)) {
                    //等于空说明除了这个售后，没有其他商品了，直接扭转订单状态
                    order.setOrderStatus(OrderUtil.STATUS_CLOSE);
                    order.setEndTime(LocalDateTime.now());
                    order.setIsBenefit(true);
                    orderService.updateById(order);
                } else {
                    for (LitemallOrderGoods og : orderGoodsList) {
                        int i = 0;
                        //其他订单 是否退款勒
                        if (og.getIsRefund()) {
                            //有申请退款，1--3-5-6-11-12 如果等于1-11-12的说明还在退款中，不需要扭转状态，直接跳出for循环
                            if (og.getAftersaleStatus() == AfterSaleUtil.STATUS_RETURN_ALLOWED ||
                                    og.getAftersaleStatus() == AfterSaleUtil.STATUS_GOODS_NOT_RECEIVED ||
                                    og.getAftersaleStatus() == AfterSaleUtil.STATUS_GOODS_RECEIVED) {
                                break;
                            } else {
                                //有申请退款，3-5-6 说明其他订单都通过审核了
                                i++;
                                //所有3-5-6退款等于除当前订单总数
                                if (i == orderGoodsList.size()) {
                                    order.setOrderStatus(OrderUtil.STATUS_CLOSE);
                                    order.setIsBenefit(true);
                                    order.setEndTime(LocalDateTime.now());
                                    orderService.updateById(order);
                                }
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
            //掉退款通道的·接口
         refundPay(order, afterSale, orderGoods);
        }
        orderGoodsService.updateById(orderGoods);
    }

    /**
     * 退款订单数据导出
     */
    public void listExport(List<Integer> ids, String orderSn, String payOrderSn,String afterSaleSn, Integer refundStatus, Integer serviceType, String payId, LocalDate start, LocalDate end, HttpServletResponse response) {
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (start != null) {
            startTime = start.atStartOfDay();
        }
        if (end != null) {
            endTime = end.atTime(LocalTime.MAX);
        }
        List<Map<String, Object>> list = aftersaleMapper.queryAfterSaleList(ids, orderSn, payOrderSn,afterSaleSn, 3, serviceType, payId, startTime, endTime);
        String sheetName = "退款导出表";
        String tabelName = "AfterSaleData";
        String titleColumn[] = {"userName", "orderSn", "afterSaleSn", "orderStatusName", "payType", "payId", "statusName", "serviceTypeName", "amount", "addTime", "plantName", "returnPayId"};
        String titleName[] = {"用户账号", "订单号", "退款单号", "订单状态", "支付方式", "支付单号", "退款状态", "退款类型", "退款金额(元)", "退款申请时间", "供货工厂", "上游退款单号"};
        PoiExcelExport.exportByMap(tabelName, sheetName, titleColumn, titleName, list, response);
    }


    @Transactional
    public Object updateStatus(String afterSaleSn) {
        LitemallAftersale afterSale = afterSaleService.selectOneByAfterSaleSn(afterSaleSn);
        if (AfterSaleUtil.STATUS_REFUND_FAILED != afterSale.getStatus()) {
            return ResponseUtil.fail(-1, "非退款失败状态不可修改");
        }
        afterSale.setStatus(AfterSaleUtil.STATUS_REFUND_SUCCESSFUL);
        afterSaleService.updateById(afterSale);
        LitemallOrderGoods orderGoods = orderGoodsService.findById(afterSale.getOrderGoodsId());
        orderGoods.setAftersaleStatus(afterSale.getStatus());
        orderGoodsService.updateById(orderGoods);
        return ResponseUtil.ok(afterSaleSn);
    }

    public Object update(String afterSaleSn) {
        LitemallAftersale afterSale = afterSaleService.selectOneByAfterSaleSn(afterSaleSn);
        if (!afterSale.getServiceType().equals(2)) {
            return ResponseUtil.fail(-1, "仅支持退货退款的确认收到退货");
        }
        if (AfterSaleUtil.STATUS_RETURN_ALLOWED != afterSale.getStatus()) {
            return ResponseUtil.fail(-1, "只能在同意退货后，确认收到退货");
        }
        afterSale.setConfirmReturn(true);
        afterSaleService.updateById(afterSale);
        return ResponseUtil.ok(afterSaleSn);
    }

    public void refundPay(LitemallOrder order, LitemallAftersale afterSale, LitemallOrderGoods orderGoods)   {
        if (StringUtils.equalsIgnoreCase(PayType.ALIPAY_MOBILE, order.getPayType())) {
            //等于支付宝退款就调支付宝退款
            AlipayTradeRefundResponse response = alipayService.doAliRefundReq(afterSale.getAftersaleSn(), order.getPayId(), order.getPayOrderSn(), afterSale.getAmount());
            if (response.isSuccess()) {
                logger.info("调用成功");
                afterSale.setStatus(AfterSaleUtil.STATUS_REFUND_FAILED);
                if ("10000".equals(response.getCode())) {
                    if ("Y".equals(response.getFundChange())
                            && afterSale.getAmount().compareTo(new BigDecimal(response.getRefundFee())) == 0 &&
                            order.getPayOrderSn().equals(response.getOutTradeNo())) {
                        afterSale.setStatus(AfterSaleUtil.STATUS_REFUND_SUCCESSFUL);
                    }
                }
                afterSaleService.updateById(afterSale);
                orderGoods.setAftersaleStatus(afterSale.getStatus());
                orderGoodsService.updateById(orderGoods);
            } else {
                throw new RuntimeException("支付宝调用失败,"+response.getMsg());
            }
        }
        if (StringUtils.equalsIgnoreCase(PayType.WX_APP, order.getPayType())) {
            List<LitemallOrder> orderList=orderService.findByPaySn(order.getPayOrderSn(),order.getUserId());
            BigDecimal totalAmount=new BigDecimal(0);
            for (LitemallOrder o : orderList) {
                totalAmount = totalAmount.add(o.getActualPrice());
            }
            int totalFee=totalAmount.multiply(new BigDecimal(100)).intValue();
            //等于微信支付，退款就调微信退款
            Map<String, Object> map=wxPayService.refund(order.getPayId(),afterSale,totalFee);
           if (Boolean.valueOf(map.get("isSuccess").toString())){
               Map<String, Object> reqMap=wxPayService.getWxRefundReq(map.get("refundId").toString(),afterSale.getAftersaleSn());
               afterSale.setStatus(AfterSaleUtil.STATUS_REFUND_FAILED);
               if (Boolean.valueOf(reqMap.get("isSuccess").toString())){
                   afterSale.setStatus(AfterSaleUtil.STATUS_REFUND_SUCCESSFUL);
                   afterSale.setRefundId(map.get("refundId").toString());
               }
               afterSaleService.updateById(afterSale);
               orderGoods.setAftersaleStatus(afterSale.getStatus());
               orderGoodsService.updateById(orderGoods);
           }else {
                String msg= map.get("channelErrMsg").toString();
                throw new RuntimeException("微信退款调用失败,"+msg);
            }
        }

    }
}
