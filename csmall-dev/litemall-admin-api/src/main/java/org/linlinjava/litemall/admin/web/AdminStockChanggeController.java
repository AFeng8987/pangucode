package org.linlinjava.litemall.admin.web;

import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.admin.dto.GoodsAllCode;
import org.linlinjava.litemall.admin.service.AdminGoodsCodeService;
import org.linlinjava.litemall.admin.service.AdminProductStockService;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.LitemallGoodsCode;
import org.linlinjava.litemall.db.domain.LitemallProductFactory;
import org.linlinjava.litemall.db.domain.LitemallSpecodeProduct;
import org.linlinjava.litemall.db.service.LitemallProductFactoryService;
import org.linlinjava.litemall.db.service.LitemallSpecodeProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/stock")
@Validated
//@ApiIgnore
public class AdminStockChanggeController {
    private final Log logger = LogFactory.getLog(AdminStockChanggeController.class);

    @Autowired
    private AdminProductStockService productStockService;

    @Autowired
    private LitemallProductFactoryService productFactoryService;

    @Autowired
    private LitemallSpecodeProductService specsCodeProductService;

    /**
     * @param goodsCode
     * @param codeName
     * @param page
     * @param limit
     * @return
     */
    @RequiresPermissions("admin:stock:list")
    @RequiresPermissionsDesc(menu = {"库存管理", "库存调整"}, button = "规格编码查询")
    @GetMapping("/list")
    @ApiOperation(value = "库存调整list列表数据")
    public Object list(String goodsCode, String codeName,String specCode,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit) {
        return productStockService.productSpecsList(goodsCode, codeName, specCode,page, limit);
    }

    /**
     * @param specsCodeId
     */
    @RequiresPermissions("admin:stock:listFactory")
    @RequiresPermissionsDesc(menu = {"库存管理", "库存调整"}, button = "工厂库存查询")
    @GetMapping("/listFactory")
    @ApiOperation(value = "规格下工厂库存信息查询")
    public Object listFactory(int specsCodeId) {
        return productStockService.queryFactory(specsCodeId);
    }

    /**
     * @param body
     */
    @RequiresPermissions("admin:stock:updateStock")
    @RequiresPermissionsDesc(menu = {"库存管理", "库存调整"}, button = "工厂库存调整")
    @PostMapping("/updateStock")
    @ApiOperation(value = "工厂库存调整")
    public Object updateStock(@RequestBody  String body) {
        return productStockService.updateFactoryProduct(body);
    }



    @RequiresPermissions("admin:stock:delFactory")
    @RequiresPermissionsDesc(menu = {"库存管理", "库存调整"}, button = "删除工厂")
    @PostMapping("/delFactory")
    @ApiOperation(value = "删除工厂")
    public Object delFactory(@RequestBody int productFactoryId) {
        return productStockService.delProductFactory(productFactoryId);
    }



    @RequiresPermissions("admin:stock:addFactory")
    @RequiresPermissionsDesc(menu = {"库存管理", "库存调整"}, button = "新增工厂")
    @PostMapping("/addFactory")
    @ApiOperation(value = "新增工厂")
    public Object addFactory(@RequestBody String  body) {
        List<Integer>  specCodeIds= JacksonUtil.parseIntegerList(body,"specCodeIds");
        LitemallProductFactory  productFactory= JacksonUtil.parseObject(body,"productFactory",LitemallProductFactory.class);

        if (productFactory.getGoodsStock()<0||productFactory.getWarnStock()<0||productFactory.getGoodsStock()>1000000||productFactory.getWarnStock()>productFactory.getGoodsStock()){
            return ResponseUtil.badArgumentValue("库存数值不对");
        }
        if (productFactory.getCostPrice().compareTo(BigDecimal.ZERO)==-1
                ||productFactory.getBuyPrice().compareTo(BigDecimal.ZERO)==-1
                ||productFactory.getCostPrice().compareTo(productFactory.getBuyPrice())!=1){
            return ResponseUtil.badArgumentValue("价格不能为负且成本价必须大于进货价");
        }
        List<LitemallSpecodeProduct> specodeProducts = specsCodeProductService.queryByIds(specCodeIds);
        if (CollectionUtils.isEmpty(specodeProducts)||specodeProducts.size()!=specCodeIds.size()) {
            return ResponseUtil.badArgument();
        }
        Map<Integer,List<LitemallSpecodeProduct>> groupSpecodeProduct=specodeProducts.stream().collect(Collectors.groupingBy(LitemallSpecodeProduct::getGoodsCodeId));
        if (groupSpecodeProduct.size()!=1) {
            return ResponseUtil.badArgumentValue("非同一商品编码下的数据，不支持当前操作");
        }

        List<LitemallProductFactory> productFactors=productFactoryService.queryBySpecsCodeIdAndFid(specCodeIds,productFactory.getFactoryId());
        if (productFactors.size()>0) {
            return ResponseUtil.badArgumentValue("该工厂已在规格编码:"+specsCodeProductService.findById(productFactors.get(0).getSpecCodeId()).getSpecCode()+"内");
        }
        productFactory.setGoodsCodeId(specodeProducts.get(0).getGoodsCodeId());
        return productStockService.addProductFactory(productFactory,specodeProducts);
    }




    @RequiresPermissions("admin:stock:inList")
    @RequiresPermissionsDesc(menu = {"库存管理", "入库"}, button = "查询")
    @GetMapping("/inList")
    @ApiOperation(value = "入库查询")
    public Object inList(String goodsCode,String codeName,String specsCode,String planName, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginDate,
                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                         @RequestParam(defaultValue = "1") Integer page,
                         @RequestParam(defaultValue = "10") Integer limit) {
        return productStockService.queryStockReCordList(goodsCode,codeName,specsCode,planName,beginDate,endDate,page, limit,2);
    }


    /**
     * @param goodsCode 商品编码
     * @param specsCode  规格编码
     * @param planName  工厂名称
     * @param beginDate  开始时间
     * @param endDate 结束时间
     * @param page
     * @param limit
     * @return
     */
    @RequiresPermissions("admin:stock:outList")
    @RequiresPermissionsDesc(menu = {"库存管理", "出库"}, button = "查询")
    @GetMapping("/outList")
    @ApiOperation(value = "出库查询")
    public Object outList(String goodsCode,String codeName,String specsCode,String planName,@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginDate,
                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                          @RequestParam(defaultValue = "1") Integer page,
                          @RequestParam(defaultValue = "10") Integer limit) {
        return productStockService.queryStockReCordList(goodsCode,codeName,specsCode,planName,beginDate,endDate,page, limit,1);
    }


    @RequiresPermissions("admin:stock:realtimeList")
    @RequiresPermissionsDesc(menu = {"库存管理", "实时库存"}, button = "查询")
    @GetMapping("/realtimeList")
    @ApiOperation(value = "实时库存查询")
    public Object realtimeList(String goodsCode,String codeName,String specsCode,@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginDate,
                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                               @RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer limit) {
        return productStockService.realtimeList(goodsCode,codeName,specsCode,beginDate,endDate,page, limit);
    }



    @RequiresPermissions("admin:stock:warnList")
    @RequiresPermissionsDesc(menu = {"库存管理", "库存预警"}, button = "查询")
    @GetMapping("/warnList")
    @ApiOperation(value = "库存预警查询")
    public Object warnList(String goodsCode,String codeName,String specsCode,
                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginDate,
                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                           @RequestParam(defaultValue = "1") Integer page,
                           @RequestParam(defaultValue = "10") Integer limit) {
        return productStockService.warnList(goodsCode,codeName,specsCode,beginDate,endDate,page, limit);
    }




}
