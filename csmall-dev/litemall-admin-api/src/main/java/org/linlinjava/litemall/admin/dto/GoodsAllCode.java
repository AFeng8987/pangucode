package org.linlinjava.litemall.admin.dto;

import org.linlinjava.litemall.db.domain.*;

import java.util.List;
import java.util.Map;

public class GoodsAllCode {

    private Integer goodsCodeId;
    private LitemallGoodsSpec[] specs;


    public Integer getGoodsCodeId() {
        return goodsCodeId;
    }

    public void setGoodsCodeId(Integer goodsCodeId) {
        this.goodsCodeId = goodsCodeId;
    }




    public LitemallGoodsSpec[] getSpecs() {
        return specs;
    }

    public void setSpecs(LitemallGoodsSpec[] specs) {
        this.specs = specs;
    }

}
