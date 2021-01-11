package org.linlinjava.litemall.admin.service;

import cn.hutool.log.StaticLog;
import com.alibaba.fastjson.JSONObject;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.pagehelper.PageHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.linlinjava.litemall.core.express.SfExpressService;
import org.linlinjava.litemall.core.notify.NotifyService;
import org.linlinjava.litemall.core.notify.NotifyType;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.core.util.PoiExcelExport;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.dao.AllianceSaleMapper;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.service.*;
import org.linlinjava.litemall.db.util.AfterSaleUtil;
import org.linlinjava.litemall.db.util.CouponUserConstant;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.linlinjava.litemall.admin.util.AdminResponseCode.*;

@Service
public class AdminOrderService {
    private final Log logger = LogFactory.getLog(AdminOrderService.class);

    @Autowired
    private LitemallOrderGoodsService orderGoodsService;
    @Autowired
    private LitemallOrderService orderService;
    @Autowired
    private LitemallUserService userService;
    @Autowired
    private LitemallAllianceSaleService allianceSaleService;
    @Autowired
    private LitemallPlantService plantService;
    @Autowired
    private SfExpressService sfExpressService;

    @Autowired
    private LogHelper logHelper;


    /**
     * 订单查询
     *
     * @param consignee
     * @param orderSn
     * @param start
     * @param end
     * @param orderStatusArray
     * @param page
     * @param limit
     * @return
     */
    public Object list(String consignee, String orderSn,String payOrderSn,String  userName, LocalDate start, LocalDate end, List<Short> orderStatusArray,
                       Integer page, Integer limit) {
        List<Map<String, Object>> orderList = orderService.querySelective(consignee, orderSn, payOrderSn,userName, start, end, orderStatusArray, page, limit);
        return ResponseUtil.okList(orderList);
    }

    public Object detail(Integer id) {
        LitemallOrder order = orderService.findById(id);
        List<LitemallOrderGoods> orderGoods = orderGoodsService.queryByOsn(order.getOrderSn());
        LitemallUser user = userService.findById(order.getUserId());
        LitemallPlant plant = plantService.findById(order.getPlantId());
        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        data.put("orderGoods", orderGoods);
        data.put("user", user.getUsername());
        data.put("plant", plant.getPlantName());
        return ResponseUtil.ok(data);
    }

    /**
     *   发货
     * @param body
     * @return
     */
    @Transactional
    public Object ship(String body)  {
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        String shipSn = JacksonUtil.parseString(body, "shipSn");
        String shipChannel = JacksonUtil.parseString(body, "shipChannel");
        if (orderId == null ) {
            return ResponseUtil.badArgument();
        }
        LitemallOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgument();
        }
        // 如果订单不是已付款状态，则不能发货
        if (!order.getOrderStatus().equals(OrderUtil.STATUS_PAY)||order.getDeliveryStatus()) {
            return ResponseUtil.fail(-1, "订单不能发货");
        }
        //有商品在售后过程中
        List<LitemallOrderGoods> isFundOGs=orderGoodsService.queryByOidAndRefund(order.getOrderSn(),true);
        if (CollectionUtils.isNotEmpty(isFundOGs)){
            for (LitemallOrderGoods og:isFundOGs) {
                if(og.getAftersaleStatus()>=AfterSaleUtil.STATUS_GOODS_NOT_RECEIVED||og.getAftersaleStatus()==AfterSaleUtil.STATUS_RETURN_ALLOWED){
                    return ResponseUtil.fail(-1, "订单下所有商品都申请售后中暂时不能发货");
                }
            }
        }
        if (StringUtils.contains(shipChannel,"顺丰")){
            return sfDelivery(order);
        }else {
            //不发顺丰就是手动输入
            order.setOrderStatus(OrderUtil.STATUS_SHIP);
            order.setShipChannel(shipChannel);
            order.setShipSn(shipSn);
            order.setShipTime(LocalDateTime.now());
            order.setDeliveryStatus(true);
            if (orderService.updateById(order) == 0) {
                return ResponseUtil.updatedDateExpired();
            }
            logHelper.logOrderSucceed("发货", "订单编号 " + order.getOrderSn() + "，工厂:" + plantService.findById(order.getPlantId()).getPlantName());
            return ResponseUtil.ok();
        }
    }

    private Object sfDelivery(LitemallOrder order) {
        LitemallPlant plant=plantService.findById(order.getPlantId());
        order.setOrderStatus(OrderUtil.STATUS_SHIP);
        String  express=sfExpressService.createExpress(plant,order);
        if (StringUtils.isBlank(express)){
            return ResponseUtil.fail(-1,"物流下单异常");
        }
        JSONObject jsonObject = JSONObject.parseObject(express);
        Integer code = jsonObject.getInteger("code");
        if (200==code){
            StaticLog.debug("响应码："+code+"响应消息: {}", jsonObject.get("msg"));
            StaticLog.debug("JSON: {}", jsonObject.get("json"));
            StaticLog.debug("快递单号: {}", jsonObject.getJSONObject("json").get("mailNo"));
            StaticLog.debug("商城订单号: {}", jsonObject.getJSONObject("json").get("orderId"));
            order.setShipSn(jsonObject.getJSONObject("json").get("mailNo").toString());
            order.setShipChannel("顺丰快递");
            order.setShipTime(LocalDateTime.now());
            order.setDeliveryStatus(true);
            if (orderService.updateById(order) == 0) {
                return ResponseUtil.updatedDateExpired();
            }
            logHelper.logOrderSucceed("发货", "订单编号 " + order.getOrderSn() + "，工厂:" + plantService.findById(order.getPlantId()).getPlantName());
            return ResponseUtil.ok();
        }else{
            logger.info("响应消息: {"+jsonObject.get("msg")+"}");
            return ResponseUtil.fail(-1,"顺丰快递发货异常："+jsonObject.get("msg").toString());
        }
    }

    /**
     * 订单发货货品清单
     * @param id
     * @return
     */
    public Object goodsDetail(Integer id) {
        Map<String, Object> data = new HashMap<>();
        Subject currentUser = SecurityUtils.getSubject();
        LitemallAdmin admin = (LitemallAdmin) currentUser.getPrincipal();
        LitemallOrder order = orderService.findById(id);
        if (1 == admin.getStatus()) {
            if (!order.getPlantId().equals(admin.getPlantId())){
                return ResponseUtil.fail(-1,"该订单非当前用户工厂所属");
            }
        }
        List<Map<String, Object>> orderGoods = orderGoodsService.deliveryQueryByOsn(order.getOrderSn());
        LitemallUser user = userService.findById(order.getUserId());
        data.put("order", order);
        data.put("orderGoods", orderGoods);
        data.put("user", user.getUsername());
        return ResponseUtil.ok(data);
    }

    /**
     * 订单发货查询
     * @param consignee
     * @param orderSn
     * @param plantId
     * @param start
     * @param end
     * @param deliveryStatus
     * @param page
     * @param limit
     * @return
     */
    public Object deliveryList(String consignee, String orderSn,String payOrderSn, Integer plantId, LocalDate start, LocalDate end, Boolean deliveryStatus, Integer page, Integer limit) {
        Subject currentUser = SecurityUtils.getSubject();
        LitemallAdmin admin = (LitemallAdmin) currentUser.getPrincipal();
        if (1 == admin.getStatus()) {
            plantId = admin.getPlantId();
        }
        List<Map<String, Object>> orderList = orderService.deliveryList(consignee, orderSn,payOrderSn, plantId, start, end, deliveryStatus, page, limit);
        Map<String, Object> orderCount = orderService.countDeliveryList(consignee, orderSn, payOrderSn, plantId, start, end);
        return ResponseUtil.okList(orderList,orderCount);
    }

    public Object orderExportList(String orderSn,String payOrderSn, String  allianceName,String  userName, LocalDate orderAddStart, LocalDate orderAddEnd, LocalDate orderCloseStart, LocalDate orderCloseEnd, Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        return ResponseUtil.okList(allianceSaleService.exportList(null, orderSn,  payOrderSn,allianceName, userName,orderAddStart, orderAddEnd, orderCloseStart, orderCloseEnd));
    }

    /**
     * 分润订单报表导出
     */
    public void listExport(List<Integer> ids, String orderSn, String payOrderSn,String allianceName,String  userName,  LocalDate orderAddStart, LocalDate orderAddEnd, LocalDate orderCloseStart, LocalDate orderCloseEnd, HttpServletResponse response) {
        List<Map<String, Object>> list = allianceSaleService.exportList(ids, orderSn, payOrderSn, allianceName, userName,orderAddStart, orderAddEnd, orderCloseStart, orderCloseEnd);
        String sheetName = "分润订单报表";
        String tabelName = "分润订单报表";
        String titleColumn[] = {"allianceName","userName", "payOrderSn","orderSn", "categoryName", "goodsNumber", "goodsPrice", "plantCostPrice","couponPrice",
                "totalProfit",  "plantName", "orderAddTime", "orderCloseTime"};
        String titleName[] = {"加盟商姓名", "加盟商账号","主订单号","子订单号",  "商品类目名", "销售数量", "销售单价(元)", "成本单价(元)","优惠金额", "总利润(元)",
                 "供货工厂", "下单时间", "订单关闭时间"};
        PoiExcelExport.exportByMap(tabelName, sheetName, titleColumn, titleName, list, response);
    }



}
