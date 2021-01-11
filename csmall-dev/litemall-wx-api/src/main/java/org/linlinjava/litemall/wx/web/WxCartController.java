package org.linlinjava.litemall.wx.web;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.system.SystemConfig;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.service.*;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.linlinjava.litemall.wx.vo.BuyOrderGoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.linlinjava.litemall.wx.util.WxResponseCode.GOODS_NO_STOCK;
import static org.linlinjava.litemall.wx.util.WxResponseCode.GOODS_UNSHELVE;

/**
 * 用户购物车服务
 */
@RestController
@RequestMapping("/wx/cart")
@Validated
public class WxCartController {
    private final Log logger = LogFactory.getLog(WxCartController.class);

    @Autowired
    private LitemallCartService cartService;
    @Autowired
    private LitemallGoodsService goodsService;
    @Autowired
    private LitemallProductFactoryService productFactoryService;
    @Autowired
    private LitemallAddressService addressService;
    @Autowired
    private LitemallGrouponRulesService grouponRulesService;
    @Autowired
    private LitemallCouponService couponService;
    @Autowired
    private LitemallCouponUserService couponUserService;
    @Autowired
    private CouponVerifyService couponVerifyService;
    @Autowired
    private LitemallSpecodeProductService specsCodeProductService;


    /**
     * 用户购物车信息
     *
     * @param userId 用户ID
     * @return 用户购物车信息
     */
    @GetMapping("index")
    public Object index(@LoginUser Integer userId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }

        List<LitemallCart> list = cartService.queryByUid(userId);
        List<LitemallCart> cartList = list.stream().filter(c -> c.getIsOnSale() == true).collect(Collectors.toList());
        List<LitemallCart> deleteList = list.stream().filter(c -> c.getIsOnSale() == false).collect(Collectors.toList());
        // 如果系统检查商品已删除或已下架，则系统自动删除。
        // 更好的效果应该是告知用户商品失效，允许用户点击按钮来清除失效商品。
        for (LitemallCart cart : deleteList) {
            cartService.deleteById(cart.getId());
            logger.debug("系统自动删除失效购物车商品 goodsId=" + cart.getGoodsId() + " 规格编码specCodeId=" + cart.getSpecCodeId());
        }

        Integer goodsCount = cartList.size();
        BigDecimal goodsAmount = new BigDecimal(0.00);
        Integer checkedGoodsCount = 0;
        BigDecimal checkedGoodsAmount = new BigDecimal(0.00);
        for (LitemallCart cart : cartList) {
            goodsAmount = goodsAmount.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
            if (cart.getChecked()) {
                checkedGoodsCount += 1;
                checkedGoodsAmount = checkedGoodsAmount.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
            }
        }
        Map<String, Object> cartTotal = new HashMap<>();
        cartTotal.put("goodsCount", goodsCount);
        cartTotal.put("goodsAmount", goodsAmount);
        cartTotal.put("checkedGoodsCount", checkedGoodsCount);
        cartTotal.put("checkedGoodsAmount", checkedGoodsAmount);

        Map<String, Object> result = new HashMap<>();
        result.put("cartList", cartList);
        result.put("cartTotal", cartTotal);

        return ResponseUtil.ok(result);
    }

    /**
     * 加入商品到购物车
     * <p>
     * 如果已经存在购物车货品，则增加数量；
     * 否则添加新的购物车货品项。
     *
     * @param userId 用户ID
     * @param cart   购物车商品信息， { goodsId: xxx, productId: xxx, number: xxx }
     * @return 加入购物车操作结果
     */
    @PostMapping("add")
    public Object add(@LoginUser Integer userId, @RequestBody LitemallCart cart) {
        logger.info("cart:" + JSONObject.toJSONString(cart));
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        if (cart == null) {
            return ResponseUtil.badArgument();
        }
        Integer number = cart.getNumber().intValue();
        Integer goodsId = cart.getGoodsId();
        Integer specCodeId = cart.getSpecCodeId();
        if (!ObjectUtils.allNotNull(number, goodsId, specCodeId) || 1 > number) {
            return ResponseUtil.badArgument();
        }
        LitemallGoods goods = goodsService.findById(goodsId);
        //判断商品是否可以购买
        if (ObjectUtils.isEmpty(goods) || !goods.getIsOnSale()) {
            return ResponseUtil.fail(GOODS_UNSHELVE, "商品已下架或不存在");
        }
        //查询库存最多的供货方
        LitemallProductFactory productFactory = productFactoryService.getOneByGoodId(goods.getGoodsCodeId(), cart.getSpecCodeId());

        //判断购物车中是否存在此规格商品
        LitemallCart existCart = cartService.selByGoodsCodeId(goodsId, cart.getSpecCodeId(), userId);
        if (existCart == null) {
            //取得规格的信息,判断规格库存
            if (productFactory == null || number > productFactory.getGoodsStock()) {
                return ResponseUtil.fail(GOODS_NO_STOCK, "单个仓库，供货不足:" + number + "，如需购买，请分开下单或加购物车");
            }
            cart.setGoodsName((goods.getName()));
            cart.setGoodsCodeId(goods.getGoodsCodeId());
            LitemallSpecodeProduct specCodeProduct = specsCodeProductService.findById(cart.getSpecCodeId());
            cart.setSpecifications(specCodeProduct.getSpecsDesc());
            cart.setPicUrl(specCodeProduct.getUrl());//购物车放规格图片
            cart.setPrice(specCodeProduct.getShopPrice());
            cart.setUserId(userId);
            cart.setChecked(true);
            cartService.add(cart);
        } else {
            //取得规格的信息,判断规格库存
            int num = existCart.getNumber() + number;
            if (num > productFactory.getGoodsStock()) {
                return ResponseUtil.fail(GOODS_NO_STOCK, "单个仓库，供货不足:" + num + "，如需购买，请分开下单或加购物车");
            }
            existCart.setNumber((short) num);
            if (cartService.updateById(existCart) == 0) {
                return ResponseUtil.updatedDataFailed();
            }
        }
        Map<String, Object> data = new HashMap();
        data.put("cartCount", cartService.countByUid(userId));
        data.put("cartId", existCart != null ? existCart.getId() : cart.getId());
        return ResponseUtil.ok(data);
    }


    /**
     * 修改购物车商品货品数量
     *
     * @param userId 用户ID
     * @param cart   购物车商品信息， { id: xxx, goodsId: xxx, productId: xxx, number: xxx }
     * @return 修改结果
     */
    @PostMapping("update")
    public Object update(@LoginUser Integer userId, @RequestBody LitemallCart cart) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
//        Integer productId = cart.getProductId();
        Integer number = cart.getNumber().intValue();
        Integer goodsId = cart.getGoodsId();
        Integer id = cart.getId();
        if (!ObjectUtils.allNotNull(id, number, goodsId)) {
            return ResponseUtil.badArgument();
        }
        if (number <= 0) {
            return ResponseUtil.badArgument();
        }

        //判断是否存在该订单
        // 如果不存在，直接返回错误
        LitemallCart existCart = cartService.findById(userId, id);
        if (existCart == null) {
            return ResponseUtil.badArgumentValue();
        }

        // 判断goodsId和productId是否与当前cart里的值一致
        if (!existCart.getGoodsId().equals(goodsId)) {
            return ResponseUtil.badArgumentValue();
        }

        //判断商品是否可以购买
        LitemallGoods goods = goodsService.findById(goodsId);
        if (goods == null || !goods.getIsOnSale()) {
            return ResponseUtil.fail(GOODS_UNSHELVE, "商品已下架");
        }

        //取得规格的信息,判断规格库存
        LitemallProductFactory productFactory = productFactoryService.getOneByGoodId(Integer.valueOf(existCart.getGoodsCodeId()), existCart.getSpecCodeId());
//        LitemallGoodsProduct product = productService.findById(productId);
        if (productFactory == null || productFactory.getGoodsStock() < number) {
            return ResponseUtil.fail(GOODS_UNSHELVE, "库存不足");
        }

        existCart.setNumber(number.shortValue());
        if (cartService.updateById(existCart) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok();
    }

    /**
     * 购物车商品货品勾选状态
     * <p>
     * 如果原来没有勾选，则设置勾选状态；如果商品已经勾选，则设置非勾选状态。
     *
     * @param userId 用户ID
     * @param body   购物车商品信息， { ids: xxx, isChecked: 1/0 }
     * @return 购物车信息
     */
    @PostMapping("checked")
    public Object checked(@LoginUser Integer userId, @RequestBody String body) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        if (body == null) {
            return ResponseUtil.badArgument();
        }

        List<Integer> ids = JacksonUtil.parseIntegerList(body, "ids");
        if (ids == null) {
            return ResponseUtil.badArgument();
        }

        Integer checkValue = JacksonUtil.parseInteger(body, "isChecked");
        if (checkValue == null) {
            return ResponseUtil.badArgument();
        }
        Boolean isChecked = (checkValue == 1);

        cartService.updateCheck(userId, ids, isChecked);
        return index(userId);
    }

    /**
     * 购物车商品删除
     *
     * @param userId 用户ID
     * @param body   购物车商品信息， { ids: xxx }
     * @return 购物车信息
     * 成功则
     * {
     * errno: 0,
     * errmsg: '成功',
     * data: xxx
     * }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @PostMapping("delete")
    public Object delete(@LoginUser Integer userId, @RequestBody String body) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        if (body == null) {
            return ResponseUtil.badArgument();
        }

        List<Integer> ids = JacksonUtil.parseIntegerList(body, "ids");

        if (ids == null || ids.size() == 0) {
            return ResponseUtil.badArgument();
        }

        cartService.delete(ids, userId);
        return index(userId);
    }

    /**
     * 购物车商品数量
     * <p>
     * 如果用户没有登录，则返回空数据。
     *
     * @param userId 用户ID
     * @return 购物车商品货品数量
     */
    @GetMapping("goodscount")
    public Object goodscount(@LoginUser Integer userId) {
        if (userId == null) {
            return ResponseUtil.ok(0);
        }
        List<LitemallCart> cartList = cartService.queryByUid(userId);
        return ResponseUtil.ok(cartList.stream().filter(p -> p.getIsOnSale() == true).collect(Collectors.toList()).size());
    }

    /**
     * 购物车下单
     *
     * @param userId          用户ID
     * @param buyOrderGoodsVo 立刻购买： 三个参数都必传
     * @param buyOrderGoodsVo cartIds         购物车ID数组，购物车下单，确认订单页的数据展示
     *                        如果收货地址ID是空，则查询当前用户的默认地址。
     * @return 购物车操作结果
     */
    @PostMapping("checkout")
    public Object checkout(@LoginUser Integer userId, @RequestBody BuyOrderGoodsVo buyOrderGoodsVo) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        if (ObjectUtils.isEmpty(buyOrderGoodsVo)) {
            return ResponseUtil.badArgument();
        }
        if (CollectionUtils.isEmpty(buyOrderGoodsVo.getCartIds()) && ObjectUtils.anyNull(buyOrderGoodsVo.getGoodsId(), buyOrderGoodsVo.getNumber(), buyOrderGoodsVo.getSpecCodeId())) {
            return ResponseUtil.badArgument();
        }
        // 商品价格
        List<LitemallCart> checkedGoodsList = new ArrayList<>();
        if (CollectionUtils.isEmpty(buyOrderGoodsVo.getCartIds())){
            if (ObjectUtils.anyNull(buyOrderGoodsVo.getGoodsId(), buyOrderGoodsVo.getNumber(), buyOrderGoodsVo.getSpecCodeId())) {
                return ResponseUtil.badArgumentValue("商品id,规格，数量必传不能为空");
            }
            LitemallGoods goods = goodsService.findById(buyOrderGoodsVo.getGoodsId());
            //查询库存最多的供货方
            LitemallProductFactory productFactory = productFactoryService.getOneByGoodId(goods.getGoodsCodeId(), buyOrderGoodsVo.getSpecCodeId());
            LitemallSpecodeProduct specCodeProduct = specsCodeProductService.findById(buyOrderGoodsVo.getSpecCodeId());
            //取得规格的信息,判断规格库存
            if (productFactory == null || buyOrderGoodsVo.getNumber() > productFactory.getGoodsStock()) {
                return ResponseUtil.fail(GOODS_NO_STOCK, "单个仓库，供货不足:" + buyOrderGoodsVo.getNumber() + "，如需购买，请分开下单");
            }
            LitemallCart cart = new LitemallCart();
            cart.setGoodsName((goods.getName()));
            cart.setPicUrl(specCodeProduct.getUrl());//购物车放规格图片
            cart.setSpecifications(specCodeProduct.getSpecsDesc());
            cart.setPrice(specCodeProduct.getShopPrice());
            cart.setNumber(buyOrderGoodsVo.getNumber().shortValue());
            checkedGoodsList.add(cart);
        } else {
            checkedGoodsList = cartService.userQueryByIds(userId, buyOrderGoodsVo.getCartIds());
            if (CollectionUtils.isEmpty(checkedGoodsList)) {
                return ResponseUtil.badArgumentValue("获取购物商品失败，当前购买商品或已下架");
            }
        }

        BigDecimal checkedGoodsPrice = new BigDecimal(0.00);
        for (LitemallCart cart : checkedGoodsList) {
            checkedGoodsPrice = checkedGoodsPrice.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
        }

        // 根据订单商品总价计算运费，满88则免运费，否则8元；
        BigDecimal freightPrice = new BigDecimal(0.00);
        if (checkedGoodsPrice.compareTo(SystemConfig.getFreightLimit()) < 0) {
            freightPrice = SystemConfig.getFreight();
        }
        // 订单费用
        BigDecimal orderTotalPrice = checkedGoodsPrice.add(freightPrice).max(new BigDecimal(0.00));

        Map<String, Object> data = new HashMap<>();
        data.put("cartIds", buyOrderGoodsVo.getCartIds());
        data.put("checkedAddress", addressService.findDefault(userId));
        data.put("goodsTotalPrice", checkedGoodsPrice);
        data.put("freightPrice", freightPrice);
        data.put("orderTotalPrice", orderTotalPrice);
        data.put("orderGoods", checkedGoodsList);
        return ResponseUtil.ok(data);
    }


}
