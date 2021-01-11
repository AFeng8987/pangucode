package org.linlinjava.litemall.db.dao;

import org.apache.ibatis.annotations.Param;
import org.linlinjava.litemall.db.domain.LitemallAllianceSale;
import org.linlinjava.litemall.db.domain.LitemallAllianceSaleExample;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AllianceSaleMapper {

    List<Map<String,Object>> queryByAllianceUserId(@Param("allianceUserId")Integer allianceUserId, @Param("startTime")LocalDateTime startTime);

    Map<String, Object> totalAllianceData(@Param("allianceUserId")Integer allianceUserId, @Param("startTime")LocalDateTime startTime);

    List<Map<String, Object>> exportList(@Param("ids")List<Integer> ids,@Param("orderSn")String orderSn,@Param("payOrderSn")String payOrderSn,@Param("allianceName") String allianceName, @Param("userName") String userName, @Param("orderAddStartTime")LocalDateTime orderAddStartTime,@Param("orderAddEndTime") LocalDateTime orderAddEndTime,@Param("orderCloseStartTime") LocalDateTime orderCloseStartTime,@Param("orderCloseEndTime") LocalDateTime orderCloseEndTime);

}