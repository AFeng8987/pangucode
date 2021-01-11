package org.linlinjava.litemall.db.dao;

import org.apache.ibatis.annotations.Param;
import org.linlinjava.litemall.db.domain.LitemallAftersale;
import org.linlinjava.litemall.db.domain.LitemallAftersaleExample;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AftersaleMapper {

    List<Map<String,Object>> queryAfterSaleList( @Param("ids")List<Integer> ids,@Param("orderSn")String orderSn,@Param("payOrderSn") String payOrderSn, @Param("afterSaleSn")String afterSaleSn,
                                 @Param("refundStatus")Integer refundStatus, @Param("serviceType")Integer serviceType,@Param("payId") String payId, @Param("startTime") LocalDateTime startTime, @Param("endTime")LocalDateTime endTime);

    Map<String, Object> queryCountByStatus(@Param("orderSn")String orderSn, @Param("payOrderSn") String payOrderSn, @Param("afterSaleSn")String afterSaleSn,
                                            @Param("serviceType")Integer serviceType,@Param("payId") String payId, @Param("startTime") LocalDateTime startTime, @Param("endTime")LocalDateTime endTime);

    int updateData(LitemallAftersale record);
}