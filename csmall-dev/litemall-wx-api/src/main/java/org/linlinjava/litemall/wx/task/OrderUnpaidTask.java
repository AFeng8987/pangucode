package org.linlinjava.litemall.wx.task;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.system.SystemConfig;
import org.linlinjava.litemall.core.task.Task;
import org.linlinjava.litemall.core.util.BeanUtil;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.domain.LitemallOrderGoods;
import org.linlinjava.litemall.db.domain.LitemallUser;
import org.linlinjava.litemall.db.service.LitemallGoodsProductService;
import org.linlinjava.litemall.db.service.LitemallOrderGoodsService;
import org.linlinjava.litemall.db.service.LitemallOrderService;
import org.linlinjava.litemall.db.service.LitemallProductFactoryService;
import org.linlinjava.litemall.db.service.LitemallStockRecordService;
import org.linlinjava.litemall.db.service.LitemallUserService;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.linlinjava.litemall.wx.service.WxOrderService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderUnpaidTask extends Task {
    private final Log logger = LogFactory.getLog(OrderUnpaidTask.class);
    private String payOrderSn = "";

    public OrderUnpaidTask(String payOrderSn, long delayInMilliseconds){
        super("OrderUnpaidTask-" + payOrderSn, delayInMilliseconds);
        this.payOrderSn = payOrderSn;
    }

    public OrderUnpaidTask(String payOrderSn){
        super("OrderUnpaidTask-" + payOrderSn, SystemConfig.getOrderUnpaid() * 60 * 1000);
        this.payOrderSn = payOrderSn;
    }

    @Override
    public void run() {
        logger.info("系统开始处理延时任务---订单超时未付款---payOrderSn：" + this.payOrderSn);

        LitemallOrderService orderService = BeanUtil.getBean(LitemallOrderService.class);
        LitemallOrderGoodsService orderGoodsService = BeanUtil.getBean(LitemallOrderGoodsService.class);
        WxOrderService wxOrderService = BeanUtil.getBean(WxOrderService.class);
        LitemallStockRecordService stockRecordService = BeanUtil.getBean(LitemallStockRecordService.class);
        LitemallUserService userService = BeanUtil.getBean(LitemallUserService.class);
        LitemallProductFactoryService productFactoryService = BeanUtil.getBean(LitemallProductFactoryService.class);

        List<LitemallOrder> orderList = orderService.findByPaySn(this.payOrderSn);
        if(CollectionUtils.isEmpty(orderList)){
            return;
        }
        LitemallUser user = userService.findById(orderList.get(0).getUserId());
        BigDecimal couponPrice=new BigDecimal(0);
        for (LitemallOrder order:orderList) {
            couponPrice=couponPrice.add(order.getCouponPrice());
            if(!OrderUtil.isCreateStatus(order)){
                return;
            }else {
                // 设置订单已取消状态
                order.setOrderStatus(OrderUtil.STATUS_AUTO_CANCEL);
                order.setEndTime(LocalDateTime.now());
                if (orderService.updateById(order) == 0) {
                    throw new RuntimeException("更新数据已失效");
                }
                // 商品货品数量增加
                String orderSn = order.getOrderSn();
                List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOsn(orderSn);
                for (LitemallOrderGoods orderGoods : orderGoodsList) {
                    Integer plantId = orderGoods.getPlantId();
                    Integer specCodeId = orderGoods.getProductId();
                    Integer goodsCodeId = orderGoods.getGoodsCodeId();
                    Short number = orderGoods.getNumber();
                    if (productFactoryService.addStockFactory(goodsCodeId, specCodeId, plantId, number) == 0) {
                        throw new RuntimeException("规格编码工厂表库存增加失败");
                    }
                    if (productFactoryService.addStockSpecode(goodsCodeId, specCodeId, number) == 0) {
                        throw new RuntimeException("规格编码货品表库存增加失败");
                    }
                    //库存记录表
                    stockRecordService.add(orderGoods.getProductId(), orderGoods.getPlantId(), Integer.valueOf(orderGoods.getNumber()), 2, "自动取消订单", "system");

                }
            }
        }
        if (couponPrice.compareTo(BigDecimal.ZERO)==1){
            //返还优惠券
            wxOrderService.releaseCoupon(this.payOrderSn,user.getId());
        }



        logger.info("系统结束处理延时任务---订单超时未付款---payOrderSn：" + this.payOrderSn);
    }
}
