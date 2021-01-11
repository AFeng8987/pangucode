package org.linlinjava.litemall.admin.web;

import io.swagger.models.auth.In;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.admin.service.LogHelper;
import org.linlinjava.litemall.admin.vo.CategoryVo;
import org.linlinjava.litemall.core.util.BeanUtil;
import org.linlinjava.litemall.core.util.RegexUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallCategory;
import org.linlinjava.litemall.db.domain.LitemallGoods;
import org.linlinjava.litemall.db.service.LitemallCategoryService;
import org.linlinjava.litemall.db.service.LitemallGoodsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/admin/category")
@Validated
@ApiIgnore
public class AdminCategoryController {
    private final Log logger = LogFactory.getLog(AdminCategoryController.class);

    @Autowired
    private LitemallCategoryService categoryService;

    @Autowired
    private LitemallGoodsService goodsService;

    @Autowired
    private LogHelper logHelper;

    @RequiresPermissions("admin:category:list")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品分类"}, button = "查询")
    @GetMapping("/list")
    public Object list(@RequestParam(defaultValue = "L1") String level,
                       @RequestParam(defaultValue = "0") Integer pid,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit) {
        logger.info("类目list查询Start时间:" + LocalDateTime.now());
        List<LitemallCategory> categoryList = categoryService.queryByLevelAndPid(level, pid, page, limit);
        return ResponseUtil.okList(categoryList);
    }

    private Object validate(LitemallCategory category) {
        String name = category.getName();
        if (StringUtils.isBlank(name) || !RegexUtil.isZh(name) || name.length() > 10) {
            return ResponseUtil.badArgument();
        }
        if (StringUtils.isBlank(category.getLevel())||!StringUtils.equalsAny(category.getLevel(), "L1", "L2")) {
            return ResponseUtil.badArgumentValue();
        }
        Integer pid = category.getPid();
        if (StringUtils.equals("L2",category.getLevel()) && (null == pid)) {
            return ResponseUtil.badArgument();
        }
        Byte sortOrder = category.getSortOrder();
        if (sortOrder.intValue() < 1||sortOrder.intValue()>100) {
            return ResponseUtil.badArgument();
        }
        return null;
    }

    @RequiresPermissions("admin:category:create")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品分类"}, button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallCategory category) {
        logger.info("商品分类：添加类目" + category.toString());
        Object error = validate(category);
        if (error != null) {
            return error;
        }
        categoryService.add(category);
        logHelper.logOtherSucceed("类目添加", "类目名:" + category.getName() + ", 类目级别:" + category.getLevel());
        return ResponseUtil.ok(category);
    }

    @RequiresPermissions("admin:category:read")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品分类"}, button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        LitemallCategory category = categoryService.findById(id);
        return ResponseUtil.ok(category);
    }

    @RequiresPermissions("admin:category:update")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品分类"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallCategory category) {
        Object error = validate(category);
        if (error != null) {
            return error;
        }
        if (categoryService.updateById(category) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        logHelper.logOtherSucceed("类目添加", category.getName());
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:category:delete")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品分类"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallCategory category) {
        Integer id = category.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        //二级类目可以删除，一级类目不可以删。二级类目删除必须提出关联的商品先下架。
        LitemallCategory category1=categoryService.findById(id);
        if (ObjectUtils.isEmpty(category1)){
            return ResponseUtil.badArgument();
        }
        if (category1.getPid()==0&&categoryService.queryByPid(category1.getId()).size()>0){
            return ResponseUtil.badArgumentValue("一级类目下有二级类目，不支持直接删除");
        }
        if (category1.getPid()!=0){
            //二级类目查关联商品
           List<LitemallGoods> goodsList= goodsService.queryByCategory(id);
           if (CollectionUtils.isNotEmpty(goodsList)){
               return ResponseUtil.fail(-1,"该类目被在售商品："+goodsList.get(0).getName()+"关联，请先下架商品或者修改该商品所属类目");
           }
        }
        categoryService.deleteById(id);
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:category:L1")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品分类"}, button = "一级类目查询")
    @GetMapping("/L1")
    public Object catL1() {
        // 所有一级分类目录
        List<LitemallCategory> l1CatList = categoryService.queryL1();
        List<Map<String, Object>> data = new ArrayList<>(l1CatList.size());
        if (CollectionUtils.isNotEmpty(l1CatList)) {
            for (LitemallCategory category : l1CatList) {
                Map<String, Object> d = new HashMap<>(2);
                d.put("value", category.getId());
                d.put("label", category.getName());
                data.add(d);
            }
        }
        return ResponseUtil.okList(data);
    }

    @RequiresPermissions("admin:category:L2")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品分类"}, button = "二级类目查询")
    @GetMapping("/L2/{pid}")
    public Object catL1(@PathVariable(name = "pid") int pid) {
        // 所有一级分类目录
        List<LitemallCategory> l2CatList = categoryService.queryByPid(pid);
        List<Map<String, Object>> data = new ArrayList<>(l2CatList.size());
        if (CollectionUtils.isNotEmpty(l2CatList)) {
            for (LitemallCategory category : l2CatList) {
                Map<String, Object> d = new HashMap<>(2);
                d.put("value", category.getId());
                d.put("label", category.getName());
                d.put("keywords", category.getKeywords());
                data.add(d);
            }
        }
        return ResponseUtil.okList(data);
    }


}
