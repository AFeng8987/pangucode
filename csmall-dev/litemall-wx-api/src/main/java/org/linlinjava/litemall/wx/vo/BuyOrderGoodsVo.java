package org.linlinjava.litemall.wx.vo;

import java.util.List;

public class BuyOrderGoodsVo {

    private Integer goodsId;
    private Integer specCodeId;
    private Integer number;

    public List<Integer> getCartIds() {
        return cartIds;
    }

    public void setCartIds(List<Integer> cartIds) {
        this.cartIds = cartIds;
    }

    private List<Integer> cartIds;

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getSpecCodeId() {
        return specCodeId;
    }

    public void setSpecCodeId(Integer specCodeId) {
        this.specCodeId = specCodeId;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }


}
