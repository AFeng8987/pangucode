package org.linlinjava.litemall.db.dao;


import org.apache.ibatis.annotations.Param;
import org.linlinjava.litemall.db.domain.LitemallProductFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ProductFactoryMapper {

    List<Map<String, Object>> queryBySpecsCodeId(@Param("specsCodeId") Integer specsCodeId);

    List<Map<String, Object>> reamStockList(@Param("goodsCode")String goodsCode,@Param("codeName") String codeName, @Param("specsCode")String specsCode,@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime")LocalDateTime endDateTime);

    List<Map<String, Object>> warnStockList(@Param("goodsCode")String goodsCode,@Param("codeName") String codeName, @Param("specsCode")String specsCode,@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime")LocalDateTime endDateTime);
}