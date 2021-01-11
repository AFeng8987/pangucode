package org.linlinjava.litemall.wx.service;

import cn.hutool.log.StaticLog;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.order.WxPayMwebOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.BaseWxPayResult;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.util.SignUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.config.WxConfig;
import org.linlinjava.litemall.core.config.WxProperties;
import org.linlinjava.litemall.core.express.ExpressService;
import org.linlinjava.litemall.core.express.SfExpressService;
import org.linlinjava.litemall.core.express.dao.ExpressInfo;
import org.linlinjava.litemall.core.notify.NotifyService;
import org.linlinjava.litemall.core.notify.NotifyType;
import org.linlinjava.litemall.core.pay.AlipayService;
import org.linlinjava.litemall.core.qcode.QCodeService;
import org.linlinjava.litemall.core.system.SystemConfig;
import org.linlinjava.litemall.core.task.TaskService;
import org.linlinjava.litemall.core.util.*;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.service.*;
import org.linlinjava.litemall.db.util.CouponUserConstant;
import org.linlinjava.litemall.db.util.GrouponConstant;
import org.linlinjava.litemall.db.util.OrderHandleOption;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.linlinjava.litemall.wx.task.OrderUnpaidTask;
import org.linlinjava.litemall.wx.vo.BuyOrderGoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.linlinjava.litemall.wx.util.WxResponseCode.*;

/**
 * 订单服务
 *
 * <p>
 * 订单状态：
 * 101 订单生成，未支付；102，下单后未支付用户取消；103，下单后未支付超时系统自动取消
 * 201 支付完成，商家未发货；202，订单生产，已付款未发货，但是退款取消；
 * 301 商家发货，用户未确认；
 * 401 用户确认收货； 402 用户没有确认收货超过一定时间，系统自动确认收货；
 *
 * <p>
 * 用户操作：
 * 当101用户未付款时，此时用户可以进行的操作是取消订单，或者付款操作
 * 当201支付完成而商家未发货时，此时用户可以取消订单并申请退款
 * 当301商家已发货时，此时用户可以有确认收货的操作
 * 当401用户确认收货以后，此时用户可以进行的操作是删除订单，评价商品，申请售后，或者再次购买
 * 当402系统自动确认收货以后，此时用户可以删除订单，评价商品，申请售后，或者再次购买
 */
@Service
public class WxOrderService {
    private final Log logger = LogFactory.getLog(WxOrderService.class);

    @Autowired
    private WxProperties properties;
    @Autowired
    private LitemallUserService userService;
    @Autowired
    private LitemallOrderService orderService;
    @Autowired
    private LitemallOrderGoodsService orderGoodsService;

    @Autowired
    private LitemallGoodsService goodsService;
    @Autowired
    private LitemallAddressService addressService;
    @Autowired
    private LitemallCartService cartService;
    @Autowired
    private SfExpressService sfExpressService;
    @Autowired
    private AlipayService alipayService;
    @Autowired
    private LitemallProductFactoryService productFactoryService;
    @Autowired
    private LitemallSpecodeProductService specsCodeProductService;
    @Autowired
    private WxPayService wxPayService;
    @Autowired
    private ExpressService expressService;
    @Autowired
    private LitemallGrouponRulesService grouponRulesService;
    @Autowired
    private LitemallGrouponService grouponService;
    @Autowired
    private QCodeService qCodeService;


    @Autowired
    private LitemallCouponService couponService;
    @Autowired
    private LitemallCouponUserService couponUserService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private LitemallStockRecordService stockRecordService;
    @Autowired
    private LitemallNodeRelationService nodeRelationService;

    /**
     * 订单列表
     *
     * @param userId   用户ID
     * @param showType 订单信息：
     *                 0，全部订单；
     *                 1，待付款；
     *                 2，待发货；
     *                 3，待收货；
     *                 4，待评价。
     * @param page     分页页数
     * @param limit    分页大小
     * @return 订单列表
     */
    public Object list(Integer userId, Integer showType, Integer page, Integer limit) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        List<Short> orderStatus = OrderUtil.orderStatus(showType);
        List<String> payOrderSnList = orderService.queryAPPListByOrderStatus(userId, orderStatus, page, limit);
        List<Map<String, Object>> orderPayVoList = new ArrayList<>(payOrderSnList.size());
        for (String payOrderSn : payOrderSnList) {
            List<LitemallOrder> orderList = orderService.findByPaySn(payOrderSn, userId);
            Map<String, Object> orderPayVo = new HashMap<>();
            orderPayVo.put("payOrderSn", payOrderSn);
            List<Map<String, Object>> orderVoList = new ArrayList<>(orderList.size());
            for (LitemallOrder o : orderList) {
                Map<String, Object> orderVo = new HashMap<>();
                orderVo.put("orderId", o.getId());
                orderVo.put("orderSn", o.getOrderSn());
                orderVo.put("goodsPrice", o.getGoodsPrice());
                orderVo.put("orderPrice", o.getOrderPrice());
                orderVo.put("freightPrice", o.getFreightPrice());
                orderVo.put("couponPrice", o.getCouponPrice());
                orderVo.put("orderStatus", o.getOrderStatus());

                List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOrderSn(o.getOrderSn());
                List<Map<String, Object>> orderGoodsVoList = new ArrayList<>(orderGoodsList.size());
                for (LitemallOrderGoods orderGoods : orderGoodsList) {
                    Map<String, Object> orderGoodsVo = new HashMap<>();
                    orderGoodsVo.put("orderGoodsId", orderGoods.getId());
                    orderGoodsVo.put("goodsName", orderGoods.getGoodsName());
                    orderGoodsVo.put("number", orderGoods.getNumber());
                    orderGoodsVo.put("picUrl", orderGoods.getPicUrl());
                    orderGoodsVo.put("specifications", orderGoods.getSpecifications());
                    orderGoodsVo.put("goodsPrice", orderGoods.getPrice());
                    orderGoodsVoList.add(orderGoodsVo);
                }
                orderVo.put("goodsList", orderGoodsVoList);
                orderVoList.add(orderVo);
            }
            orderPayVo.put("orderList", orderVoList);
            orderPayVoList.add(orderPayVo);
        }

        return ResponseUtil.okList(orderPayVoList, payOrderSnList);
    }

    /**
     * 订单详情
     *
     * @param userId     用户ID
     * @param payOrderSn 付款订单嗯呢
     * @return 订单详情
     */
    public Object detailByPayOrderSn(Integer userId, String payOrderSn) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        // 订单信息
        List<LitemallOrder> orderList = orderService.findByPaySn(payOrderSn, userId);
        if (CollectionUtils.isEmpty(orderList)) {
            return ResponseUtil.fail(ORDER_UNKNOWN, "订单不存在");
        }
        List<Map<String, Object>> orderVoList = new ArrayList<>(orderList.size());
        for (LitemallOrder o : orderList) {
            Map<String, Object> orderVo = new HashMap<String, Object>();
            orderVo.put("orderId", o.getId());
            orderVo.put("orderSn", o.getOrderSn());
            orderVo.put("payOrderSn", o.getPayOrderSn());
            orderVo.put("message", o.getMessage());
            orderVo.put("addTime", o.getAddTime());
            orderVo.put("consignee", o.getConsignee());
            orderVo.put("mobile", o.getMobile());
            orderVo.put("address", o.getAddress());
            orderVo.put("payId", o.getPayId());
            orderVo.put("payType", o.getPayType());
            orderVo.put("payTime", o.getPayTime());
            orderVo.put("goodsPrice", o.getGoodsPrice());
            orderVo.put("freightPrice", o.getFreightPrice());
            orderVo.put("couponPrice", o.getCouponPrice());
            orderVo.put("orderPrice", o.getOrderPrice());
            orderVo.put("actualPrice", o.getActualPrice());
            orderVo.put("orderStatus", o.getOrderStatus());
            orderVo.put("expCode", o.getShipChannel());
            orderVo.put("expNo", o.getShipSn());

            List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOrderSn(o.getOrderSn());
            orderVo.put("goodsList", orderGoodsList);
            orderVoList.add(orderVo);
        }
        return ResponseUtil.ok(orderVoList);

    }

    /**
     * 订单详情
     *
     * @param userId  用户ID
     * @param orderId 付款订单嗯呢
     * @return 订单详情
     */
    public Object detailByOrderId(Integer userId, Integer orderId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        // 订单信息
        LitemallOrder o = orderService.findById(userId, orderId);
        List<Map<String, Object>> orderVoList = new ArrayList<>();
        Map<String, Object> orderVo = new HashMap<String, Object>();
        orderVo.put("orderId", o.getId());
        orderVo.put("orderSn", o.getOrderSn());
        orderVo.put("payOrderSn", o.getPayOrderSn());
        orderVo.put("message", o.getMessage());
        orderVo.put("addTime", o.getAddTime());
        orderVo.put("consignee", o.getConsignee());
        orderVo.put("mobile", o.getMobile());
        orderVo.put("address", o.getAddress());
        orderVo.put("payId", o.getPayId());
        orderVo.put("payType", o.getPayType());
        orderVo.put("payTime", o.getPayTime());
        orderVo.put("goodsPrice", o.getGoodsPrice());
        orderVo.put("freightPrice", o.getFreightPrice());
        orderVo.put("couponPrice", o.getCouponPrice());
        orderVo.put("orderPrice", o.getOrderPrice());
        orderVo.put("actualPrice", o.getActualPrice());
        orderVo.put("orderStatus", o.getOrderStatus());
        orderVo.put("expCode", o.getShipChannel());
        orderVo.put("expNo", o.getShipSn());

        List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOrderSn(o.getOrderSn());
        orderVo.put("goodsList", orderGoodsList);
        orderVoList.add(orderVo);

        return ResponseUtil.ok(orderVoList);

    }


    /**
     * 提交订单
     * <p>
     * 1. 创建订单表项和订单商品表项;
     * 2. 购物车清空;
     * 3. 优惠券设置已用;
     * 4. 商品货品库存减少;
     * 5. 添加出库记录
     * 6. 订单支付超期任务
     * </p>
     *
     * @param userId 用户ID
     * @param body   订单信息，{ cartId：xxx, addressId: xxx, couponId: xxx, message: xxx, grouponRulesId: xxx}
     * @return 提交订单操作结果
     */
    @Transactional
    public Object submit(Integer userId, String body) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        if (body == null) {
            return ResponseUtil.badArgument();
        }
        List<Integer> cartIds = JacksonUtil.parseIntegerList(body, "cartIds");
        BuyOrderGoodsVo buyOrderGoodsVo = JacksonUtil.parseObject(body, "buyOrderGoodsVo", BuyOrderGoodsVo.class);
        Integer addressId = JacksonUtil.parseInteger(body, "addressId");
        String message = JacksonUtil.parseString(body, "message");
        Integer userCouponId = JacksonUtil.parseInteger(body, "userCouponId");
        if (ObjectUtils.isEmpty(addressId)) {
            return ResponseUtil.badArgument();
        }
        if (ObjectUtils.allNotNull(cartIds, buyOrderGoodsVo) || ObjectUtils.allNull(cartIds, buyOrderGoodsVo)) {
            return ResponseUtil.badArgument();
        }
        // 收货地址
        LitemallAddress checkedAddress = addressService.query(userId, addressId);
        if (checkedAddress == null) {
            return ResponseUtil.fail(401, "收获地址不存在");
        }

        LitemallUser user = userService.findById(userId);
        // 获取可用的优惠券信息
        BigDecimal couponPrice = new BigDecimal(0);
        LitemallCouponUser couponUser = null;
        if (null != userCouponId) {
            couponUser = couponUserService.findNotUsedById(userCouponId);
            if (couponUser == null) {
                return ResponseUtil.fail(401, "优惠卷不可用，请重新选择");
            }
            LitemallCoupon coupon = couponService.findById(couponUser.getCouponId());
            // 使用优惠券减免的金额
            couponPrice = couponPrice.add(coupon.getDiscount());
        }
        //详细地址
        String detailedAddress = checkedAddress.getProvince() + "-" + checkedAddress.getCity() + "-" + checkedAddress.getCounty() + "-" + checkedAddress.getStreet() + "-" + checkedAddress.getAddressDetail();
        Integer allianceUserId = matchingAlliance(user.getId(), detailedAddress);
        //生成支付单号
        String payOrderSn = "PAY" + System.currentTimeMillis() + (int) ((Math.random() * 9 + 1) * 100);
        if (null != buyOrderGoodsVo) {
            if (ObjectUtils.anyNull(buyOrderGoodsVo.getSpecCodeId(), buyOrderGoodsVo.getGoodsId(), buyOrderGoodsVo.getNumber())) {
                return ResponseUtil.fail(401, "获取商品失败");
            }
            return buyNowSubmit(payOrderSn, user, buyOrderGoodsVo, checkedAddress.getName(), checkedAddress.getTel(), detailedAddress, message, couponPrice, allianceUserId, couponUser);
        }
        if (CollectionUtils.isEmpty(cartIds)) {
            return ResponseUtil.fail(401, "获取商品失败");
        }
        List<LitemallCart> cartList = cartService.userQueryByIds(userId, cartIds);
        if (CollectionUtils.isEmpty(cartList) || cartList.size() != cartIds.size()) {
            return ResponseUtil.fail(401, "获取商品失败");
        }
        if (cartList.size() == 1) {
            return oneCartSubmit(payOrderSn, user, cartList.get(0), checkedAddress.getName(), checkedAddress.getTel(), detailedAddress, message, couponPrice, allianceUserId, couponUser);
        } else {
            return moreCartSubmit(payOrderSn, user, cartList, checkedAddress.getName(), checkedAddress.getTel(), detailedAddress, message, couponPrice, allianceUserId, couponUser, cartIds);
        }
    }

    /**
     * 1. 创建订单表项和订单商品表项;
     * 2. 购物车清空;
     * 3. 优惠券设置已用;
     * 4. 商品货品库存减少;
     * 5. 添加出库记录
     * 6. 订单支付超期任务
     *
     * @param user            用户信息
     * @param cart            购物车单个对象
     * @param consignee       收货人
     * @param mobile          收货人联系电话
     * @param detailedAddress 收货地址
     * @param message         备注
     * @param couponPrice     优惠金额
     * @param allianceUserId  订单归属的加盟商
     * @return
     */
    private Object oneCartSubmit(String payOrderSn, LitemallUser user, LitemallCart cart, String consignee, String mobile, String detailedAddress, String message, BigDecimal couponPrice, Integer allianceUserId, LitemallCouponUser couponUser) {
        logger.info("单个商品下单，用户：" + user.getUsername() + "--购物车ID：" + cart.getId() + "--商品名：" + cart.getGoodsName() + "--支付单号：" + payOrderSn);
        //订单商品总价格
        BigDecimal GoodsTotalPrice = new BigDecimal(0);
        //校验库存是否足够
        LitemallProductFactory productFactory = productFactoryService.getOneByGoodId(cart.getGoodsCodeId(), cart.getSpecCodeId());
        LitemallSpecodeProduct specodeProduct = specsCodeProductService.findById(cart.getSpecCodeId());
        int remainNumber = productFactory.getGoodsStock() - cart.getNumber();
        if (remainNumber < 0) {
            return ResponseUtil.fail(GOODS_NO_STOCK, "商品：" + cart.getGoodsName() + "总库存：" + specodeProduct.getTotalStock() + "，匹配最优仓库当前库存仅" + productFactory.getGoodsStock() + "请下单数量不能超过最优仓库的库存数");
        }
        //更新工厂库存数据
        productFactory.setGoodsStock(productFactory.getGoodsStock() - cart.getNumber());
        productFactoryService.updateById(productFactory);
        //更新规格的总库存数据
        specodeProduct.setTotalStock(specodeProduct.getTotalStock() - cart.getNumber());
        specsCodeProductService.updateById(specodeProduct);
        //添加出库记录
        stockRecordService.add(specodeProduct.getId(), productFactory.getFactoryId(), Integer.valueOf(cart.getNumber()), 1, "用户下单", user.getNickname());
        GoodsTotalPrice = GoodsTotalPrice.add(specodeProduct.getShopPrice().multiply(new BigDecimal(cart.getNumber())));
        // 根据订单商品总价计算运费，满足条件（例如88元）则免运费，否则需要支付运费（例如8元）；
        BigDecimal freightPrice = new BigDecimal(0);
        if (GoodsTotalPrice.compareTo(SystemConfig.getFreightLimit()) < 0) {
            freightPrice = SystemConfig.getFreight();
        }
        // 订单费用=商品费用+运费-优惠金额，如果小于0，就取0；
        BigDecimal orderPrice = GoodsTotalPrice.add(freightPrice).subtract(couponPrice).max(new BigDecimal(0));
        // 最终支付费用
        BigDecimal actualPrice = orderPrice;

        LitemallOrder order = new LitemallOrder();
        LitemallOrderGoods orderGoods = new LitemallOrderGoods();
        /*填充订单信息*/
        String orderSn = "PG" + orderService.generateOrderSn(user.getId());
        order.setUserId(user.getId());
        order.setPayOrderSn(payOrderSn);
        order.setOrderSn(orderSn);
        order.setOrderStatus(OrderUtil.STATUS_CREATE);
        order.setMessage(message);
        //收货信息
        order.setConsignee(consignee);
        order.setMobile(mobile);
        order.setAddress(detailedAddress);
        //价格
        order.setFreightPrice(freightPrice);
        order.setGoodsPrice(GoodsTotalPrice);
        order.setOrderPrice(orderPrice);
        order.setActualPrice(actualPrice);
        order.setCouponPrice(couponPrice);
        //订单分润所属加盟商
        order.setAllianceUserid(allianceUserId);
        //订单供货工厂
        order.setPlantId(productFactory.getFactoryId());
        orderService.add(order);
        /*填充订单商品信息*/
        orderGoods.setOrderSn(orderSn);
        orderGoods.setGoodsId(cart.getGoodsId());//商品ID
        orderGoods.setGoodsCodeId(cart.getGoodsCodeId());//商品编码
        orderGoods.setProductId(cart.getSpecCodeId());//规格编码
        orderGoods.setGoodsName(cart.getGoodsName());//规格名称
        orderGoods.setPicUrl(specodeProduct.getUrl());//规格图片
        orderGoods.setPrice(specodeProduct.getShopPrice());
        orderGoods.setNumber(cart.getNumber());
        orderGoods.setSpecifications(specodeProduct.getSpecsDesc());
        orderGoods.setCouponPrice(couponPrice);
        orderGoods.setGoodsCostPrice(productFactory.getCostPrice());
        orderGoods.setPlantId(productFactory.getFactoryId());
        orderGoodsService.add(orderGoods);
        //删除购物车数据
        cartService.deleteById(cart.getId());
        //优惠券扭转已使用
        if (null != couponUser) {
            couponUser.setPayOrderSn(payOrderSn);
            couponUser.setUsedTime(LocalDateTime.now());
            couponUser.setStatus(CouponUserConstant.STATUS_USED);
            couponUserService.update(couponUser);
        }
        // 订单支付超期任务
        taskService.addTask(new OrderUnpaidTask(order.getPayOrderSn()));
        return ResponseUtil.ok(payOrderSn);
    }

    /**
     * 1. 创建订单表项和订单商品表项;
     * 2. 购物车清空;
     * 3. 优惠券设置已用;
     * 4. 商品货品库存减少;
     * 5. 添加出库记录
     * 6. 订单支付超期任务
     * 后面优化所有for循环处理数据的,改用batchInsert
     * @param payOrderSn
     * @param user
     * @param cartList
     * @param consignee
     * @param mobile
     * @param detailedAddress
     * @param message
     * @param couponPrice
     * @param allianceUserId
     * @param couponUser
     * @return
     */
    private Object moreCartSubmit(String payOrderSn, LitemallUser user, List<LitemallCart> cartList, String consignee, String mobile, String detailedAddress, String message, BigDecimal couponPrice, Integer allianceUserId, LitemallCouponUser couponUser, List<Integer> cartIds) {
        logger.info("多个商品下单，用户：" + user.getUsername() + "--购物车ID：" + cartIds.toString() + "--商品名：" + cartList.toString() + "--支付单号：" + payOrderSn);
        //统计所有商品的价格
        BigDecimal totalAllGoodsPrice = cartList.stream().map(cart -> cart.getPrice().multiply(new BigDecimal(cart.getNumber()))).reduce(BigDecimal.ZERO, WxOrderService::sum);
        List<LitemallOrderGoods> orderGoodsList = new ArrayList<>();
        //需要更新的库存数据的list
        List<LitemallProductFactory> productFactoryList = new ArrayList<>();
        List<LitemallSpecodeProduct> specCodeProductList = new ArrayList<>();
        //出库记录
        List<Map<String, Integer>> mapList = new ArrayList<>();
        // 将购物车的商品按工厂分类
        for (LitemallCart cart : cartList) {
            Map<String, Integer> map = new HashMap<>();
            //根据规格编码和商品编码匹配最优供货方
            LitemallProductFactory productFactory = productFactoryService.getOneByGoodId(cart.getGoodsCodeId(), cart.getSpecCodeId());
            LitemallSpecodeProduct specodeProduct = specsCodeProductService.findById(cart.getSpecCodeId());
            LitemallGoods goods = goodsService.queryBuyById(cart.getGoodsId());
            if (ObjectUtils.anyNull(productFactory, specodeProduct, goods)) {
                return ResponseUtil.fail(-1, "购物车数据错误，导致下单失败");
            }
            int remainNumber = productFactory.getGoodsStock() - cart.getNumber();
            if (remainNumber < 0) {
                return ResponseUtil.fail(GOODS_NO_STOCK, "商品：" + cart.getGoodsName() + "存在多个仓库，总库存：" + specodeProduct.getTotalStock() + "，但是当前单仓库最高库存，仅库存" + productFactory.getGoodsStock() + "请分开下单");
            }
            map.put("spId", specodeProduct.getId());
            map.put("plantId", productFactory.getFactoryId());
            map.put("number", Integer.valueOf(cart.getNumber()));
            mapList.add(map);
            //更新工厂库存数据
            productFactory.setGoodsStock(productFactory.getGoodsStock() - cart.getNumber());
            productFactoryList.add(productFactory);
            //更新规格的总库存数据
            specodeProduct.setTotalStock(specodeProduct.getTotalStock() - cart.getNumber());
            specCodeProductList.add(specodeProduct);
            //将购物车的数据转换订单商品数据，无须分组
            LitemallOrderGoods orderGoods = new LitemallOrderGoods();
            // orderGoods.setOrderSn();因为没有分组，OrderSn先不做赋值
            //填充订单商品表数据
            orderGoods.setGoodsId(goods.getId());
            //orderGoods.setOrderStatus(OrderUtil.STATUS_CREATE);
            orderGoods.setGoodsCodeId(goods.getGoodsCodeId());
            orderGoods.setGoodsSn(goods.getGoodsSn());
            orderGoods.setProductId(productFactory.getSpecCodeId());
            orderGoods.setGoodsName(goods.getName());
            orderGoods.setPicUrl(specodeProduct.getUrl());
            orderGoods.setPrice(specodeProduct.getShopPrice());
            orderGoods.setNumber(cart.getNumber());
            orderGoods.setSpecifications(specodeProduct.getSpecsDesc());
            orderGoods.setPlantId(productFactory.getFactoryId());
            orderGoods.setGoodsCostPrice(productFactory.getCostPrice());
            orderGoods.setAddTime(LocalDateTime.now());
            orderGoodsService.add(orderGoods);
            orderGoodsList.add(orderGoods);
        }
        //将订单商品表的数据按工厂分组
        Map<Integer, List<LitemallOrderGoods>> groupByPlant = orderGoodsList.stream().collect(Collectors.groupingBy(LitemallOrderGoods::getPlantId));
        // 根据订单商品总价计算运费，满足条件（例如88元）则免运费，否则需要支付运费（例如8元）；
        BigDecimal freightPrice = new BigDecimal(0);
        if (totalAllGoodsPrice.compareTo(SystemConfig.getFreightLimit()) < 0) {
            freightPrice = SystemConfig.getFreight();
        }
        //遍历分组
        List<LitemallOrder> orderList = new ArrayList<>();
        for (Map.Entry<Integer, List<LitemallOrderGoods>> entryOrderGoodsList : groupByPlant.entrySet()) {
            Integer plantId = entryOrderGoodsList.getKey();
            List<LitemallOrderGoods> orderGoods = entryOrderGoodsList.getValue();
            //生成订单号
            String orderSn = "PG" + orderService.generateOrderSn(user.getId());
            //给该厂下所有的订单商品数据赋值
            orderGoods.stream().forEach(og -> og.setOrderSn(orderSn));
            groupByPlant.put(plantId, orderGoods);
            /*填充订单信息*/
            LitemallOrder order = new LitemallOrder();
            order.setUserId(user.getId());
            order.setPayOrderSn(payOrderSn);
            order.setOrderSn(orderSn);
            order.setOrderStatus(OrderUtil.STATUS_CREATE);
            order.setMessage(message);
            //收货信息
            order.setConsignee(consignee);
            order.setMobile(mobile);
            order.setAddress(detailedAddress);
            //订单分润所属加盟商
            order.setAllianceUserid(allianceUserId);
            //订单供货工厂
            order.setPlantId(plantId);
            //价格
            order.setFreightPrice(freightPrice);
            //汇总当前商品的所有价格
            BigDecimal totalGoodsPrice = orderGoods.stream().map(orderGood -> orderGood.getPrice().multiply(new BigDecimal(orderGood.getNumber()))).reduce(BigDecimal.ZERO, WxOrderService::sum);
            order.setGoodsPrice(totalGoodsPrice);
            //金额等于0,如果不等于0就等for处理完去处理；
            if (BigDecimal.ZERO.compareTo(couponPrice) == 0) {
                BigDecimal orderPrice = totalGoodsPrice.add(freightPrice);
                order.setOrderPrice(orderPrice);
                order.setActualPrice(orderPrice);
                order.setCouponPrice(couponPrice);
            }
            orderList.add(order);
        }
        if (BigDecimal.ZERO.compareTo(couponPrice) == 0) {
            orderList.stream().forEach(order -> {
                BigDecimal orderPrice = order.getGoodsPrice().add(order.getFreightPrice());
                order.setOrderPrice(orderPrice);
                order.setActualPrice(orderPrice);
                order.setCouponPrice(couponPrice);
                List<LitemallOrderGoods> orderGoods = groupByPlant.get(order.getPlantId());
                orderGoods.stream().forEach(oGoods -> {
                    orderGoodsService.updateById(oGoods);
                });
            });
        } else {
            //优惠金额拆分到子单
            Map<String, BigDecimal> map = orderDiscount(orderList, couponPrice);
            orderList.stream().forEach(order -> {
                BigDecimal orderPrice = order.getGoodsPrice().add(order.getFreightPrice()).subtract(map.get(order.getOrderSn())).max(new BigDecimal(0));
                order.setOrderPrice(orderPrice);
                order.setActualPrice(orderPrice);
                order.setCouponPrice(map.get(order.getOrderSn()));
                //得到子订单数据
                List<LitemallOrderGoods> orderGoods = groupByPlant.get(order.getPlantId());
                //处理子订单每个商品优惠金额,
                Map<Integer, BigDecimal> ogMap = orderGoodsDiscount(orderGoods, map.get(order.getOrderSn()));
                for (Map.Entry<Integer, BigDecimal> data : ogMap.entrySet()) {
                    LitemallOrderGoods oGoods = orderGoods.stream().filter(og -> og.getId().equals(data.getKey())).findFirst().orElse(null);
                    if (null != oGoods) {
                        oGoods.setCouponPrice(data.getValue());
                        orderGoodsService.updateById(oGoods);
                    }
                }
            });
        }
        //订单批量插入
        orderList.stream().forEach(p -> {
            p.setUpdateTime(LocalDateTime.now());
            p.setAddTime(LocalDateTime.now());
        });
        orderService.batchInsert(orderList);
        //删除购物车数据
        cartService.batchDelete(cartIds);
        //优惠券扭转已使用
        if (null != couponUser) {
            couponUser.setPayOrderSn(payOrderSn);
            couponUser.setUsedTime(LocalDateTime.now());
            couponUser.setStatus(CouponUserConstant.STATUS_USED);
            couponUserService.update(couponUser);
        }
        // 订单支付超期任务
        taskService.addTask(new OrderUnpaidTask(payOrderSn));

        //更新的库存数据
        productFactoryList.stream().forEach(p -> {
            productFactoryService.updateById(p);
        });
        specCodeProductList.stream().forEach(s -> {
            specsCodeProductService.updateById(s);
        });
        //出库记录
        mapList.stream().forEach(map -> {
            stockRecordService.add(map.get("spId"), map.get("plantId"), map.get("number"), 1, "用户下单", user.getNickname());
        });
        return ResponseUtil.ok(payOrderSn);
    }

    /**
     * 1. 创建订单表项和订单商品表项;
     * 2. 购物车清空;
     * 3. 优惠券设置已用;
     * 4. 商品货品库存减少;
     * 5. 添加出库记录
     * 6. 订单支付超期任务
     *
     * @param user            用户信息
     * @param buyOrderGoodsVo 立刻购买实体
     * @param consignee       收货人
     * @param mobile          收货人联系电话
     * @param detailedAddress 收货地址
     * @param message         备注
     * @param couponPrice     优惠金额
     * @param allianceUserId  订单归属的加盟商
     * @return
     */
    private Object buyNowSubmit(String payOrderSn, LitemallUser user, BuyOrderGoodsVo buyOrderGoodsVo, String consignee, String mobile, String detailedAddress, String message, BigDecimal couponPrice, Integer allianceUserId, LitemallCouponUser couponUser) {
        //订单商品总价格
        BigDecimal GoodsTotalPrice = new BigDecimal(0);
        LitemallGoods goods = goodsService.findById(buyOrderGoodsVo.getGoodsId());
        logger.info("立即购买商品下单，用户：" + user.getUsername() + "--商品：" + buyOrderGoodsVo.getGoodsId() + "--商品名：" + goods.getName() + "--支付单号：" + payOrderSn);
        //校验库存是否足够
        LitemallProductFactory productFactory = productFactoryService.getOneByGoodId(goods.getGoodsCodeId(), buyOrderGoodsVo.getSpecCodeId());
        LitemallSpecodeProduct specodeProduct = specsCodeProductService.findById(buyOrderGoodsVo.getSpecCodeId());
        int remainNumber = productFactory.getGoodsStock() - buyOrderGoodsVo.getNumber();
        if (remainNumber < 0) {
            return ResponseUtil.fail(-1, "商品：" + goods.getName() + "总库存：" + specodeProduct.getTotalStock() + "，匹配最优仓库当前库存仅" + productFactory.getGoodsStock() + "请下单数量不能超过最优仓库的库存数");
        }
        //更新工厂库存数据
        productFactory.setGoodsStock(productFactory.getGoodsStock() - buyOrderGoodsVo.getNumber());
        productFactoryService.updateById(productFactory);
        //更新规格的总库存数据
        specodeProduct.setTotalStock(specodeProduct.getTotalStock() - buyOrderGoodsVo.getNumber());
        specsCodeProductService.updateById(specodeProduct);
        //添加出库记录
        stockRecordService.add(specodeProduct.getId(), productFactory.getFactoryId(), Integer.valueOf(buyOrderGoodsVo.getNumber()), 1, "用户下单", user.getNickname());
        GoodsTotalPrice = GoodsTotalPrice.add(specodeProduct.getShopPrice().multiply(new BigDecimal(buyOrderGoodsVo.getNumber())));
        // 根据订单商品总价计算运费，满足条件（例如88元）则免运费，否则需要支付运费（例如8元）；
        BigDecimal freightPrice = new BigDecimal(0);
        if (GoodsTotalPrice.compareTo(SystemConfig.getFreightLimit()) < 0) {
            freightPrice = SystemConfig.getFreight();
        }
        // 订单费用=商品费用+运费-优惠金额，如果小于0，就取0；
        BigDecimal orderPrice = GoodsTotalPrice.add(freightPrice).subtract(couponPrice).max(new BigDecimal(0));
        // 最终支付费用
        BigDecimal actualPrice = orderPrice;

        LitemallOrder order = new LitemallOrder();
        LitemallOrderGoods orderGoods = new LitemallOrderGoods();
        /*填充订单信息*/
        String orderSn = "PG" + orderService.generateOrderSn(user.getId());
        order.setUserId(user.getId());
        order.setPayOrderSn(payOrderSn);
        order.setOrderSn(orderSn);
        order.setOrderStatus(OrderUtil.STATUS_CREATE);
        order.setMessage(message);
        //收货信息
        order.setConsignee(consignee);
        order.setMobile(mobile);
        order.setAddress(detailedAddress);
        //价格
        order.setFreightPrice(freightPrice);
        order.setGoodsPrice(GoodsTotalPrice);
        order.setOrderPrice(orderPrice);
        order.setActualPrice(actualPrice);
        order.setCouponPrice(couponPrice);
        //订单分润所属加盟商
        order.setAllianceUserid(allianceUserId);
        //订单供货工厂
        order.setPlantId(productFactory.getFactoryId());
        orderService.add(order);
        /*填充订单商品信息*/
        orderGoods.setOrderSn(orderSn);
        orderGoods.setGoodsId(goods.getId());//商品ID
        orderGoods.setGoodsCodeId(goods.getGoodsCodeId());//商品编码
        orderGoods.setProductId(buyOrderGoodsVo.getSpecCodeId());//规格编码
        orderGoods.setGoodsName(goods.getName());//名称
        orderGoods.setPicUrl(specodeProduct.getUrl());//规格图片
        orderGoods.setPrice(specodeProduct.getShopPrice());
        orderGoods.setNumber(buyOrderGoodsVo.getNumber().shortValue());
        orderGoods.setSpecifications(specodeProduct.getSpecsDesc());
        orderGoods.setCouponPrice(couponPrice);
        orderGoods.setGoodsCostPrice(productFactory.getCostPrice());
        orderGoods.setPlantId(productFactory.getFactoryId());
        orderGoodsService.add(orderGoods);

        //优惠券扭转已使用
        if (null != couponUser) {
            couponUser.setPayOrderSn(payOrderSn);
            couponUser.setUsedTime(LocalDateTime.now());
            couponUser.setStatus(CouponUserConstant.STATUS_USED);
            couponUserService.update(couponUser);
        }
        // 订单支付超期任务
        taskService.addTask(new OrderUnpaidTask(order.getPayOrderSn()));
        return ResponseUtil.ok(payOrderSn);
    }

    /**
     * 查询订单归属的加盟商，优先按邀请链，邀请立案没有，就按地址，还没有就默认为平台
     *
     * @param userId
     * @param address
     * @return
     */
    private Integer matchingAlliance(Integer userId, String address) {
        /*---------------查询订单归属的加盟商-----------------*/
        //查当前关系链上的加盟商
        LitemallNodeRelation nodeRelation = nodeRelationService.queryAllianceByUserId(userId);
        if (ObjectUtils.isNotEmpty(nodeRelation)) {
            //不为null就是该加盟商
            return nodeRelation.getAncestor();
        } else {
            //为空就按地址查
            address = address.substring(0, address.lastIndexOf("-"));
            LitemallAlliance alliance = userService.selAllianceByAddress(address);
            //查不到加盟商就归平台
            return alliance != null ? alliance.getUserId() : -1;
        }
    }

    /**
     * 取消订单
     * <p>
     * 1. 检测当前订单是否能够取消；
     * 2. 设置订单取消状态；
     * 3. 商品货品库存恢复；
     * 4. 返还优惠券；
     *
     * @param userId 用户ID
     * @param body   订单信息，{ orderId：xxx }
     * @return 取消订单操作结果
     */
    @Transactional
    public Object cancel(Integer userId, String body) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        //取消订单需要把统一下单的所有子订单一起取消
        String payOrderSn = JacksonUtil.parseString(body, "payOrderSn");
        if (StringUtils.isEmpty(payOrderSn)) {
            return ResponseUtil.badArgument();
        }
        LitemallUser user = userService.findById(userId);
        List<LitemallOrder> orderList = orderService.findByPaySn(payOrderSn, userId);
        if (CollectionUtils.isEmpty(orderList)) {
            return ResponseUtil.badArgumentValue();
        }
        BigDecimal couponPrice = new BigDecimal(0);
        for (LitemallOrder o : orderList) {
            if (!o.getUserId().equals(userId)) {
                return ResponseUtil.badArgumentValue();
            }
            couponPrice = couponPrice.add(o.getCouponPrice());
            // 检测是否能够取消
            OrderHandleOption handleOption = OrderUtil.build(o);
            if (!handleOption.isCancel()) {
                return ResponseUtil.fail(ORDER_INVALID_OPERATION, "订单不能取消");
            }
            // 设置订单已取消状态
            o.setOrderStatus(OrderUtil.STATUS_CANCEL);
            o.setEndTime(LocalDateTime.now());
            if (orderService.updateById(o) == 0) {
                throw new RuntimeException("更新数据已失效");
            }
            // 商品货品数量增加
            List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOsn(o.getOrderSn());
            for (LitemallOrderGoods orderGoods : orderGoodsList) {
                Integer specCodeId = orderGoods.getProductId();
                Integer goodsCodeId = orderGoods.getGoodsCodeId();
                Short number = orderGoods.getNumber();
                Integer plantId = orderGoods.getPlantId();
                if (productFactoryService.addStockFactory(goodsCodeId, specCodeId, plantId, number) == 0) {
                    throw new RuntimeException("规格编码工厂表库存增加失败");
                }
                if (productFactoryService.addStockSpecode(goodsCodeId, specCodeId, number) == 0) {
                    throw new RuntimeException("规格编码货品表库存增加失败");
                }
                //库存记录表
                stockRecordService.add(orderGoods.getProductId(), orderGoods.getPlantId(), Integer.valueOf(orderGoods.getNumber()), 2, "取消订单", user.getNickname());
            }
        }
        if (couponPrice.compareTo(BigDecimal.ZERO) == 1) {
            releaseCoupon(payOrderSn, userId);
        }
        return ResponseUtil.ok();
    }

    /**
     * 付款订单的预支付会话标识
     * <p>
     * 1. 检测当前订单是否能够付款
     * 2. 微信商户平台返回支付订单ID
     * 3. 设置订单付款状态
     *
     * @param userId 用户ID
     * @param body   订单信息，{ orderId：xxx }
     * @return 支付订单ID
     */
    @Transactional
    public Object prepay(Integer userId, String body, HttpServletRequest request) {
        String logPrefix = "【微信支付统一下单】";
        logger.info(logPrefix);
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        String payOrderSn = JacksonUtil.parseString(body, "payOrderSn");
        if (StringUtils.isEmpty(payOrderSn)) {
            return ResponseUtil.badArgument();
        }
        List<LitemallOrder> orderList = orderService.findByPaySn(payOrderSn, userId);
        if (CollectionUtils.isEmpty(orderList)) {
            return ResponseUtil.badArgumentValue("获取订单信息失败");
        }
        BigDecimal totalAmount = new BigDecimal(0);
        StringBuilder wxBody = new StringBuilder("订单");
        wxBody.append(payOrderSn);
        for (LitemallOrder order : orderList) {
            // 检测是否能够取消
            OrderHandleOption handleOption = OrderUtil.build(order);
            if (!handleOption.isPay()) {
                return ResponseUtil.fail(ORDER_INVALID_OPERATION, "订单不能支付");
            }
            totalAmount = totalAmount.add(order.getActualPrice());
        }
        logger.info("下单总金额运算：" + totalAmount.toString());
        WxPayUnifiedOrderResult wxPayUnifiedOrderResult;
        try {
            WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
            orderRequest.setOutTradeNo(payOrderSn);

            orderRequest.setBody(wxBody.toString());
            // 元转成分
            int fee = 0;
            BigDecimal actualPrice = totalAmount;
            fee = actualPrice.multiply(new BigDecimal(100)).intValue();
            orderRequest.setTotalFee(fee);
            orderRequest.setTradeType("APP");
            orderRequest.setSpbillCreateIp(IpUtil.getIpAddr(request));
            wxPayUnifiedOrderResult = wxPayService.unifiedOrder(orderRequest);
            logger.info(logPrefix + " >>> 下单成功,响应参数：" + wxPayUnifiedOrderResult.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.fail(ORDER_PAY_FAIL, "订单不能支付");
        }
        return ResponseUtil.ok(wxReturnInfo(payOrderSn, wxPayUnifiedOrderResult.getPrepayId()));
    }


    /**
     * 微信付款成功或失败回调接口
     * <p>
     * 1. 检测当前订单是否是付款状态;
     * 2. 设置订单付款成功状态相关信息;
     * 3. 响应微信商户平台.
     *
     * @param request  请求内容
     * @param response 响应内容
     * @return 操作结果
     */
    @Transactional
    public Object payNotify(HttpServletRequest request, HttpServletResponse response) {
        String xmlResult = null;
        try {
            xmlResult = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        } catch (IOException e) {
            e.printStackTrace();
            return WxPayNotifyResponse.fail(e.getMessage());
        }

        WxPayOrderNotifyResult result = null;
        try {
            result = wxPayService.parseOrderNotifyResult(xmlResult);

            if (!WxPayConstants.ResultCode.SUCCESS.equals(result.getResultCode())) {
                logger.error(xmlResult);
                throw new WxPayException("微信通知支付失败！");
            }
            if (!WxPayConstants.ResultCode.SUCCESS.equals(result.getReturnCode())) {
                logger.error(xmlResult);
                throw new WxPayException("微信通知支付失败！");
            }
        } catch (WxPayException e) {
            e.printStackTrace();
            return WxPayNotifyResponse.fail(e.getMessage());
        }

        logger.info("处理腾讯支付平台的订单支付");
        logger.info(result);

        String payOrderSn = result.getOutTradeNo();
        String payId = result.getTransactionId();

        // 分转化成元
        String totalFee = BaseWxPayResult.fenToYuan(result.getTotalFee());
        List<LitemallOrder> orderList = orderService.findByPaySn(payOrderSn);
        if (CollectionUtils.isEmpty(orderList)) {
            return WxPayNotifyResponse.fail("订单不存在 payOrderSn=" + payOrderSn);
        }

        BigDecimal totalAmount = new BigDecimal(0);

        StringBuilder wxBody = new StringBuilder("订单");
        wxBody.append(payOrderSn);
        for (LitemallOrder order : orderList) {
            // 检查这个订单是否已经处理过
            if (OrderUtil.hasPayed(order)) {
                return WxPayNotifyResponse.success("订单已经处理成功!");
            }
            totalAmount = totalAmount.add(order.getActualPrice());
        }
        logger.info("下单总金额运算：" + totalAmount.toString());

        // 检查支付订单金额
        if (!totalFee.equals(totalAmount.toString())) {
            return WxPayNotifyResponse.fail(payOrderSn + " : 支付金额不符合 totalFee=" + totalFee);
        }
        for (LitemallOrder order : orderList) {
            order.setPayId(payId);
            order.setPayType(PayType.WX_APP);
            order.setPayTime(LocalDateTime.now());
            order.setOrderStatus(OrderUtil.STATUS_PAY);
            if (orderService.updateById(order) == 0) {
                return WxPayNotifyResponse.fail("更新数据已失效");
            }

        }
        //增加商品销量
        addSales(payOrderSn);
        // 取消订单超时未支付任务
        taskService.removeTask(new OrderUnpaidTask(payOrderSn));
        return WxPayNotifyResponse.success("处理成功!");
    }


    /**
     * 确认收货
     * <p>
     * 1. 检测当前订单是否能够确认收货；
     * 2. 设置订单确认收货状态。
     *
     * @param userId 用户ID
     * @param body   订单信息，{ orderId：xxx }
     * @return 订单操作结果
     */
    public Object confirm(Integer userId, String body) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        if (orderId == null) {
            return ResponseUtil.badArgument();
        }
        //TODO
        LitemallOrder order = orderService.findById(userId, orderId);
        if (order == null) {
            return ResponseUtil.badArgument();
        }
        if (!order.getUserId().equals(userId)) {
            return ResponseUtil.badArgumentValue();
        }

        OrderHandleOption handleOption = OrderUtil.build(order);
        if (!handleOption.isConfirm()) {
            return ResponseUtil.fail(ORDER_INVALID_OPERATION, "订单不能确认收货");
        }
        order.setOrderStatus(OrderUtil.STATUS_CONFIRM);
        order.setConfirmTime(LocalDateTime.now());
        order.setEndTime(LocalDateTime.now().plusDays(SystemConfig.getLitemallOrderClose()));
        if (orderService.updateById(order) == 0) {
            return ResponseUtil.updatedDateExpired();
        }
        return ResponseUtil.ok();
    }


    /****************************************************************代码分隔******************************************************/
    /**
     * 加盟商查看订单详情 脱敏
     *
     * @param userId       用户ID
     * @param orderGoodsId 订单子表ID
     * @return 订单详情
     */
    public Object insensitiveDetail(Integer userId, Integer orderGoodsId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        LitemallUser user = userService.findById(userId);
        if (user.getUserLevel() == 0) {
            return ResponseUtil.fail(-1, "当前用户非加盟商");
        }
        LitemallAlliance alliance = userService.selAlliance(user.getId());
        // 订单信息
        LitemallOrderGoods orderGoods = orderGoodsService.findById(orderGoodsId);
        if (null == orderGoods) {
            return ResponseUtil.fail(ORDER_UNKNOWN, "订单不存在");
        }
        // 订单信息
        LitemallOrder order = orderService.findBySn(orderGoods.getOrderSn());
        if (!userId.equals(order.getAllianceUserid())) {
            return ResponseUtil.fail(-1, "该订单与当前用户无关，无权查看");
        }
        if (!order.getOrderStatus().equals(OrderUtil.STATUS_CLOSE)) {
            return ResponseUtil.fail(-1, "当前非结算订单");
        }

        Map<String, Object> orderVo = new HashMap<String, Object>();
        orderVo.put("id", order.getId());
        orderVo.put("orderSn", order.getOrderSn());
        orderVo.put("message", order.getMessage());
        orderVo.put("addTime", order.getAddTime());
        /******脱敏信息******/
        orderVo.put("consignee", DesensitizationUtil.chineseName(order.getConsignee()));
        orderVo.put("mobile", DesensitizationUtil.mobilePhone(order.getMobile()));
        orderVo.put("address", DesensitizationUtil.address(order.getAddress(), order.getAddress().length() - order.getAddress().lastIndexOf("-")));
        /******费用******/
        orderVo.put("goodsPrice", order.getGoodsPrice());
        orderVo.put("freightPrice", order.getFreightPrice());
        orderVo.put("couponPrice", order.getCouponPrice());
        orderVo.put("actualPrice", order.getActualPrice());

        /******商品内容******/
        List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByPlantIdOid(orderGoods.getPlantId(), order.getOrderSn());

        Map<String, Object> result = new HashMap<>();
        result.put("orderInfo", orderVo);
        result.put("orderGoods", orderGoodsList);
        return ResponseUtil.ok(result);

    }


    public Object queryExpressInfo(Integer userId, Integer orderId) {
        // 订单信息
        LitemallOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.fail(-1, "订单不存在 id=" + orderId);
        }
        if (!order.getUserId().equals(userId)) {
            return ResponseUtil.badArgumentValue();
        }
        if (!order.getDeliveryStatus()) {
            return ResponseUtil.fail(-1, "订单暂无物流信息 id=" + orderId);
        }
        String mobile = order.getMobile();
        ExpressInfo express = expressService.getExpressInfo(mobile.substring(mobile.length() - 4), order.getShipSn());
        return ResponseUtil.ok(express);
    }


    public Object reminderShipment(Integer userId, String payOrderSn) {
        //根据订单ID查订单数据，判断是否可以提醒发货
        List<LitemallOrder> orderList = orderService.findByPaySn(payOrderSn, userId);
        if (CollectionUtils.isEmpty(orderList)) {
            return ResponseUtil.badArgument();
        }
        List<LitemallOrder> shipmentOrderList = orderList.stream().filter(p -> p.getOrderStatus() == OrderUtil.STATUS_PAY).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(shipmentOrderList)) {
            return ResponseUtil.ok();
        } else {
            List<LitemallOrder> updateOrderList = shipmentOrderList.stream().filter(p -> !p.getReminderShipment()).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(updateOrderList)) {
                orderService.reminderShipment(updateOrderList.stream().map(p -> p.getId()).collect(Collectors.toList()));
            }
            return ResponseUtil.ok();
        }
    }

 /*   public Object testPay(Integer userId, String payOrderSn, String payType) {
        //return ResponseUtil.fail(ORDER_INVALID_OPERATION, "订单不能提醒发货");
        List<LitemallOrder> orderList = orderService.findByPaySn(payOrderSn, userId);
        if (CollectionUtils.isEmpty(orderList)) {
            return ResponseUtil.badArgumentValue();
        }
        String payId = payType + orderService.generateOrderSn(userId);
        LocalDateTime payTime = LocalDateTime.now();
        for (LitemallOrder order : orderList) {
            order.setPayId(payId);
            order.setPayType(payType);
            order.setPayTime(payTime);
            order.setActualPrice(order.getOrderPrice());
            order.setOrderStatus(OrderUtil.STATUS_PAY);
            if (orderService.updateById(order) == 0) {
                return ResponseUtil.updatedDataFailed();
            }
        }
        return ResponseUtil.ok();
    }*/

    public Object alipay(Integer userId, String body, HttpServletRequest request) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        String payOrderSn = JacksonUtil.parseString(body, "payOrderSn");
        if (StringUtils.isBlank(payOrderSn)) {
            return ResponseUtil.badArgument();
        }

        List<LitemallOrder> orderList = orderService.findByPaySn(payOrderSn, userId);
        if (CollectionUtils.isEmpty(orderList)) {
            return ResponseUtil.badArgument();
        }
        BigDecimal totalAmount = new BigDecimal(0);
        StringBuilder alipayBody = new StringBuilder("订单");
        alipayBody.append(payOrderSn);
        String subject = "商品下单";
        for (LitemallOrder order : orderList) {
            // 检测是否能够取消
            OrderHandleOption handleOption = OrderUtil.build(order);
            if (!handleOption.isPay()) {
                return ResponseUtil.fail(ORDER_INVALID_OPERATION, "订单不能支付");
            }
            totalAmount = totalAmount.add(order.getActualPrice());
            logger.info("下单总金额运算：" + totalAmount.toString());
        }
        AlipayTradeAppPayResponse result = alipayService.doAliPayMobileReq(payOrderSn, totalAmount, alipayBody.toString(), subject);
        if (null == result) {
            return ResponseUtil.fail(-1, "调用支付宝失败");
        }
        return ResponseUtil.ok(result.getBody());
    }


    @Transactional
    public void alipayNotify(String out_trade_no, String trade_no, String total_amount) throws RuntimeException {
        List<LitemallOrder> orderList = orderService.findByPaySnAndStatus(out_trade_no, OrderUtil.STATUS_CREATE);
        if (CollectionUtils.isEmpty(orderList)) {
            throw new RuntimeException("业务订单不存在,payOrderSn" + out_trade_no);
        }
        BigDecimal totalOrderPrice = new BigDecimal("0.00");
        for (LitemallOrder order : orderList) {
            totalOrderPrice = totalOrderPrice.add(order.getActualPrice());
        }
        BigDecimal totalAmount = new BigDecimal(total_amount);
        if (totalAmount.compareTo(totalOrderPrice) != 0) {
            throw new RuntimeException("支付宝回调通知,payOrderSn=" + out_trade_no + "{}金额不匹配,totalAmount=" + totalAmount + "需要支付totalActualPrice=" + totalOrderPrice);
        }
        for (LitemallOrder order : orderList) {
            order.setPayType(PayType.ALIPAY_MOBILE);
            order.setPayTime(LocalDateTime.now());
            order.setPayId(trade_no);
            order.setOrderStatus(OrderUtil.STATUS_PAY);
            if (orderService.updateById(order) == 0) {
                throw new RuntimeException("支付宝回调通知,payOrderSn=" + out_trade_no + "{}更新数据失败");
            }
        }
        //增加商品销量
        addSales(out_trade_no);
        // 取消订单超时未支付任务
        taskService.removeTask(new OrderUnpaidTask(out_trade_no));
    }

    //主订单按比例平摊优惠金额

    /**
     * 按订单金额占比，划分每个订单应该优惠多少金额
     *
     * @param orderList        多个订单
     * @param totalCouponPrice
     * @return
     */
    public Map<String, BigDecimal> orderDiscount(List<LitemallOrder> orderList, BigDecimal totalCouponPrice) {
        Map<String, BigDecimal> result = new HashMap<>();
        BigDecimal totalGoodsPrice = new BigDecimal("0.00");//订单总额
        BigDecimal surplusCouponPrice = totalCouponPrice;//剩余优惠金额
        int number = 0;
        for (LitemallOrder order : orderList) {
            totalGoodsPrice = totalGoodsPrice.add(order.getGoodsPrice());
        }
        for (LitemallOrder order : orderList) {
            //当前订单优惠金额
            BigDecimal couponPrice = new BigDecimal(0);
            number++;
            if (number == orderList.size()) {
                couponPrice = surplusCouponPrice;
            } else {
                couponPrice = order.getGoodsPrice().divide(totalGoodsPrice, 2, RoundingMode.HALF_UP).multiply(totalCouponPrice).setScale(2, RoundingMode.HALF_UP);
            }
            result.put(order.getOrderSn(), couponPrice);
            surplusCouponPrice = surplusCouponPrice.subtract(couponPrice);
        }
        return result;
    }

    //子订单按比例平摊优惠金额
    public Map<Integer, BigDecimal> orderGoodsDiscount(List<LitemallOrderGoods> orderGoodsList, BigDecimal
            totalCouponPrice) {
        Map<Integer, BigDecimal> result = new HashMap<>();
        BigDecimal totalOrderPrice = new BigDecimal("0.00");//订单总额
        BigDecimal surplusCouponPrice = totalCouponPrice;//剩余优惠金额
        int number = 0;
        for (LitemallOrderGoods orderGoods : orderGoodsList) {
            totalOrderPrice = totalOrderPrice.add(orderGoods.getPrice().multiply(new BigDecimal(orderGoods.getNumber())));
        }
        for (LitemallOrderGoods orderGoods : orderGoodsList) {
            //当前订单优惠金额
            BigDecimal couponPrice = new BigDecimal("0.00");
            number++;
            if (number == orderGoodsList.size()) {
                couponPrice = surplusCouponPrice;
            } else {
                couponPrice = orderGoods.getPrice().multiply(new BigDecimal(orderGoods.getNumber())).divide(totalOrderPrice, 2, RoundingMode.HALF_UP).multiply(totalCouponPrice).setScale(2, RoundingMode.HALF_UP);
            }
            result.put(orderGoods.getId(), couponPrice);
            surplusCouponPrice = surplusCouponPrice.subtract(couponPrice);
        }
        return result;
    }


    //重写lamdba求和调用
    private static BigDecimal ifNullSet0(BigDecimal in) {
        if (in != null) {
            return in;
        }
        return BigDecimal.ZERO;
    }

    //重写lamdba求和调用
    private static BigDecimal sum(BigDecimal... in) {
        BigDecimal result = BigDecimal.ZERO;
        for (int i = 0; i < in.length; i++) {
            result = result.add(ifNullSet0(in[i]));
        }
        return result;
    }


    /**
     * 取消订单/退款返还优惠券
     * <br/>
     *
     * @param payOrderSn
     * @param userId
     * @return void
     * @author Tyson
     * @date 2020/6/8/0008 1:41
     */
    public void releaseCoupon(String payOrderSn, Integer userId) {
        List<LitemallCouponUser> couponUsers = couponUserService.queryByPayOrderSnAndUserId(payOrderSn, userId);
        for (LitemallCouponUser couponUser : couponUsers) {
            // 优惠券状态设置为可使用
            couponUser.setStatus(CouponUserConstant.STATUS_USABLE);
            couponUser.setUpdateTime(LocalDateTime.now());
            couponUserService.update(couponUser);
        }
    }

    public Map<String, Object> wxReturnInfo(String payOrderSn, String prepayId) {
        Map<String, Object> map = new HashMap<>();

        Map<String, String> payInfo = new HashMap<>();
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonceStr = String.valueOf(System.currentTimeMillis());
        // APP支付绑定的是微信开放平台上的账号，APPID为开放平台上绑定APP后发放的参数
        String appId = properties.getAppId();
        Map<String, String> configMap = new HashMap<>();
        // 此map用于参与调起sdk支付的二次签名,格式全小写，timestamp只能是10位,格式固定，切勿修改
        String partnerId = properties.getMchId();
        configMap.put("prepayid", prepayId);
        configMap.put("partnerid", partnerId);
        String packageValue = "Sign=WXPay";
        configMap.put("package", packageValue);
        configMap.put("timestamp", timestamp);
        configMap.put("noncestr", nonceStr);
        configMap.put("appid", appId);
        // 此map用于客户端与微信服务器交互
        payInfo.put("sign", SignUtils.createSign(configMap, "MD5", properties.getMchKey(), null));
        payInfo.put("prepayId", prepayId);
        payInfo.put("partnerId", partnerId);
        payInfo.put("appId", appId);
        payInfo.put("packageValue", packageValue);
        payInfo.put("timeStamp", timestamp);
        payInfo.put("nonceStr", nonceStr);
        map.put("payParams", payInfo);
        map.put("payOrderSn", payOrderSn);
        map.put("prepayId", prepayId);
        return map;
    }

    @Async
    public void addSales(String payOrderSn) {
        logger.info("异步方法:addSales, 执行状态：Start...........");
        try {
            List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryGoodsListAddSales(payOrderSn);
            for(LitemallOrderGoods orderGoods : orderGoodsList) {
                logger.info("处理订单商品：" + orderGoods.getGoodsName() + "，添加销量数：" + orderGoods.getNumber());
                LitemallGoods goods = goodsService.findById(orderGoods.getGoodsId());
                //存该商品总销量
                goods.setSales(goods.getSales() + Integer.valueOf(orderGoods.getNumber()));
                goodsService.updateById(goods);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            logger.info("异步方法:addSales, 执行状态：异常...........");
        }
        logger.info("异步方法:addSales, 执行状态：end...........");
    }


}
