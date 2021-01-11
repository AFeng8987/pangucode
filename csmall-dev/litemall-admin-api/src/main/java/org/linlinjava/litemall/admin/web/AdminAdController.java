package org.linlinjava.litemall.admin.web;

import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.admin.service.LogHelper;
import org.linlinjava.litemall.core.util.RegexUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallAd;
import org.linlinjava.litemall.db.service.LitemallAdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/admin/ad")
@Validated
public class AdminAdController {
    private final Log logger = LogFactory.getLog(AdminAdController.class);

    @Autowired
    private LitemallAdService adService;

    @Autowired
    private LogHelper logHelper;

    @RequiresPermissions("admin:ad:list")
    @RequiresPermissionsDesc(menu = {"商品管理", "Banner配置"}, button = "查询")
    @GetMapping("/list")
    public Object list(String name, String content,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        PageInfo adList = adService.querySelective(name, content, page, limit, sort, order);
        logger.info("广告管理-查询");
        return ResponseUtil.okList(adList);
    }

    private Object validate(LitemallAd ad) {
        String name = ad.getName();
        String link = ad.getLink();
        String url = ad.getUrl();
        Boolean status = ad.getStatus();
        if (StringUtils.isAnyBlank(name, link, url) || ObjectUtils.isEmpty(status)) {
            return ResponseUtil.badArgument();
        }
        if (name.length() > 20) {
            return ResponseUtil.badArgumentValue("Banner名称长度不能超过20中文字符");
        }
        if (!RegexUtil.isURL(url)) {
            return ResponseUtil.badArgument();
        }
        if (status && !RegexUtil.isURL(link)) {
            return ResponseUtil.badArgumentValue("外部链接格式错误");
        }
        return null;
    }

    @RequiresPermissions("admin:ad:create")
    @RequiresPermissionsDesc(menu = {"商品管理", "Banner配置"}, button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallAd ad) {
        logger.info("广告管理-添加");
        Object error = validate(ad);
        if (error != null) {
            return error;
        }
        adService.add(ad);
        logHelper.logOtherSucceed("添加广告", "BannerName：" + ad.getName());
        return ResponseUtil.ok(ad);
    }

    @RequiresPermissions("admin:ad:read")
    @RequiresPermissionsDesc(menu = {"商品管理", "Banner配置"}, button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        logger.info("广告管理-详情");
        LitemallAd ad = adService.findById(id);
        return ResponseUtil.ok(ad);
    }

    @RequiresPermissions("admin:ad:update")
    @RequiresPermissionsDesc(menu = {"商品管理", "Banner配置"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallAd ad) {
        logger.info("广告管理-编辑");
        Object error = validate(ad);
        if (error != null) {
            return error;
        }
        if (adService.updateById(ad) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        logHelper.logOtherSucceed("广告管理-编辑", "BannerName：" + ad.getName());
        return ResponseUtil.ok(ad);
    }

    @RequiresPermissions("admin:ad:delete")
    @RequiresPermissionsDesc(menu = {"商品管理", "Banner配置"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallAd ad) {
        Integer id = ad.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        adService.deleteById(id);
        logHelper.logOtherSucceed("广告管理-删除", "BannerId：" + id);
        return ResponseUtil.ok();
    }

}
