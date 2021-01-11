package org.linlinjava.litemall.admin.service;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.admin.dto.GoodsAllinone;
import org.linlinjava.litemall.core.qcode.QCodeService;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.dto.LitemallGoodsDto;
import org.linlinjava.litemall.db.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.linlinjava.litemall.admin.util.AdminResponseCode.GOODS_NAME_EXIST;

@Service
public class AdminGoodsService {
    private final Log logger = LogFactory.getLog(AdminGoodsService.class);

    @Autowired
    private LitemallGoodsService goodsService;

    @Autowired
    private LitemallGoodsAttributeService attributeService;
    @Autowired
    private LitemallProductFactoryService productFactoryService;
    @Autowired
    private LitemallCategoryService categoryService;

    @Autowired
    private AdminGoodsCodeService codeService;
    @Autowired
    private LitemallGoodsCodeService goodsCodeService;

    @Autowired
    private LitemallCartService cartService;
    @Autowired
    private QCodeService qCodeService;
    @Autowired
    private LitemallSystemConfigService systemConfigService;
    @Autowired
    private LitemallOrderGoodsService orderGoodsService;
    @Autowired
    private LogHelper logHelper;
    @Autowired
    private LitemallSpecodeProductService specodeProductService;

    private LitemallGoodsDto ModelToDto(LitemallGoods goods, String freight) {
        LitemallGoodsDto dto = new LitemallGoodsDto();
        dto.setId(goods.getId());
        dto.setGoodsSn(goods.getGoodsSn());
        dto.setName(goods.getName());
        dto.setCategoryId(goods.getCategoryId());
        dto.setBrandId(goods.getBrandId());
        dto.setGallery(goods.getGallery());
        dto.setKeywords(goods.getKeywords());
        dto.setBrief(goods.getBrief());
        dto.setOnSale(goods.getIsOnSale());
        dto.setSortOrder(goods.getSortOrder());
        dto.setPicUrl(goods.getPicUrl());
        dto.setShareUrl(goods.getShareUrl());
        dto.setNew(goods.getIsNew());
        dto.setHot(goods.getIsHot());
        dto.setUnit(goods.getUnit());
        dto.setCounterPrice(goods.getCounterPrice());
        dto.setRetailPrice(goods.getRetailPrice());
        dto.setAddTime(goods.getAddTime());
        dto.setDeleted(goods.getDeleted());
        dto.setDetail(goods.getDetail());
        dto.setGoodsCodeId(goods.getGoodsCodeId());
        if ("0".equals(freight)) {
            dto.setFreight("免运费");
        } else {
            dto.setFreight(freight);
        }
        dto.setInventory(productFactoryService.selInventory(goods.getGoodsCodeId()));
        dto.setSales(String.valueOf(orderGoodsService.countByGoodsId(goods.getGoodsSn())));
        return dto;
    }

    public Object list(Integer goodsId, String goodsCode, String goodsName,String categoryName,
                       Boolean isHome,Boolean isOnSale,Integer page, Integer limit) {
        List<Map<String,Object>> goodsList = goodsService.queryList( goodsId,  goodsCode,  goodsName,  categoryName,  isHome, isOnSale,page,  limit);
        return ResponseUtil.okList(goodsList);
    }

    public Object listByGoodsId(List<Integer> goodsIds, Integer pageNum, Integer pageSize) {

        String freight = systemConfigService.selByKey("litemall_express_freight_value");
        List<LitemallGoods> goodsList = goodsService.listByGoodsId(goodsIds);

        List<LitemallGoodsDto> dtoList = goodsList.stream().map(item -> ModelToDto(item, freight)).collect(Collectors.toList());

        //创建Page类
        Page page = new Page(pageNum, pageSize);
        //为Page类中的total属性赋值
        page.setTotal(dtoList.size());
        //计算当前需要显示的数据下标起始值
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, dtoList.size());
        //从链表中截取需要显示的子链表，并加入到Page
        page.addAll(dtoList.subList(startIndex, endIndex));
        //以Page创建PageInfo
        PageInfo pageInfo = new PageInfo<>(page);

        return ResponseUtil.okList(pageInfo);
    }

    public Object activityGoods(Integer activityId, String goodsCode, String goodsName, Integer pageNum, Integer pageSize) {
        String freight = systemConfigService.selByKey("litemall_express_freight_value");
        if (StringUtils.isNotBlank(goodsName)) {
            goodsName = "%" + goodsName + "%";
        }
        if (StringUtils.isNotBlank(goodsCode)) {
            goodsCode = "%" + goodsCode + "%";
        }
        PageHelper.startPage(pageNum, pageSize);
        List<LitemallGoodsDto> dtoList = goodsService.adminActivityGoods(activityId, goodsCode, goodsName);
        dtoList.stream().forEach(item -> item.setFreight(freight));
        return ResponseUtil.okList(dtoList);
    }

    private Object validate(GoodsAllinone goodsAllinone) {
        LitemallGoods goods = goodsAllinone.getGoods();
        String name = goods.getName();
        BigDecimal retailPrice = goods.getRetailPrice(); //零售价
        BigDecimal counterPrice = goods.getCounterPrice(); //专柜价
        if (BigDecimal.ZERO.compareTo(retailPrice) == 1 || BigDecimal.ZERO.compareTo(counterPrice) == 1 || retailPrice.compareTo(counterPrice) == 1) {
            return ResponseUtil.badArgumentValue("价格不能小于0且原价必须大于售价");
        }
        //商品名，简介，详情，主图片，展示图,关键词不能为空
        if (StringUtils.isAnyBlank(name, goods.getBrief(), goods.getDetail(), goods.getPicUrl(), goods.getGallery(), goods.getKeywords())) {
            return ResponseUtil.badArgument();
        }
        //视频校验
       /* if (StringUtils.isNotBlank(goods.getShareUrl())&&) {
            return ResponseUtil.badArgument();
        }*/
        Integer goodCodeId = goods.getGoodsCodeId();
        if (null == goodCodeId || null == goodsCodeService.findById(goodCodeId)) {
            return ResponseUtil.badArgumentValue("商品编码不存在");
        }
        if (goods.getIsOnSale()){
            LitemallGoods usedGoods = goodsService.queryByGoodsCodeId(goodCodeId);
            if (null != usedGoods && (null == goods.getId() || !usedGoods.getId().equals(goods.getId()))) {
                return ResponseUtil.badArgumentValue("商品编码已被在售商品:" + usedGoods.getName() + "绑定，不支持多在售商品绑定同一个商品编码");
            }
        }
        List<LitemallSpecodeProduct> specodeProducts = specodeProductService.queryByGid(goodCodeId);
        if (CollectionUtils.isEmpty(specodeProducts)) {
            return ResponseUtil.badArgumentValue("该商品编码下暂无规格，请先维护好规格！");
        }
        // 分类可以不设置，如果设置则需要验证分类存在
        Integer categoryId = goods.getCategoryId();
        if (null != categoryId && null == categoryService.findById(categoryId)) {
            return ResponseUtil.badArgumentValue("类目不能为空");
        }

        LitemallGoodsAttribute[] attributes = goodsAllinone.getAttributes();
        List<LitemallGoodsAttribute> attributeList = Arrays.asList(attributes);
        if (CollectionUtils.isNotEmpty(attributeList)) {
            for (LitemallGoodsAttribute attribute : attributes) {
                String attr = attribute.getAttribute();
                String value = attribute.getValue();
                if (StringUtils.isAnyBlank(attr, value)) {
                    return ResponseUtil.badArgument();
                }
            }
        }
        return null;
    }

    /**
     * 编辑商品
     * <p>
     * NOTE：
     * 由于商品涉及到四个表，特别是litemall_goods_product表依赖litemall_goods_specification表，
     * 这导致允许所有字段都是可编辑会带来一些问题，因此这里商品编辑功能是受限制：
     * （1）litemall_goods表可以编辑字段；
     * （2）litemall_goods_specification表只能编辑pic_url字段，其他操作不支持；
     * （3）litemall_goods_product表只能编辑price, number和url字段，其他操作不支持；
     * （4）litemall_goods_attribute表支持编辑、添加和删除操作。
     * <p>
     * NOTE2:
     * 前后端这里使用了一个小技巧：
     * 如果前端传来的update_time字段是空，则说明前端已经更新了某个记录，则这个记录会更新；
     * 否则说明这个记录没有编辑过，无需更新该记录。
     * <p>
     * NOTE3:
     * （1）购物车缓存了一些商品信息，因此需要及时更新。
     * 目前这些字段是goods_sn, goods_name, price, pic_url。
     * （2）但是订单里面的商品信息则是不会更新。
     * 如果订单是未支付订单，此时仍然以旧的价格支付。
     */
    @Transactional
    public Object update(GoodsAllinone goodsAllinone) {
        Object error = validate(goodsAllinone);
        if (error != null) {
            return error;
        }
        LitemallGoods goods = goodsAllinone.getGoods();
        LitemallGoods checkGoods=goodsService.findById(goods.getId());
        if (checkGoods.getIsOnSale()) {
            return ResponseUtil.fail(-1, "在售商品不支持修改，请先下架商品");
        }
        if (null==checkGoods) {
            return ResponseUtil.badArgumentValue( "编辑商品不存在");
        }
        LitemallGoodsAttribute[] attributes = goodsAllinone.getAttributes();
        // 商品基本信息表litemall_goods
        if (goodsService.updateById(goods) == 0) {
            throw new RuntimeException("更新数据失败");
        }
        // 商品参数表litemall_goods_attribute
        for (LitemallGoodsAttribute attribute : attributes) {
            if (attribute.getId() == null || attribute.getId().equals(0)) {
                attribute.setGoodsId(goods.getId());
                attributeService.add(attribute);
            } else if (attribute.getDeleted()) {
                attributeService.deleteById(attribute.getId());
            } else if (attribute.getUpdateTime() == null) {
                attributeService.updateById(attribute);
            }
        }
        logHelper.logGeneralSucceed("编辑商品");
        return ResponseUtil.ok();
    }

    @Transactional
    public Object delete(LitemallGoods goods) {
        Integer id = goods.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        Integer gid = goods.getId();
        goodsService.deleteById(gid);
        attributeService.deleteByGid(gid);
        cartService.deleteByGoodsId(gid);
        logHelper.logGeneralSucceed("删除商品");
        return ResponseUtil.ok();
    }

    @Transactional
    public Object create(GoodsAllinone goodsAllinone) {
        Object error = validate(goodsAllinone);
        if (error != null) {
            return error;
        }
        LitemallGoods goods = goodsAllinone.getGoods();
        LitemallGoodsAttribute[] attributes = goodsAllinone.getAttributes();
        String name = goods.getName();
        if (goodsService.checkExistByName(name)) {
            return ResponseUtil.fail(GOODS_NAME_EXIST, "商品名已经存在");
        }
        // 商品基本信息表litemall_goods
        goodsService.add(goods);

        //将生成的分享图片地址写入数据库
        String url = qCodeService.createGoodShareImage(goods.getId().toString(), goods.getPicUrl(), goods.getName());
        if (!StringUtils.isEmpty(url)) {
            goods.setShareUrl(url);
            if (goodsService.updateById(goods) == 0) {
                throw new RuntimeException("更新数据失败");
            }
        }
        // 商品参数表litemall_goods_attribute
        for (LitemallGoodsAttribute attribute : attributes) {
            attribute.setGoodsId(goods.getId());
            attributeService.add(attribute);
        }

        logHelper.logGeneralSucceed("添加商品");
        return ResponseUtil.ok();
    }


    public Object detail(Integer id) {
        LitemallGoods goods = goodsService.findById(id);
        List<LitemallProductFactory> productFactories = productFactoryService.queryByGid(id);
        List<LitemallSpecodeProduct> specodeProducts = specodeProductService.queryByGid(id);
        List<LitemallGoodsAttribute> attributes = attributeService.queryByGid(id);
        Integer categoryId = goods.getCategoryId();
        LitemallCategory category = categoryService.findById(categoryId);
        Map<String, Integer> categoryIds = new HashMap<>();
        if (category != null) {
            categoryIds.put("L1", category.getPid());
            categoryIds.put("L2", category.getId());
        }
        LitemallGoodsCode goodsCode = goodsCodeService.findById(goods.getGoodsCodeId());
        Map<String, Object> data = new HashMap<>();
        data.put("goods", goods);
        data.put("goodsCode", goodsCode);
        data.put("productFactories", productFactories);
        data.put("attributes", attributes);
        data.put("categoryIds", categoryIds);
        data.put("specodeProducts", specodeProducts);
        return ResponseUtil.ok(data);
    }

    /**
     * 上下架
     *
     * @param litemallGoods
     * @return
     */
    @Transactional
    public Object updateGoods(LitemallGoods litemallGoods) {
        if (litemallGoods.getIsOnSale()) {
            //上架，需求校验gcode是否被删除，gcode下规格是否还有，类目是否还在
            Integer goodsCodeId = litemallGoods.getGoodsCodeId();
            LitemallGoods usedGoods = goodsService.queryByGoodsCodeId(goodsCodeId);
            if (null != usedGoods) {
                return ResponseUtil.badArgumentValue("商品编码已被在售商品:" + usedGoods.getName() + "绑定，不支持多在售商品绑定同一个商品编码");
            }
            List<LitemallSpecodeProduct> specodeProducts = specodeProductService.queryByGid(goodsCodeId);
            if (CollectionUtils.isEmpty(specodeProducts)) {
                return ResponseUtil.badArgumentValue("该商品关联的商品编码暂无规格，请先维护好规格!");
            }
            Integer categoryId = litemallGoods.getCategoryId();
            LitemallCategory category = categoryService.findById(categoryId);
            if (null == category) {
                return ResponseUtil.badArgumentValue("该类目已被删除，请重新编辑类目");
            }
        } else {
            //根据商品ID下架所有人的购物车该商品
            cartService.batchUpdateByGoodsId(litemallGoods.getId());
        }
        return ResponseUtil.ok(goodsService.updateById(litemallGoods));
    }

    public Object hotList(String goodsCode, String goodsName, Integer page, Integer limit) {
        List<Map<String,Object>> goodsList = goodsService.queryByHot(goodsCode,goodsName,page,limit);
        return ResponseUtil.okList(goodsList);
    }
}
