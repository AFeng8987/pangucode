package org.linlinjava.litemall.db.dao;

import org.apache.ibatis.annotations.Param;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.domain.LitemallOrderGoods;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OrderGoodsMapper {

    List<Map<String,Object>> queryOrderGoodsByOrderSn(@Param("orderSn") String orderSn);

    int batchUpdate(@Param("list")List<LitemallOrderGoods> list);

    List<LitemallOrderGoods> queryByPaySn(String payOrderSn);

    /**
     * 根据paySn处理销量增加
     * @param payOrderSn
     * @return
     */
    List<LitemallOrderGoods> queryByPaySnAddSales(String payOrderSn);

    List<LitemallOrderGoods> queryAllianceSaleByOrderSn(String orderSn);
}