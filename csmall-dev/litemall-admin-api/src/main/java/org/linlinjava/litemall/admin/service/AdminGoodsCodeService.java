package org.linlinjava.litemall.admin.service;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.linlinjava.litemall.admin.dto.GoodsAllCode;
import org.linlinjava.litemall.core.util.RegexUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminGoodsCodeService {
    private final Log logger = LogFactory.getLog(AdminGoodsCodeService.class);

    @Autowired
    private LitemallGoodsService goodsService;

    @Autowired
    private LitemallGoodsCodeService goodsCodeService;

    @Autowired
    private LitemallGoodsSpecService goodsSpecService;

    @Autowired
    private LitemallSpecodeProductService speCodeProductService;

    @Autowired
    private LitemallStockRecordService stockRecordService;

    @Autowired
    private LogHelper logHelper;


    @Autowired
    private LitemallProductFactoryService productFactoryService;

    /**
     * 查询商品编码
     *
     * @param goodsCode 商品编码
     * @param codeName  编码名称
     * @param page      分页
     * @param size      大小
     * @return
     */
    public Object listGoodsCode(String goodsCode, String codeName, Integer page, Integer size) {
        List<LitemallGoodsCode> goodsCodeList = goodsCodeService.querySelective(goodsCode, codeName, page, size);
        return ResponseUtil.okList(goodsCodeList);
    }

    /**
     * 删除商品编码
     *
     * @param goodsCode 商品编码对象
     * @return
     */
    @Transactional
    public Object delete(LitemallGoodsCode goodsCode) {
        //联动到商品是否下架
        Integer gCodeId = goodsCode.getId();
        if (goodsService.countByGoodsCodeId(gCodeId)) {
            return ResponseUtil.fail(-1, "还有商品未下架");
        }
        if (gCodeId == null) {
            return ResponseUtil.badArgument();
        }
        batchDelAddStockRecord(gCodeId, "删除商品编码触发");
        goodsCodeService.deleteById(gCodeId);
        goodsSpecService.deleteByCodeId(gCodeId);
        speCodeProductService.deleteByCodeId(gCodeId);
        productFactoryService.deleteByCodeId(gCodeId);
        return ResponseUtil.ok();
    }

    private Object validateGoodsCode(LitemallGoodsCode goodsCode) {
        String name = goodsCode.getCodeName();
        if (StringUtils.isBlank(name)||!RegexUtil.isUsername(name)||name.length()>10) {
            return ResponseUtil.badArgumentValue("商品编码名称不能为空仅支持长度10的中英数字组合");
        }
        String code = goodsCode.getGoodsCode();
        if (StringUtils.isBlank(code)||!RegexUtil.isMatch(RegexUtil.REGEX_LETTER,code)||code.length()>15) {
            return ResponseUtil.badArgumentValue("商品编码不能为空仅支持长度15的英文+数字+下划线组合");
        }
        return null;
    }


    /**
     * 新增商品编码
     *
     * @param goodsCode 商品编码对象
     * @return
     */
    public Object createGoodsCode(LitemallGoodsCode goodsCode) {
        Object error = validateGoodsCode(goodsCode);
        if (error != null) {
            return error;
        }
        String code = goodsCode.getGoodsCode();
        if (goodsCodeService.checkCode(code)) {
            return ResponseUtil.badArgumentValue("商品编码重复");
        }
        goodsCodeService.create(goodsCode);
        logHelper.logOtherSucceed("新增商品编码");
        return ResponseUtil.ok(goodsCode);
    }

    /**
     * 更新商品编码
     *
     * @param goodsCode 商品编码对象
     * @return
     */
    public Object updateGoodsCode(LitemallGoodsCode goodsCode) {
        Integer id = goodsCode.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        Object error = validateGoodsCode(goodsCode);
        if (error != null) {
            return error;
        }
        LitemallGoodsCode code=goodsCodeService.findById(id);
        //如果code修改了，校验一下code的唯一
        if (!StringUtils.equalsIgnoreCase(code.getGoodsCode(),goodsCode.getGoodsCode())&&goodsCodeService.checkCode(goodsCode.getGoodsCode())){
            return ResponseUtil.badArgumentValue("修改后的编码不可用");
        }
        if (goodsCodeService.updateById(goodsCode) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        logHelper.logOtherSucceed("修改商品编码");
        return ResponseUtil.ok(goodsCode);
    }

    /**
     * 查商品编码下的规格详情
     *
     * @param id 商品编码ID
     * @return
     */
    public Object read(Integer id) {
        LitemallGoodsCode goodsCode = goodsCodeService.findById(id);
        List<LitemallGoodsSpec> goodsSpecs = goodsSpecService.queryByGid(id);
        List<LitemallSpecodeProduct> specsCodeProducts = speCodeProductService.queryByGid(id);
        Map<String, Object> data = new HashMap<>();
        data.put("goodsCode", goodsCode);
        data.put("specs", goodsSpecs);
        data.put("products", specsCodeProducts);
        return ResponseUtil.ok(data);
    }


    /**
     * 更新规格编码数据
     *
     * @param specsCodeProducts
     * @return
     */
    public Object updatePro(List<LitemallSpecodeProduct> specsCodeProducts) {
        //判空
        if (CollectionUtils.isEmpty(specsCodeProducts)) {
            return ResponseUtil.badArgument();
        }
        Object error = validateSpecsPro(specsCodeProducts);
        if (error != null) {
            logHelper.logGeneralFail("更新规格编码对象数据", "参数错误");
            return error;
        }
        // 商品编码需要验证存在
        if (goodsCodeService.findById(specsCodeProducts.get(0).getGoodsCodeId()) == null) {
            return ResponseUtil.badArgumentValue();
        }
        if (goodsService.countByGoodsCodeId(specsCodeProducts.get(0).getGoodsCodeId())) {
            return ResponseUtil.fail(-1, "还有关联商品未下架");
        }
        //校验数组中对象的ID是否传了，把没传ID的做新数组，
        List<LitemallSpecodeProduct> specsCodeProductList = specsCodeProducts.stream().filter(s -> s.getId() == null).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(specsCodeProductList)) {
            return ResponseUtil.badArgumentValue("ID不能为空");
        }
        List<LitemallSpecodeProduct> unique = specsCodeProducts.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(LitemallSpecodeProduct::getSpecCode))), ArrayList::new)
        );
        if (unique.size()!=specsCodeProducts.size()){
            return ResponseUtil.badArgumentValue("规格编码不能有重复");
        }
        speCodeProductService.updateBatch(specsCodeProducts);
        return ResponseUtil.ok();
    }


    public void batchDelAddStockRecord(Integer gCodeId, String remarks) {
        LitemallGoodsCode goodsCode = goodsCodeService.findById(gCodeId);
        List<LitemallSpecodeProduct> specsCodeProducts = speCodeProductService.queryByGid(gCodeId);
        if (specsCodeProducts.size() < 1) {
            return;
        }
        List<LitemallStockRecord> data = new ArrayList<>();
        Subject currentUser = SecurityUtils.getSubject();
        LitemallAdmin admin = (LitemallAdmin) currentUser.getPrincipal();
        for (LitemallSpecodeProduct specsCodeProduct : specsCodeProducts) {
            List<Map<String, Object>> result = productFactoryService.queryBySpecsCodeId(specsCodeProduct.getId());
            if (result.size() < 1) {
                return;
            }
            for (Map map : result) {
                int number = (int) map.get("goodsStock");
                if (number > 0) {
                    LitemallStockRecord stockRecord = new LitemallStockRecord();
                    stockRecord.setGoodsCode(goodsCode.getGoodsCode());
                    stockRecord.setCodeName(goodsCode.getCodeName());
                    stockRecord.setSpecsCode(specsCodeProduct.getSpecCode());
                    stockRecord.setSpecsDesc(specsCodeProduct.getSpecsDesc());
                    stockRecord.setPlantName((String) map.get("plantName"));
                    stockRecord.setNumber(number);
                    stockRecord.setStatus(1);
                    stockRecord.setOperator(admin.getUsername());
                    stockRecord.setRemarks(remarks);
                    stockRecord.setUrl(specsCodeProduct.getUrl());
                    data.add(stockRecord);
                }
            }
        }
        stockRecordService.batchInsert(data);
    }

    /**
     * 新建规格值
     *
     * @param goodsAllCode
     * @return
     */
    @Transactional
    public Object createSpecs(GoodsAllCode goodsAllCode) {
        Object error = validateGoodsCode(goodsAllCode);
        if (error != null) {
            return ResponseUtil.badArgument();
        }
        // 商品编码需要验证存在
        if (goodsCodeService.findById(goodsAllCode.getGoodsCodeId()) == null) {
            return ResponseUtil.badArgumentValue();
        }
        if (goodsService.countByGoodsCodeId(goodsAllCode.getGoodsCodeId())) {
            return ResponseUtil.fail(-1, "还有关联商品未下架");
        }
        batchInsertSpecsAndProduct(goodsAllCode);
        List<LitemallSpecodeProduct> specodeProducts = speCodeProductService.queryByGid(goodsAllCode.getGoodsCodeId());
        return ResponseUtil.ok(specodeProducts);
    }

    /**
     * 删除规格值
     *
     * @param goodsCode
     * @return
     */
    @Transactional
    public Object delSpecs(LitemallGoodsCode goodsCode) {
        //联动到商品是否下架
        Integer gCodeId = goodsCode.getId();
        // 商品编码需要验证存在
        if (goodsCodeService.findById(gCodeId) == null) {
            return ResponseUtil.badArgumentValue();
        }
        if (goodsService.countByGoodsCodeId(gCodeId)) {
            return ResponseUtil.fail(-1, "还有关联商品未下架");
        }
        batchDelAddStockRecord(gCodeId, "删除规格值触发");
        goodsSpecService.deleteByCodeId(gCodeId);
        speCodeProductService.deleteByCodeId(gCodeId);
        productFactoryService.deleteByCodeId(gCodeId);
        return ResponseUtil.ok();
    }

    /**
     * 批量新增规格且组合Sku插入product
     *
     * @param goodsAllCode
     */

    public void batchInsertSpecsAndProduct(GoodsAllCode goodsAllCode) {
        Integer gCodeId = goodsAllCode.getGoodsCodeId();
        //规格值新增
        List<LitemallGoodsSpec> goodsSpecs = Arrays.asList(goodsAllCode.getSpecs());
        goodsSpecService.batchInsert(goodsSpecs);
        //组合后货品新增
        List<LitemallSpecodeProduct> specsCodeProducts = specToProduct(Arrays.asList(goodsAllCode.getSpecs()), gCodeId);
        speCodeProductService.batchInsert(specsCodeProducts);
    }


    private Object validateGoodsCode(GoodsAllCode goodsAllCode) {
        Integer gCodeId = goodsAllCode.getGoodsCodeId();
        if (ObjectUtils.isEmpty(gCodeId)|| Integer.valueOf("0").equals(gCodeId)) {
            return ResponseUtil.badArgumentValue();
        }
        // 商品编码需要验证存在
        if (goodsCodeService.findById(gCodeId) == null) {
            return ResponseUtil.badArgumentValue();
        }
        //校验规格
        LitemallGoodsSpec[] goodsSpecs = goodsAllCode.getSpecs();
        if (goodsSpecs.length < 1) {
            return ResponseUtil.badArgumentValue();
        }
        for (LitemallGoodsSpec goodsSpec : goodsSpecs) {
            String spec = goodsSpec.getSpec();
            if (StringUtils.isBlank(spec)) {
                return ResponseUtil.badArgument();
            }
            String value = goodsSpec.getValue();
            if (StringUtils.isBlank(value)) {
                return ResponseUtil.badArgument();
            }
        }
        return null;
    }


    private List<LitemallSpecodeProduct> specToProduct(List<LitemallGoodsSpec> list, Integer gCodeId) {
        Map<String, List<LitemallGoodsSpec>> userGroupMap = list.stream().
                collect(Collectors.groupingBy(LitemallGoodsSpec::getSpec));
        Collection<List<LitemallGoodsSpec>> mapValues = userGroupMap.values();
        List<List<LitemallGoodsSpec>> dimensionValue = new ArrayList<>(mapValues);    // 原List

        List<List<LitemallGoodsSpec>> result = new ArrayList<>(); // 返回集合
        descartes(dimensionValue, result, 0, new ArrayList<LitemallGoodsSpec>());


        List<LitemallSpecodeProduct> specodeProducts = new ArrayList<>();
        for (List<LitemallGoodsSpec> models : result) {
            String[] s = new String[models.size()];
            for (int i = 0; i < models.size(); i++) {
                s[i] = models.get(i).getValue();
            }
            LitemallSpecodeProduct specsCodeProduct = new LitemallSpecodeProduct();
            specsCodeProduct.setSpecsDesc(s);
            specsCodeProduct.setGoodsCodeId(gCodeId);
            specsCodeProduct.setSpecCode("NO_UU" + result.indexOf(models) + 1);
            specodeProducts.add(specsCodeProduct);
        }
        return specodeProducts;
    }


    public void descartes(List<List<LitemallGoodsSpec>> dimensionValue, List<List<LitemallGoodsSpec>> result, int layer, List<LitemallGoodsSpec> currentList) {
        if (layer < dimensionValue.size() - 1) {
            if (dimensionValue.get(layer).size() == 0) {
                descartes(dimensionValue, result, layer + 1, currentList);
            } else {
                for (int i = 0; i < dimensionValue.get(layer).size(); i++) {
                    List<LitemallGoodsSpec> list = new ArrayList<>(currentList);
                    list.add(dimensionValue.get(layer).get(i));
                    descartes(dimensionValue, result, layer + 1, list);
                }
            }
        } else if (layer == dimensionValue.size() - 1) {
            if (dimensionValue.get(layer).size() == 0) {
                result.add(currentList);
            } else {
                for (int i = 0; i < dimensionValue.get(layer).size(); i++) {
                    List<LitemallGoodsSpec> list = new ArrayList<>(currentList);
                    list.add(dimensionValue.get(layer).get(i));
                    result.add(list);
                }
            }
        }
    }


    private Object validateSpecsPro(List<LitemallSpecodeProduct> specsCodePros) {
        for (LitemallSpecodeProduct specsCodePro : specsCodePros) {
            String specsCode = specsCodePro.getSpecCode();
            if (StringUtils.isBlank(specsCode)||specsCode.length()>15||!RegexUtil.isMatch(RegexUtil.REGEX_LETTER,specsCode)) {
                return ResponseUtil.badArgumentValue("规格编码英文+数字+下划线，长度不能超过15");
            }
            String url = specsCodePro.getUrl();
            if (!RegexUtil.isURL(url)) {
                return ResponseUtil.badArgumentValue("图片必传");
            }
            BigDecimal shopPrice = specsCodePro.getShopPrice();
            if (shopPrice == null || shopPrice.compareTo(BigDecimal.ZERO) < 1) {
                return ResponseUtil.badArgumentValue("价格必须为正数");
            }
        }
        return null;
    }


}

