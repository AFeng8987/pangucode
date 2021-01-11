package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.linlinjava.litemall.db.dao.LitemallSpecodeProductMapper;
import org.linlinjava.litemall.db.dao.SpecodeProductMapper;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.domain.LitemallSpecodeProduct.Column;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class LitemallSpecodeProductService {
    @Resource
    private LitemallSpecodeProductMapper litemallSpecodeProductMapper;

    @Resource
    private SpecodeProductMapper specodeProductMapper;


    private Column[] columns = new Column[]{Column.id, Column.goodsCodeId, Column.specCode, Column.specsDesc,Column.shopPrice, Column.url, Column.totalStock, Column.addTime, Column.updateTime};

    public List<LitemallSpecodeProduct> queryByGid(Integer id) {
        LitemallSpecodeProductExample example = new LitemallSpecodeProductExample();
        example.or().andGoodsCodeIdEqualTo(id);
        return litemallSpecodeProductMapper.selectByExampleSelective(example,columns);
    }

    public LitemallSpecodeProduct queryByGidSpecsCode(Integer id, String specsCode) {
        LitemallSpecodeProductExample example = new LitemallSpecodeProductExample();
        example.or().andGoodsCodeIdEqualTo(id).andSpecCodeEqualTo(specsCode);
        return litemallSpecodeProductMapper.selectOneByExample(example);
    }

    public LitemallSpecodeProduct findById(Integer id) {
        return litemallSpecodeProductMapper.selectByPrimaryKey(id);
    }

    public List<LitemallSpecodeProduct> queryByIds(List<Integer> ids) {
        LitemallSpecodeProductExample example = new LitemallSpecodeProductExample();
        example.or().andIdIn(ids);
        return litemallSpecodeProductMapper.selectByExample(example);
    }

    public void deleteByCodeId(Integer gid) {
        LitemallSpecodeProductExample example = new LitemallSpecodeProductExample();
        example.or().andGoodsCodeIdEqualTo(gid);
        litemallSpecodeProductMapper.deleteByExample(example);
    }

    public void add(LitemallSpecodeProduct speCodeProduct) {
        speCodeProduct.setAddTime(LocalDateTime.now());
        speCodeProduct.setUpdateTime(LocalDateTime.now());
        litemallSpecodeProductMapper.insertSelective(speCodeProduct);
    }


    public void updateById(LitemallSpecodeProduct specodeProduct) {
        specodeProduct.setUpdateTime(LocalDateTime.now());
        litemallSpecodeProductMapper.updateByPrimaryKeySelective(specodeProduct);
    }

    public void batchInsert(List<LitemallSpecodeProduct> specCodeProductList) {
        litemallSpecodeProductMapper.batchInsertSelective(specCodeProductList, Column.goodsCodeId, Column.specCode, Column.specsDesc);
    }


    public PageInfo<Map<String, Object>> productSpecsList(String goodsCode, String codeName, String specCode, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<Map<String, Object>> result = specodeProductMapper.queryByGCodeNameSpecsCode(goodsCode, codeName, specCode);
        return new PageInfo<>(result);
    }


    public void updateBatch(List<LitemallSpecodeProduct> list){
        specodeProductMapper.updateBatch(list);
    }


    public int count() {
        LitemallSpecodeProductExample example = new LitemallSpecodeProductExample();
        return (int)litemallSpecodeProductMapper.countByExample(example);
    }
}
