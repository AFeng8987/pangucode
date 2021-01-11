package org.linlinjava.litemall.db.dao;

import org.apache.ibatis.annotations.Param;
import org.linlinjava.litemall.db.domain.LitemallGoodsActivity;

import java.util.List;

public interface GoodsActivityMapper {

    List<String> list(@Param("activityId") String activityId, @Param("goodsId") String goodsId, @Param("goodsName") String goodsName);

    void del(@Param("activityId") Integer activityId, @Param("goodsId") Integer goodsId);

    void delList(@Param("activityId") Integer activityId, @Param("list") List<Integer> goodsId);

    void addList(@Param("list")List<LitemallGoodsActivity> list);
}
