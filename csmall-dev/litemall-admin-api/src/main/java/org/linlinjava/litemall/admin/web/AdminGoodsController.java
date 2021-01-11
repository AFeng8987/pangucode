package org.linlinjava.litemall.admin.web;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.admin.dto.GoodsAllinone;
import org.linlinjava.litemall.admin.service.AdminGoodsService;
import org.linlinjava.litemall.admin.service.LogHelper;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallGoods;
import org.linlinjava.litemall.db.service.LitemallGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/goods")
@Validated
public class AdminGoodsController {
    private final Log logger = LogFactory.getLog(AdminGoodsController.class);

    @Autowired
    private AdminGoodsService adminGoodsService;

    @Autowired
    private LitemallGoodsService litemallGoodsService;


    @Autowired
    private LogHelper logHelper;

    /**
     * 查询商品
     *
     * @param goodsId
     * @param goodsCode
     * @param goodsName
     * @param categoryName
     * @param page
     * @param limit
     * @return
     */
    @RequiresPermissions("admin:goods:list")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品列表"}, button = "商品列表")
    @GetMapping("/list")
    public Object list(Integer goodsId, String goodsCode, String goodsName,String categoryName,
                       Boolean isHome,Boolean isOnSale,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit ) {
        logger.info("商品管理查询：参数｛ID："+goodsId+"名称："+goodsName+"商品编码："+goodsCode+"类目名称："+categoryName+"}");
        return adminGoodsService.list(goodsId, goodsCode, goodsName,categoryName,isHome, isOnSale,page, limit);
    }



    /**
     * 编辑商品
     *
     * @param goodsAllinone
     * @return
     */
    @RequiresPermissions("admin:goods:update")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品列表"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody GoodsAllinone goodsAllinone) {
        return adminGoodsService.update(goodsAllinone);
    }

    /**
     * 删除商品
     *
     * @param goods
     * @return
     */
    @RequiresPermissions("admin:goods:delete")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品列表"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallGoods goods) {
        return adminGoodsService.delete(goods);
    }

    /**
     * 添加商品
     *
     * @param goodsAllinone
     * @return
     */
    @RequiresPermissions("admin:goods:create")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品添加"}, button = "商品添加")
    @PostMapping("/create")
    public Object create(@RequestBody GoodsAllinone goodsAllinone) {
        return adminGoodsService.create(goodsAllinone);
    }

    /**
     * 商品详情
     *
     * @param id
     * @return
     */
    @RequiresPermissions("admin:goods:read")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品列表"}, button = "详情")
    @GetMapping("/detail")
    public Object detail(@NotNull Integer id) {
        return adminGoodsService.detail(id);

    }

    @RequiresPermissions("admin:goods:onSale")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品列表"}, button = "上下架")
    @PostMapping("/onSale")
    public Object onSale(@RequestBody LitemallGoods litemallGoods) {
        String operation=litemallGoods.getIsOnSale()?"上架":"下架";
        if (ObjectUtils.anyNull(litemallGoods.getId(), litemallGoods.getIsOnSale())) {
            return ResponseUtil.badArgument();
        }
        LitemallGoods goods=litemallGoodsService.findById(litemallGoods.getId());
        if (ObjectUtils.isEmpty(goods)){
            return ResponseUtil.badArgument();
        }
        if (goods.getIsOnSale().equals(litemallGoods.getIsOnSale())){
            logger.info("修改---商品："+goods.getName()+"当前在售情况与修改后一致，无需改动");
            return ResponseUtil.badArgument();
        }
        logger.info("修改---商品："+goods.getName()+"当前状态："+goods.getIsOnSale()+"修改为：---"+operation);
        goods.setIsOnSale(!goods.getIsOnSale());
        logHelper.logOtherSucceed("上下架","商品:"+goods.getName()+operation);
        return adminGoodsService.updateGoods(goods);
    }


    @RequiresPermissions("admin:goods:hotSale")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品列表"}, button = "是否热卖")
    @PostMapping("/hotSale")
    public Object hotSale(@RequestBody LitemallGoods litemallGoods) {
        String operation=litemallGoods.getIsHot()?"是":"否";
        if (ObjectUtils.anyNull(litemallGoods.getId(), litemallGoods.getIsHot())) {
            return ResponseUtil.badArgumentValue("ID和操作热卖必传");
        }
        LitemallGoods goods=litemallGoodsService.findById(litemallGoods.getId());
        if (ObjectUtils.isEmpty(goods)||!goods.getIsOnSale()){
            return ResponseUtil.badArgumentValue("非在售商品不支持修改");
        }
        logger.info("修改商品："+goods.getName()+"是否热卖:"+operation);
        goods.setIsHot(litemallGoods.getIsHot());
        if (litemallGoodsService.updateById(goods)==0){
            return ResponseUtil.updatedDataFailed();
        }
        logHelper.logOtherSucceed("设置热卖","商品:"+goods.getName()+"是否热卖:"+operation);
        return ResponseUtil.ok(litemallGoods);
    }



    @RequiresPermissions("admin:goods:homeGoods")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品列表"}, button = "设置商品为首页展示")
    @PostMapping("/homeGoods")
    public Object homeGoods(@RequestBody String body) {
        List<Integer> goodsIds = JacksonUtil.parseIntegerList(body, "goodsIds");
        Boolean isHome = JacksonUtil.parseBoolean(body, "isHome");
        if (null==isHome) {
            return ResponseUtil.badArgumentValue("必传字段");
        }
        if (CollectionUtils.isEmpty(goodsIds)) {
            return ResponseUtil.badArgumentValue("要设置首页的商品数据不能为空");
        }
        logger.info("修改商品："+goodsIds.toString()+"为首页展示");
        if (litemallGoodsService.updateHomeGoodsByIds(goodsIds,isHome)==0){
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok();
    }

}
