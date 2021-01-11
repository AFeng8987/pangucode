package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.apache.commons.collections.CollectionUtils;
import org.linlinjava.litemall.db.dao.AftersaleMapper;
import org.linlinjava.litemall.db.dao.LitemallAftersaleMapper;
import org.linlinjava.litemall.db.dao.LitemallOrderGoodsMapper;
import org.linlinjava.litemall.db.dao.LitemallPlantMapper;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.util.AfterSaleUtil;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LitemallAftersaleService {
    @Resource
    private LitemallAftersaleMapper litemallAftersaleMapper;
    @Resource
    private LitemallOrderGoodsMapper litemallOrderGoodsMapper;
    @Resource
    private AftersaleMapper afterSaleMapper;
    @Resource
    private LitemallPlantMapper plantMapper;
    @Autowired
    private LitemallOrderGoodsService orderGoodsService;
    @Autowired
    private LitemallOrderService orderService;

    @Autowired
    private LitemallAllianceSaleService allianceSaleService;

    public LitemallAftersale findById(Integer id) {
        return litemallAftersaleMapper.selectByPrimaryKey(id);
    }

    public LitemallAftersale findById(Integer userId, Integer id) {
        LitemallAftersaleExample example = new LitemallAftersaleExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return litemallAftersaleMapper.selectOneByExample(example);
    }

    public List<LitemallAftersale> queryList(Integer userId, Integer status, Integer page, Integer limit, String sort, String order) {
        LitemallAftersaleExample example = new LitemallAftersaleExample();
        LitemallAftersaleExample.Criteria criteria = example.or();
        if (status != null) {
            criteria.andStatusEqualTo(status);
        }
        criteria.andDeletedEqualTo(false);
        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        } else {
            example.setOrderByClause(LitemallAftersale.Column.addTime.desc());
        }

        PageHelper.startPage(page, limit);
        return litemallAftersaleMapper.selectByExample(example);
    }

    public List<Map<String, Object>> querySelective(String orderSn, String payOrderSn,String afterSaleSn,
                                                    Integer refundStatus, Integer serviceType, String payId, LocalDate start, LocalDate end, Integer page, Integer limit) {
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (start != null) {
            startTime = start.atStartOfDay();
        }
        if (end != null) {
            endTime = end.atTime(LocalTime.MAX);
        }
        PageHelper.startPage(page, limit);
        return afterSaleMapper.queryAfterSaleList(null, orderSn,  payOrderSn,afterSaleSn, refundStatus, serviceType, payId, startTime, endTime);
    }

    public Map<String, Object> queryCountByStatus(String orderSn, String payOrderSn,String afterSaleSn,
                                                  Integer serviceType, String payId, LocalDate start, LocalDate end) {
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (start != null) {
            startTime = start.atStartOfDay();
        }
        if (end != null) {
            endTime = end.atTime(LocalTime.MAX);
        }
        return afterSaleMapper.queryCountByStatus(orderSn, payOrderSn, afterSaleSn, serviceType, payId, startTime, endTime);
    }

    private String getRandomNum(Integer num) {
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < num; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public int countByAftersaleSn(Integer userId, String aftersaleSn) {
        LitemallAftersaleExample example = new LitemallAftersaleExample();
        example.or().andAftersaleSnEqualTo(aftersaleSn).andDeletedEqualTo(false);
        return (int) litemallAftersaleMapper.countByExample(example);
    }

    // TODO 这里应该产生一个唯一的编号，但是实际上这里仍然存在两个售后编号相同的可能性
    public String generateAfterSaleSn(Integer userId) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
        String now = df.format(LocalDate.now());
        String aftersaleSn = now + getRandomNum(6);
        while (countByAftersaleSn(userId, aftersaleSn) != 0) {
            aftersaleSn = "R"+now + getRandomNum(6);
        }
        return aftersaleSn;
    }

    public void add(LitemallAftersale aftersale) {
        aftersale.setAddTime(LocalDateTime.now());
        aftersale.setUpdateTime(LocalDateTime.now());
        litemallAftersaleMapper.insertSelective(aftersale);
    }

    public void deleteByIds(List<Integer> ids) {
        LitemallAftersaleExample example = new LitemallAftersaleExample();
        example.or().andIdIn(ids).andDeletedEqualTo(false);
        LitemallAftersale aftersale = new LitemallAftersale();
        aftersale.setUpdateTime(LocalDateTime.now());
        aftersale.setDeleted(true);
        litemallAftersaleMapper.updateByExampleSelective(aftersale, example);
    }

    public void deleteById(Integer id) {
        litemallAftersaleMapper.logicalDeleteByPrimaryKey(id);
    }

    public void deleteByOrderId(Integer userId, Integer orderId, Integer orderGoodsId) {
        LitemallAftersaleExample example = new LitemallAftersaleExample();
        example.or().andDeletedEqualTo(false);
        LitemallAftersale aftersale = new LitemallAftersale();
        aftersale.setUserId(userId);
        aftersale.setOrderId(orderId);
        aftersale.setOrderGoodsId(orderGoodsId);
        aftersale.setUpdateTime(LocalDateTime.now());
        aftersale.setDeleted(true);
        litemallAftersaleMapper.updateByExampleSelective(aftersale, example);
    }

    public int updateById(LitemallAftersale aftersale) {
        aftersale.setUpdateTime(LocalDateTime.now());
       return litemallAftersaleMapper.updateByPrimaryKeySelective(aftersale);
    }






    /**
     * 根据订单ID统计售后数据,不等于某订单商品id（orderGoodsId）
     */
    public long countByOrderId(Integer orderGoodsId, Integer orderId) {
        LitemallAftersaleExample example = new LitemallAftersaleExample();
        example.or().andOrderGoodsIdNotEqualTo(orderGoodsId).andOrderIdEqualTo(orderId);
        List<Integer> status=new ArrayList<>();
        status.add(3);
        status.add(5);
        status.add(6);
        example.or().andStatusIn(status);
        return litemallAftersaleMapper.countByExample(example);
    }


    /**
     * 详情
     */
    public Object detail(Integer id) {
        LitemallAftersale afterSale = litemallAftersaleMapper.selectByPrimaryKey(id);
        LitemallOrderGoods orderGoods = litemallOrderGoodsMapper.selectByPrimaryKey(afterSale.getOrderGoodsId());
        LitemallPlant plant = plantMapper.findById(orderGoods.getPlantId());
        LitemallOrder order = orderService.findBySn(orderGoods.getOrderSn());
        Map<String, Object> data = new HashMap<>();
        data.put("orderGood", orderGoods);
        data.put("order", order);
        data.put("plant", plant);
        data.put("apply", afterSale);
        return data;
    }

    public LitemallAftersale selectOneByAfterSaleSn(String aftersaleSn) {
        LitemallAftersaleExample example = new LitemallAftersaleExample();
        example.or().andAftersaleSnEqualTo(aftersaleSn);
        return litemallAftersaleMapper.selectOneByExample(example);
    }

    public int updateData(LitemallAftersale record) {
        return afterSaleMapper.updateData(record);
    }


    public List<LitemallAftersale> queryByOgIdAndStatus(Integer userId, Integer orderGoodsId,int status) {
        LitemallAftersaleExample example = new LitemallAftersaleExample();
        example.or().andOrderGoodsIdEqualTo(orderGoodsId).andUserIdEqualTo(userId).andStatusEqualTo(status);
        example.setOrderByClause("add_time" + " " + "desc");
        return litemallAftersaleMapper.selectByExample(example);
    }

}
