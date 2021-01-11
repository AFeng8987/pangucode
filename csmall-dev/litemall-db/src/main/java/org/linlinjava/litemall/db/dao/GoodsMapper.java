package org.linlinjava.litemall.db.dao;

import org.apache.ibatis.annotations.Param;
import org.linlinjava.litemall.db.domain.LitemallGoods;
import org.linlinjava.litemall.db.domain.LitemallGoodsExample;
import org.linlinjava.litemall.db.dto.LitemallGoodsDto;

import java.util.List;
import java.util.Map;

public interface GoodsMapper {
    

    List<LitemallGoodsDto> activityGoods(@Param("activityId") Integer activityId, @Param("goodsCode") String goodsCode, @Param("goodsName") String goodsName);

    List<LitemallGoodsDto> activityQueryGoods(@Param("activityId") Integer activityId, @Param("goodsCode") String goodsCode, @Param("goodsName") String goodsName);

    List<LitemallGoodsDto> activityAPPGoods(@Param("activityId") Integer activityId, @Param("goodsId") Integer goodsId, @Param("goodsName") String goodsName);

    List<Map<String,Object>> queryGoodsList(@Param("goodsId")Integer goodsId,@Param("goodsCode") String goodsCode, @Param("categoryName") String categoryName, @Param("goodsName")String goodsName,@Param("isHome") Boolean isHome,@Param("isOnSale")Boolean isOnSale);

    List<Map<String, Object>> queryHotGoodsList(@Param("goodsCode")String goodsCode,@Param("goodsName") String goodsName);
}
