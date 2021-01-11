package org.linlinjava.litemall.admin.job;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.system.SystemConfig;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.service.*;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 检测订单状态
 */
@Component
public class OrderJob {
    private final Log logger = LogFactory.getLog(OrderJob.class);

    @Autowired
    private LitemallOrderGoodsService orderGoodsService;
    @Autowired
    private LitemallOrderService orderService;

    @Autowired
    private LitemallAllianceSaleService allianceSaleService;


    /**
     * 自动确认订单
     * <p>
     * 定时检查订单未确认情况，如果超时 LITEMALL_ORDER_UNCONFIRM 天则自动确认订单
     * 定时时间是每半个小时。
     * <p>
     * TODO
     * 注意，因为是相隔一天检查，因此导致订单真正超时时间是 [LITEMALL_ORDER_UNCONFIRM, 1 + LITEMALL_ORDER_UNCONFIRM]
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    public void checkOrderUnconfirm() {
        logger.info("系统开启定时任务检查订单是否已经超期自动确认收货");

        List<LitemallOrder> orderList = orderService.queryUnconfirm(SystemConfig.getOrderUnconfirm());
        for (LitemallOrder order : orderList) {

            // 设置订单自动收货状态
            order.setOrderStatus(OrderUtil.STATUS_AUTO_CONFIRM);
            //设置订单关闭时间，当前时间+订单关闭时间配置
            order.setEndTime(LocalDateTime.now().plusDays(SystemConfig.getLitemallOrderClose()));
//            order.setConfirmTime(LocalDateTime.now());
            if (orderService.updateById(order) == 0) {
                logger.info("订单 ID=" + order.getId() + " 数据已经更新，放弃自动确认收货");
            } else {
                logger.info("订单 ID=" + order.getId() + " 已经超期自动确认收货");
            }
        }

        logger.info("系统结束定时任务检查订单是否已经超期自动确认收货");
    }

    /**
     * 自动关闭订单
     * <p>
     * 定时检查订单关闭情况，如果超时 LITEMALL_ORDER_CLOSE 天则自动关闭订单
     * 定时时间是半个小时一次。
     * <p>
     * TODO
     * 注意，因为是相隔一天检查，因此导致订单真正超时时间是 [LITEMALL_ORDER_CLOSE, 1 + LITEMALL_ORDER_CLOSE]
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    public void checkOrderClose() {
        logger.info("系统开启定时任务检查订单是否已经超期自动关闭");

        List<LitemallOrder> orderList = orderService.queryClose();
        for (LitemallOrder order : orderList) {

            // 设置订单已关闭状态
            order.setOrderStatus(OrderUtil.STATUS_CLOSE);
            //order.setEndTime(LocalDateTime.now());
            if (orderService.updateById(order) == 0) {
                logger.info("订单 ID=" + order.getId() + " 数据已经更新，放弃自动确认收货");
            } else {
                logger.info("订单 ID=" + order.getId() + " 已经超期自动确认收货");
            }

        }

        logger.info("系统结束定时任务检查订单是否已经超期自动关闭");
    }

    /**
     * 添加销售数据
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void addAllianceSale() {
        logger.info("系统开启定时任务添加销售数据");
        List<LitemallOrder> orderList = orderService.queryCloseOrder();
        for (LitemallOrder order : orderList) {
            //查还没分润的订单商品
            List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryAllianceSaleByOrderSn(order.getOrderSn());
            if (CollectionUtils.isEmpty(orderGoodsList)) {
                order.setIsBenefit(true);
                orderService.updateById(order);
                continue;
            }
            for (LitemallOrderGoods orderGoods : orderGoodsList) {
                allianceSaleService.addAllianceSale(order, orderGoods);
            }
        }

        logger.info("系统结束定时任务添加销售数据");
    }


    /**
     * 可评价订单商品超期
     * <p>
     * 定时检查订单商品评价情况，如果确认商品超时 LITEMALL_ORDER_COMMENT 天则取消可评价状态
     * 定时时间是每天凌晨4点。
     * <p>
     * TODO
     * 注意，因为是相隔一天检查，因此导致订单真正超时时间是 [LITEMALL_ORDER_COMMENT, 1 + LITEMALL_ORDER_COMMENT]
     */
/*    @Scheduled(cron = "0 0 4 * * ?")
    public void checkOrderComment() {
        logger.info("系统开启任务检查订单是否已经超期未评价");

        List<LitemallOrder> orderList = orderService.queryComment(SystemConfig.getOrderComment());
        for (LitemallOrder order : orderList) {

            orderService.updateWithOptimisticLocker(order);

            List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(order.getId());
            for (LitemallOrderGoods orderGoods : orderGoodsList) {
                orderGoods.setComment(-1);
                orderGoodsService.updateById(orderGoods);
            }
        }

        logger.info("系统结束任务检查订单是否已经超期未评价");
    }*/

}
