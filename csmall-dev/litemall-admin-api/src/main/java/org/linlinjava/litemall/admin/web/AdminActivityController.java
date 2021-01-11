package org.linlinjava.litemall.admin.web;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.admin.service.AdminGoodsService;
import org.linlinjava.litemall.admin.service.LogHelper;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.core.util.RegexUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallActivity;
import org.linlinjava.litemall.db.domain.LitemallGoods;
import org.linlinjava.litemall.db.domain.LitemallGoodsActivity;
import org.linlinjava.litemall.db.dto.LitemallGoodsDto;
import org.linlinjava.litemall.db.service.LitemallActivityService;
import org.linlinjava.litemall.db.service.LitemallGoodsActivityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 活动页板块
 */
@RestController
@RequestMapping("/admin/activity")
@Validated
public class AdminActivityController {
    private final Log logger = LogFactory.getLog(AdminActivityController.class);

    @Autowired
    private LitemallActivityService litemallActivity;

    @Autowired
    private LitemallGoodsActivityService goodsActivityService;

    @Autowired
    private AdminGoodsService adminGoodsService;
    @Autowired
    private LogHelper logHelper;


    @RequiresPermissions("admin:activity:activityList")
    @RequiresPermissionsDesc(menu = {"活动板块", "精品模块"}, button = "列表查询")
    @ApiOperation(value = "精品模块查询", notes = "精品模块查询")
    @GetMapping("/activityList")
    public Object activityList(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer limit) {
        logger.info("精品模块查询--查询");
        List<LitemallActivity> list = litemallActivity.activityList(page, limit);
        return ResponseUtil.okList(list);
    }

    @RequiresPermissions("admin:activity:activityDetail")
    @RequiresPermissionsDesc(menu = {"活动板块", "精品模块"}, button = "详情")
    @ApiOperation(value = "精品模块详情", notes = "精品模块详情")
    @GetMapping("/activityDetail")
    public Object activityDetail(@NotNull Integer id) {
        logger.info("精品模块详情");
        LitemallActivity activity = litemallActivity.getOne(id);
        logHelper.logGeneralSucceed("精品模块详情");
        return ResponseUtil.ok(activity);
    }

    @RequiresPermissions("admin:activity:activityUpdate")
    @RequiresPermissionsDesc(menu = {"活动板块", "精品模块"}, button = "修改")
    @ApiOperation(value = "板块修改", notes = "精品模块修改")
    @PostMapping("/activityUpdate")
    public Object activityUpdate(@RequestBody LitemallActivity activity) {
        logger.info("精品模块修改");
        if (null==activity.getId()||StringUtils.isAnyBlank(activity.getActivityName(),activity.getPicUrl())){
            return ResponseUtil.badArgument();
        }
        if (!RegexUtil.isZh(activity.getActivityName())||activity.getActivityName().length()>10) {
            return ResponseUtil.badArgumentValue("活动名不能超过10个中文");
        }
        if (!RegexUtil.isURL(activity.getPicUrl())){
            return ResponseUtil.badArgumentValue("图片传参错误");
        }
        litemallActivity.activityUpdate(activity);
        logHelper.logGeneralSucceed("修改活动模块");
        return ResponseUtil.ok();
    }

    /**
     * 查询活动商品
     *
     * @return
     */
    @RequiresPermissions("admin:activity:activityGoods")
    @RequiresPermissionsDesc(menu = {"活动板块", "精品模块"}, button = "模块商品查询")
    @ApiOperation(value = "模块商品查询", notes = "模块商品查询")
    @GetMapping("/activityGoods/{activityId}")
    public Object activityGoods(@PathVariable Integer activityId,
                                String goodsCode, String goodsName,
                                @RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer limit) {
        logger.info("活动商品查询");
        return adminGoodsService.activityGoods(activityId, goodsCode, goodsName, page, limit);
    }

    /**
     * 删除活动商品
     *
     * @return
     */
    @RequiresPermissions("admin:activity:activityGoodsDel")
    @RequiresPermissionsDesc(menu = {"活动板块", "精品模块"}, button = "模块商品删除")
    @ApiOperation(value = "活动商品删除", notes = "商品删除")
    @PostMapping("/activityGoodsDel/{activityId}/{goodsId}")
    public Object activityGoodsDel(@PathVariable Integer activityId, @PathVariable Integer goodsId) {
        logger.info("活动商品删除");
        goodsActivityService.del(activityId, goodsId);
        logHelper.logGeneralSucceed("活动商品删除","活动版块ID："+activityId+"商品ID："+goodsId);
        return ResponseUtil.ok();
    }

    /**
     * 添加活动商品
     *
     * @return
     */
    @RequiresPermissions("admin:activity:activityGoodsAdd")
    @RequiresPermissionsDesc(menu = {"活动板块", "精品模块"}, button = "模块商品添加")
    @ApiOperation(value = "活动商品添加", notes = "活动商品添加")
    @PostMapping("/activityGoodsAdd")
    public Object activityGoodsAdd(@RequestBody String body) {
        logger.info("模块商品添加");
        Integer activityId = JacksonUtil.parseInteger(body, "activityId");
        List<Integer> goodsIds = JacksonUtil.parseIntegerList(body, "goodsIds");
        if (ObjectUtils.anyNull(activityId,goodsIds)) {
            return ResponseUtil.badArgumentValue("参数不能为空");
        }
        LitemallActivity activity=litemallActivity.getOne(activityId);
        if (null==activity) {
            return ResponseUtil.badArgumentValue("模块不存在");
        }
        List<LitemallGoodsActivity> goodsActivityList= goodsActivityService.queryByActivityId(activityId,goodsIds);
        if (goodsActivityList.size()>0){
            List<Integer> Ids=goodsActivityList.stream().map(LitemallGoodsActivity::getGoodsId).collect(Collectors.toList());
            return ResponseUtil.badArgumentValue("商品中有已添加的商品存在："+Ids.toString());
        }
        goodsActivityService.add(activityId, goodsIds);
        logHelper.logGeneralSucceed("活动商品添加","活动版块ID："+activityId+"商品IDs："+goodsIds.toString());
        return ResponseUtil.ok();
    }

    /**
     * 所有商品查询
     *
     * @return
     */
    @RequiresPermissions("admin:activity:goodsList")
    @RequiresPermissionsDesc(menu = {"活动板块", "精品模块"}, button = "所有商品查询")
    @ApiOperation(value = "所有商品查询", notes = "所有商品查询")
    @GetMapping("/goodsList/{activityId}")
    public Object goodsList(@PathVariable Integer activityId,
                            String goodsCode,  String goodsName,
                            @RequestParam(defaultValue = "1") Integer page,
                            @RequestParam(defaultValue = "10") Integer limit) {
        logger.info("模块商品"+activityId+"查询商品");
        List<LitemallGoodsDto> data=goodsActivityService.activityQueryGoods(activityId,goodsCode,goodsName,page,limit);
        return ResponseUtil.okList(data);
    }


    @RequiresPermissions("admin:activity:hotGoods")
    @RequiresPermissionsDesc(menu = {"活动板块", "热卖模块"}, button = "热卖商品列表")
    @GetMapping("/hotGoods")
    public Object hotGoods( String goodsCode, String goodsName,
                           @RequestParam(defaultValue = "1") Integer page,
                           @RequestParam(defaultValue = "10") Integer limit) {
        logger.info("热卖商品列表查询：参数｛商品编码："+goodsCode+"名称："+goodsName+"}");
        return adminGoodsService.hotList(goodsCode,goodsName,page,limit);
    }

}
