package org.linlinjava.litemall.db.dao;

import org.apache.ibatis.annotations.Param;
import org.linlinjava.litemall.db.domain.LitemallSpecodeProduct;
import org.linlinjava.litemall.db.domain.LitemallSpecodeProductExample;

import java.util.List;
import java.util.Map;

public interface SpecodeProductMapper {

        List<Map<String,Object>> queryByGCodeNameSpecsCode(@Param("goodsCode")String goodsCode,@Param("codeName") String codeName,@Param("specCode") String specCode);

        int updateBatch(@Param("list") List<LitemallSpecodeProduct> list);

}