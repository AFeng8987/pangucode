package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.linlinjava.litemall.db.dao.AllianceSaleMapper;
import org.linlinjava.litemall.db.dao.LitemallAllianceSaleMapper;
import org.linlinjava.litemall.db.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LitemallAllianceSaleService {


    @Resource
    private LitemallAllianceSaleMapper litemallAllianceSaleMapper;

    @Autowired
    private LitemallUserService userService;
    @Autowired
    private LitemallGoodsService goodsService;

    @Resource
    private AllianceSaleMapper allianceSaleMapper;

    public void add(LitemallAllianceSale sale) {
        sale.setAddTime(LocalDateTime.now());
        litemallAllianceSaleMapper.insertSelective(sale);
    }



    public List<Map<String, Object>> queryByAllianceUserId(Integer allianceUserId, Integer type, Integer page, Integer limit) {
        LocalDateTime firstDay = null;
        if (0 == type) {
            firstDay = LocalDateTime.now().minusMonths(1);
        } else if (1 == type) {
            firstDay = LocalDateTime.now().minusMonths(3);
        } else if (2 == type) {
            firstDay = LocalDateTime.now().minusMonths(6);
        }
        PageHelper.startPage(page, limit);
        return allianceSaleMapper.queryByAllianceUserId(allianceUserId, firstDay);
    }

    public Map<String, Object> totalAllianceData(Integer userId, Integer type) {
        LocalDateTime firstDay = null;
        if (0 == type) {
            firstDay = LocalDateTime.now().minusMonths(1);
        } else if (1 == type) {
            firstDay = LocalDateTime.now().minusMonths(3);
        } else if (2 == type) {
            firstDay = LocalDateTime.now().minusMonths(6);
        }
        return allianceSaleMapper.totalAllianceData(userId, firstDay);
    }


    public List<Map<String, Object>> exportList(List<Integer> ids, String orderSn,String payOrderSn, String allianceName, String userName, LocalDate orderAddStart, LocalDate orderAddEnd, LocalDate orderCloseStart, LocalDate orderCloseEnd) {
        LocalDateTime orderAddStartTime = null;
        LocalDateTime orderAddEndTime = null;
        LocalDateTime orderCloseStartTime = null;
        LocalDateTime orderCloseEndTime = null;
        if (orderAddStart != null) {
            orderAddStartTime = orderAddStart.atStartOfDay();
        }
        if (StringUtils.isBlank(allianceName)) {
            allianceName = null;
        }
        if (StringUtils.isBlank(userName)) {
            userName = null;
        }
        if (orderAddEnd != null) {
            orderAddEndTime = orderAddEnd.atTime(LocalTime.MAX);
        }
        if (orderCloseStart != null) {
            orderCloseStartTime = orderCloseStart.atStartOfDay();
        }
        if (orderCloseEnd != null) {
            orderCloseEndTime = orderCloseEnd.atTime(LocalTime.MAX);
        }

        return allianceSaleMapper.exportList(ids, orderSn, payOrderSn,allianceName, userName, orderAddStartTime, orderAddEndTime, orderCloseStartTime, orderCloseEndTime);
    }


    @Transactional
    public void addAllianceSale(LitemallOrder order, LitemallOrderGoods orderGoods) {
        LitemallAllianceSale allianceSale = new LitemallAllianceSale();
        Integer allianceUserId = -1;
        String allianceName = "平台所属";
        //查询订单所属加盟商
        if (-1 != order.getAllianceUserid()) {
            LitemallUser user = userService.findById(order.getAllianceUserid());
            if (user.getUserLevel() == 1) {
                allianceName=userService.selAlliance(order.getAllianceUserid()).getAllianceName();
                allianceUserId=user.getId();
            } else {
                String address = order.getAddress().substring(0, order.getAddress().lastIndexOf("-"));
                LitemallAlliance alliance = userService.selAllianceByAddress(address);
                if (alliance != null) {
                    allianceName=alliance.getAllianceName();
                    allianceUserId=alliance.getUserId();
                }
            }
        }
        allianceSale.setAllianceUserid(allianceUserId);
        allianceSale.setAllianceName(allianceName);
        allianceSale.setOrderSn(order.getOrderSn());
        allianceSale.setOrderGoodsId(orderGoods.getId());

        LitemallGoods goods = goodsService.findById(orderGoods.getGoodsId());
        allianceSale.setCategoryId(goods.getCategoryId());

        allianceSale.setGoodsNumber(Integer.valueOf(orderGoods.getNumber()));
        allianceSale.setGoodsPrice(orderGoods.getPrice());
        allianceSale.setPlantCostPrice(orderGoods.getGoodsCostPrice());
        allianceSale.setCouponPrice(orderGoods.getCouponPrice());
        //总利润=数量*（ 销售单价-成本单价 ）
        BigDecimal total = (orderGoods.getPrice().subtract(orderGoods.getGoodsCostPrice())).multiply(new BigDecimal(Integer.valueOf(orderGoods.getNumber()))).subtract(orderGoods.getCouponPrice());
        if (total.compareTo(new BigDecimal(0)) == -1) {
            total = new BigDecimal(0);
        }
        allianceSale.setTotalProfit(total);

//        allianceSale.setGroupId(order.getGroupId());
        allianceSale.setPlantId(orderGoods.getPlantId());

        allianceSale.setOrderAddTime(order.getAddTime());
        allianceSale.setOrderCloseTime(order.getEndTime());


        add(allianceSale);
    }
}
