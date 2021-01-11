package org.linlinjava.litemall.admin.service;

import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.LitemallAdmin;
import org.linlinjava.litemall.db.domain.LitemallProductFactory;
import org.linlinjava.litemall.db.domain.LitemallSpecodeProduct;
import org.linlinjava.litemall.db.domain.LitemallStockRecord;
import org.linlinjava.litemall.db.service.LitemallProductFactoryService;
import org.linlinjava.litemall.db.service.LitemallSpecodeProductService;
import org.linlinjava.litemall.db.service.LitemallStockRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class AdminProductStockService {
    private final Log logger = LogFactory.getLog(AdminProductStockService.class);

    @Autowired
    private LitemallProductFactoryService productFactoryService;

    @Autowired
    private LitemallSpecodeProductService specsCodeProductService;

    @Autowired
    private LitemallStockRecordService stockRecordService;

    @Autowired
    private LogHelper logHelper;

    public Object productSpecsList(String goodsCode, String codeName, String specCode, Integer page, Integer size) {
        PageInfo<Map<String, Object>> goodsProductList = specsCodeProductService.productSpecsList(goodsCode, codeName, specCode, page, size);
        return ResponseUtil.ok(goodsProductList);
    }


    public Object queryFactory(int specsCodeId) {
        List<Map<String, Object>> factors = productFactoryService.queryBySpecsCodeId(specsCodeId);
        return ResponseUtil.ok(factors);
    }

    /**
     * @param body {type:增减库存add增subtract减，number:数量，id工厂库存数据ID}
     * @return
     */
    @Transactional
    public Object updateFactoryProduct(String body) {
        String type = JacksonUtil.parseString(body, "type");
        Integer number = JacksonUtil.parseInteger(body, "number");
        Integer productFactoryId = JacksonUtil.parseInteger(body, "id");
        if (productFactoryId == null || number == null || StringUtils.isBlank(type)) {
            return ResponseUtil.badArgument();
        }
        LitemallProductFactory productFactory = productFactoryService.findById(productFactoryId);
        if (productFactory == null || number <= 0) {
            return ResponseUtil.badArgument();
        }
        LitemallSpecodeProduct specodeProduct = specsCodeProductService.findById(productFactory.getSpecCodeId());
        Subject currentUser = SecurityUtils.getSubject();
        LitemallAdmin admin = (LitemallAdmin) currentUser.getPrincipal();
        switch (type) {
            case "add":
                productFactory.setGoodsStock(productFactory.getGoodsStock() + number);
                specodeProduct.setTotalStock(specodeProduct.getTotalStock() + number);
                stockRecordService.add(specodeProduct.getId(), productFactory.getFactoryId(), number, 2, "管理员调整库存", admin.getUsername());
                logHelper.logOtherSucceed("调整库存","规格编码："+specodeProduct.getSpecCode()+"增加库存数："+number);
                break;
            case "subtract":
                if (number > productFactory.getGoodsStock()) {
                    return ResponseUtil.badArgument();
                }
                productFactory.setGoodsStock(productFactory.getGoodsStock() - number);
                specodeProduct.setTotalStock(specodeProduct.getTotalStock() - number);
                stockRecordService.add(specodeProduct.getId(), productFactory.getFactoryId(), number, 1, "管理员调整库存", admin.getUsername());
                logHelper.logOtherSucceed("调整库存","规格编码："+specodeProduct.getSpecCode()+"减少库存数："+number);
                break;
            default:
                return ResponseUtil.badArgument();
        }
        productFactoryService.updateById(productFactory);
        specsCodeProductService.updateById(specodeProduct);
        return ResponseUtil.ok(productFactory);
    }

    @Transactional
    public Object delProductFactory(int productFactoryId) {
        LitemallProductFactory productFactory = productFactoryService.findById(productFactoryId);
        if (productFactory == null) {
            return ResponseUtil.badArgument();
        }
        //删除工厂数据
        productFactoryService.deleteById(productFactoryId);
        //删减规格编码总库存
        LitemallSpecodeProduct specodeProduct = specsCodeProductService.findById(productFactory.getSpecCodeId());
        specodeProduct.setTotalStock(specodeProduct.getTotalStock() - productFactory.getGoodsStock());
        specsCodeProductService.updateById(specodeProduct);
        //记录出库记录
        Subject currentUser = SecurityUtils.getSubject();
        LitemallAdmin admin = (LitemallAdmin) currentUser.getPrincipal();
        stockRecordService.add(productFactory.getSpecCodeId(), productFactory.getFactoryId(), productFactory.getGoodsStock(), 1, "删除工厂", admin.getUsername());
        logHelper.logOtherSucceed("删除规格编码："+specodeProduct.getSpecCode(),"工厂ID:"+productFactory.getFactoryId());
        return ResponseUtil.ok();
    }

    @Transactional
    public Object addProductFactory(LitemallProductFactory productFactory,List<LitemallSpecodeProduct> specodeProducts ) {
        for (LitemallSpecodeProduct specodeProduct:specodeProducts) {
            //添加工厂信息
            productFactory.setSpecCodeId(specodeProduct.getId());
            productFactoryService.add(productFactory);
            specodeProduct.setTotalStock(specodeProduct.getTotalStock()+productFactory.getGoodsStock());
            //更新规格总库存
            specsCodeProductService.updateById(specodeProduct);
            //入库
            Subject currentUser = SecurityUtils.getSubject();
            LitemallAdmin admin = (LitemallAdmin) currentUser.getPrincipal();
            stockRecordService.add(productFactory.getSpecCodeId(), productFactory.getFactoryId(), productFactory.getGoodsStock(), 2, "新增工厂", admin.getUsername());
            logHelper.logOtherSucceed("规格编码："+specodeProduct.getSpecCode(),"新增工厂ID:"+productFactory.getFactoryId());
        }
        return ResponseUtil.ok();
    }

    public Object queryStockReCordList(String goodsCode, String codeName, String specsCode, String planName, LocalDate startTime, LocalDate endTime, Integer page, Integer limit, Integer status) {
       List<LitemallStockRecord> result= stockRecordService.querySelective(goodsCode,codeName,specsCode,planName,startTime,endTime,page,limit,status);
        return ResponseUtil.okList(result);
    }


    public Object realtimeList(String goodsCode, String codeName, String specsCode, LocalDate startTime, LocalDate endTime, Integer page, Integer limit) {
      List<Map<String,Object>> data= productFactoryService.querySelective(goodsCode,codeName,specsCode,startTime,endTime,page,limit);
      return ResponseUtil.okList(data);
    }

    public Object warnList(String goodsCode, String codeName, String specsCode, LocalDate beginDate,LocalDate endDate, Integer page, Integer limit) {
        List<Map<String,Object>> data= productFactoryService.warnList(goodsCode,codeName,specsCode,beginDate,endDate,page,limit);
        return ResponseUtil.okList(data);
    }
}
