package org.linlinjava.litemall.admin.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.admin.dto.GoodsAllCode;
import org.linlinjava.litemall.admin.service.AdminGoodsCodeService;
import org.linlinjava.litemall.db.domain.LitemallGoodsCode;
import org.linlinjava.litemall.db.domain.LitemallGoodsSpec;
import org.linlinjava.litemall.db.domain.LitemallSpecodeProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/code")
@Validated
public class AdminCodeController {
    private final Log logger = LogFactory.getLog(AdminCodeController.class);

    @Autowired
    private AdminGoodsCodeService codeService;

    /**
     * @param goodsCode
     * @param codeName
     * @param page
     * @param limit
     * @return
     */
    @RequiresPermissions("admin:code:listGoods")
    @RequiresPermissionsDesc(menu = {"库存管理", "编码维护"}, button = "商品编码查询")
    @GetMapping("/list")
    public Object list(String goodsCode, String codeName,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit) {
        logger.info("编码维护---商品编码查询");
        return codeService.listGoodsCode(goodsCode, codeName, page, limit);
    }

    /**
     * 添加商品
     *
     * @param goodsCode
     * @return
     */
    @RequiresPermissions("admin:code:create")
    @RequiresPermissionsDesc(menu = {"库存管理", "编码维护"}, button = "商品编码新增")
    @PostMapping("/create")
    @ApiOperation(value = "商品编码新增")
    public Object create(@RequestBody LitemallGoodsCode goodsCode) {
        return codeService.createGoodsCode(goodsCode);
    }

    @RequiresPermissions("admin:code:update")
    @RequiresPermissionsDesc(menu = {"库存管理", "编码维护"}, button = "商品编码编辑")
    @PostMapping("/update")
    @ApiOperation(value = "商品编码编辑")
    public Object update(@RequestBody LitemallGoodsCode goodsCode) {
        return codeService.updateGoodsCode(goodsCode);
    }

    @RequiresPermissions("admin:code:delete")
    @RequiresPermissionsDesc(menu = {"库存管理", "编码维护"}, button = "商品编码删除")
    @PostMapping("/delete")
    @ApiOperation(value = "商品编码删除")
    public Object delete(@RequestBody LitemallGoodsCode goodsCode) {
        return codeService.delete(goodsCode);
    }

    @RequiresPermissions("admin:code:read")
    @RequiresPermissionsDesc(menu = {"库存管理", "编码维护"}, button = "查看规格编码")
    @GetMapping("/read")
    @ApiOperation(value = "查看商品编码的详情")
    public Object read(Integer id) {
        return codeService.read(id);
    }


    @RequiresPermissions("admin:code:createSpecs")
    @RequiresPermissionsDesc(menu = {"库存管理", "编码维护"}, button = "规格新增")
    @PostMapping("/createSpecs")
    @ApiOperation(value = "规格新增")
    public Object createSpecs(@RequestBody GoodsAllCode goodsAllCode) {
        return codeService.createSpecs(goodsAllCode);
    }

    @RequiresPermissions("admin:code:delSpecs")
    @RequiresPermissionsDesc(menu = {"库存管理", "编码维护"}, button = "规格删除")
    @PostMapping("/delSpecs")
    @ApiOperation(value = "规格删除")
    public Object delSpecs(@RequestBody LitemallGoodsCode goodsCode) {
        return codeService.delSpecs(goodsCode);
    }

    @RequiresPermissions("admin:code:updatePro")
    @RequiresPermissionsDesc(menu = {"库存管理", "编码维护"}, button = "货品规格编码更新")
    @PostMapping("/updatePro")
    @ApiOperation(value = "货品规格编码更新")
    public Object updatePro(@RequestBody List<LitemallSpecodeProduct> specsCodeProductList) {
        return codeService.updatePro(specsCodeProductList);
    }

}
