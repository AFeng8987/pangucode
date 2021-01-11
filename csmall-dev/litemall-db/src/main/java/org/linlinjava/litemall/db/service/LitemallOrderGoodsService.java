package org.linlinjava.litemall.db.service;

import org.linlinjava.litemall.db.dao.LitemallOrderGoodsMapper;
import org.linlinjava.litemall.db.dao.OrderGoodsMapper;
import org.linlinjava.litemall.db.domain.LitemallAlliance;
import org.linlinjava.litemall.db.domain.LitemallAllianceSaleExample;
import org.linlinjava.litemall.db.domain.LitemallOrderGoods;
import org.linlinjava.litemall.db.domain.LitemallOrderGoodsExample;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class LitemallOrderGoodsService {
    @Resource
    private LitemallOrderGoodsMapper litemallOrderGoodsMapper;

    @Resource
    private OrderGoodsMapper ogMapper;


    public int add(LitemallOrderGoods orderGoods) {
        orderGoods.setAddTime(LocalDateTime.now());
        orderGoods.setUpdateTime(LocalDateTime.now());
        return litemallOrderGoodsMapper.insertSelective(orderGoods);
    }

    public List<LitemallOrderGoods> queryById(Integer id) {
        LitemallOrderGoodsExample example = new LitemallOrderGoodsExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return litemallOrderGoodsMapper.selectByExample(example);
    }
    public List<LitemallOrderGoods> queryByOsn(String orderSn) {
        LitemallOrderGoodsExample example = new LitemallOrderGoodsExample();
       example.or().andOrderSnEqualTo(orderSn).andDeletedEqualTo(false);
        return litemallOrderGoodsMapper.selectByExample(example);
    }

    /**
     * 不包含商品成本价
     * @param orderSn
     * @return
     */
    public List<LitemallOrderGoods> queryByOrderSn(String orderSn) {
        LitemallOrderGoodsExample example = new LitemallOrderGoodsExample();
        example.or().andOrderSnEqualTo(orderSn).andDeletedEqualTo(false);
        return litemallOrderGoodsMapper.selectByExampleSelective(example, LitemallOrderGoods.Column.excludes(LitemallOrderGoods.Column.goodsCostPrice));
    }


    /**
     * 根据订单号查订单下除指定orderGoodsId的所有订单商品
     * @param orderSn
     * @param orderGoodsId
     * @return
     */
    public List<LitemallOrderGoods> queryByOsnIsNotEqualOGId(String orderSn,Integer orderGoodsId) {
        LitemallOrderGoodsExample example = new LitemallOrderGoodsExample();
        example.or().andOrderSnEqualTo(orderSn).andIdNotEqualTo(orderGoodsId).andDeletedEqualTo(false);
        return litemallOrderGoodsMapper.selectByExample(example);
    }

    public List<LitemallOrderGoods> queryByOidAndRefund(String orderSn, boolean type) {
        LitemallOrderGoodsExample example = new LitemallOrderGoodsExample();
        example.or().andOrderSnEqualTo(orderSn).andIsRefundEqualTo(type).andDeletedEqualTo(false);
        return litemallOrderGoodsMapper.selectByExample(example);
    }

    public List<LitemallOrderGoods> queryAllianceSaleByOrderSn(String orderSn) {
        return  ogMapper.queryAllianceSaleByOrderSn(orderSn);
    }




    public List<LitemallOrderGoods> findByOidAndGid(String orderSn, Integer goodsId) {
        LitemallOrderGoodsExample example = new LitemallOrderGoodsExample();
        example.or().andOrderSnEqualTo(orderSn).andGoodsIdEqualTo(goodsId).andDeletedEqualTo(false);
        return litemallOrderGoodsMapper.selectByExample(example);
    }

    public LitemallOrderGoods findById(Integer id) {
        return litemallOrderGoodsMapper.selectByPrimaryKey(id);
    }


    public void updateById(LitemallOrderGoods orderGoods) {
        orderGoods.setUpdateTime(LocalDateTime.now());
        litemallOrderGoodsMapper.updateByPrimaryKeySelective(orderGoods);
    }

    public Short getComments(String orderSn) {
        LitemallOrderGoodsExample example = new LitemallOrderGoodsExample();

        example.or().andOrderSnEqualTo(orderSn).andDeletedEqualTo(false);
        long count = litemallOrderGoodsMapper.countByExample(example);
        return (short) count;
    }

    public boolean checkExist(Integer goodsId) {
        LitemallOrderGoodsExample example = new LitemallOrderGoodsExample();
        example.or().andGoodsIdEqualTo(goodsId).andDeletedEqualTo(false);
        return litemallOrderGoodsMapper.countByExample(example) != 0;
    }

    public void deleteByOrderSn(String orderSn) {
        LitemallOrderGoodsExample example = new LitemallOrderGoodsExample();
        example.or().andOrderSnEqualTo(orderSn).andDeletedEqualTo(false);
        litemallOrderGoodsMapper.logicalDeleteByExample(example);
    }

    public long countByGoodsId(String id) {
        LitemallOrderGoodsExample example = new LitemallOrderGoodsExample();
        example.or().andGoodsSnEqualTo(id);
        return litemallOrderGoodsMapper.countByExample(example);
    }

    public long countByOrderSnAndIsFund(String OrderSn , Boolean isFund) {
        LitemallOrderGoodsExample example = new LitemallOrderGoodsExample();
        example.or().andOrderSnEqualTo(OrderSn);
        if (isFund!=null){
            example.or().andIsRefundEqualTo(isFund);
        }
        return litemallOrderGoodsMapper.countByExample(example);
    }

    public void updateAftersaleStatus(Integer orderId, boolean isRefund) {
        LitemallOrderGoods orderGoods = new LitemallOrderGoods();
        orderGoods.setId(orderId);
        orderGoods.setUpdateTime(LocalDateTime.now());
        litemallOrderGoodsMapper.updateByPrimaryKeySelective(orderGoods);
    }

    public List<Map<String, Object>> deliveryQueryByOsn(String orderSn) {
        return ogMapper.queryOrderGoodsByOrderSn(orderSn);
    }

    public List<LitemallOrderGoods> queryByPlantIdOid(int plantId, String orderSn) {
        LitemallOrderGoodsExample example = new LitemallOrderGoodsExample();
        example.or().andOrderSnEqualTo(orderSn).andPlantIdEqualTo(plantId).andDeletedEqualTo(false);
        return litemallOrderGoodsMapper.selectByExample(example);
    }

    public int batchUpdate(List<LitemallOrderGoods> ogPlantList) {
        return ogMapper.batchUpdate(ogPlantList);
    }

    public List<LitemallOrderGoods> findByPaySn(String payOrderSn) {
        return ogMapper.queryByPaySn(payOrderSn);
    }

    public List<LitemallOrderGoods> queryGoodsListAddSales(String payOrderSn) {
        return ogMapper.queryByPaySnAddSales(payOrderSn);
    }
}
