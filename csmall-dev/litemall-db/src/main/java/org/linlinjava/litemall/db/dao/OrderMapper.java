package org.linlinjava.litemall.db.dao;

import org.apache.ibatis.annotations.Param;
import org.linlinjava.litemall.db.domain.LitemallNodeRelation;
import org.linlinjava.litemall.db.domain.LitemallOrder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OrderMapper {
    int updateWithOptimisticLocker(@Param("lastUpdateTime") LocalDateTime lastUpdateTime, @Param("order") LitemallOrder order);


    List<Map<String,Object>> queryOrder(@Param("consignee")String consignee, @Param("orderSn")String orderSn,@Param("payOrderSn")String payOrderSn,@Param("userName")String userName,@Param("startTime")LocalDateTime startTime, @Param("endTime")LocalDateTime endTime, @Param("orderStatusArray")List<Short> orderStatusArray);


    Map<String, Object> queryDetailById(@Param("orderId")Integer orderId);

    List<Map<String, Object>> queryOrderDelivery(@Param("consignee")String consignee, @Param("orderSn")String orderSn, @Param("payOrderSn")String payOrderSn,@Param("plantId")Integer plantId, @Param("startTime")LocalDateTime startTime,  @Param("endTime")LocalDateTime endTime,@Param("deliveryStatus") Boolean deliveryStatus);

    List<String> queryAppList(@Param("userId")Integer userId,@Param("orderStatusArray")List<Short> orderStatus);


    int batchInsert(@Param("list") List<LitemallOrder> list);

    Map<String, Object> countOrderDelivery(@Param("consignee")String consignee, @Param("orderSn")String orderSn,@Param("payOrderSn")String payOrderSn,@Param("plantId")Integer plantId, @Param("startTime")LocalDateTime startTime,  @Param("endTime")LocalDateTime endTime);
}