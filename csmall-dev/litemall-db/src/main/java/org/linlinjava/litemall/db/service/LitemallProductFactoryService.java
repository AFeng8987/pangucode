package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.linlinjava.litemall.db.dao.LitemallProductFactoryMapper;
import org.linlinjava.litemall.db.dao.LitemallSpecodeProductMapper;
import org.linlinjava.litemall.db.dao.ProductFactoryMapper;
import org.linlinjava.litemall.db.domain.LitemallProductFactory;
import org.linlinjava.litemall.db.domain.LitemallProductFactory.Column;
import org.linlinjava.litemall.db.domain.LitemallProductFactoryExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class LitemallProductFactoryService {
    @Resource
    private LitemallProductFactoryMapper litemallProductFactoryMapper;

    @Resource
    private ProductFactoryMapper productFactoryMapper;
    @Resource
    private LitemallSpecodeProductMapper litemallSpecodeProductMapper;

    private DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<Map<String, Object>> queryBySpecsCodeId(int specId) {
        return productFactoryMapper.queryBySpecsCodeId(specId);
    }

    public LitemallProductFactory findById(Integer id) {
        return litemallProductFactoryMapper.selectByPrimaryKey(id);
    }

    public void deleteByCodeId(Integer gid) {
        LitemallProductFactoryExample example = new LitemallProductFactoryExample();
        example.or().andGoodsCodeIdEqualTo(gid);
        litemallProductFactoryMapper.deleteByExample(example);
    }

    public void deleteById(Integer gid) {
        litemallProductFactoryMapper.deleteByPrimaryKey(gid);
    }


    public void add(LitemallProductFactory goodsSpecification) {
        goodsSpecification.setAddTime(LocalDateTime.now());
        goodsSpecification.setUpdateTime(LocalDateTime.now());
        litemallProductFactoryMapper.insertSelective(goodsSpecification);
    }


    public void updateById(LitemallProductFactory specification) {
        specification.setUpdateTime(LocalDateTime.now());
        litemallProductFactoryMapper.updateByPrimaryKeySelective(specification);
    }


    public List<LitemallProductFactory> queryByGid(Integer gCodeId) {
        LitemallProductFactoryExample example = new LitemallProductFactoryExample();
        example.or().andGoodsCodeIdEqualTo(gCodeId);
        return litemallProductFactoryMapper.selectByExample(example);
    }
    public List<LitemallProductFactory> queryByPid(Integer Pid) {
        LitemallProductFactoryExample example = new LitemallProductFactoryExample();
        example.or().andFactoryIdEqualTo(Pid);
        return litemallProductFactoryMapper.selectByExample(example);
    }

    public String selInventory(Integer goodsCodeId) {
        //TODO
        return litemallProductFactoryMapper.selInventory(goodsCodeId);
    }

    public LitemallProductFactory getOneByGoodId(Integer goodsCodeId, Integer specCodeId) {
        return litemallProductFactoryMapper.getOneByGoodId(goodsCodeId, specCodeId);
    }

    public int reduceStock(Integer id, Short num){
        return litemallProductFactoryMapper.reduceStock(id, num);
    }

    public int addStockFactory(Integer goodsCodeId, Integer specCodeId, Integer plantId, Short num){
        return litemallProductFactoryMapper.addStock(goodsCodeId, specCodeId, plantId, num);
    }

    public int addStockSpecode(Integer goodsCodeId, Integer specCodeId, Short num){
        return litemallSpecodeProductMapper.addStock(goodsCodeId, specCodeId, num);
    }

    public int count() {
        LitemallProductFactoryExample example = new LitemallProductFactoryExample();
        example.or();
        return (int)litemallProductFactoryMapper.countByExample(example);
    }

    public List<Map<String,Object>> querySelective(String goodsCode, String codeName, String specsCode, LocalDate beginDate, LocalDate endDate, Integer page, Integer limit) {
        LocalDateTime startDateTime=null;
        LocalDateTime endDateTime=null;
        if (null != beginDate) {
            startDateTime = beginDate.atStartOfDay();
        }
        if (null != endDate) {
            endDateTime = endDate.atTime(LocalTime.MAX);
        }
        PageHelper.startPage(page, limit);
        return productFactoryMapper.reamStockList(goodsCode,codeName,specsCode,startDateTime,endDateTime);
    }

    public List<LitemallProductFactory> queryBySpecsCodeIdAndFid(List<Integer> specCodeIds, Integer factoryId) {
        LitemallProductFactoryExample example = new LitemallProductFactoryExample();
        example.or().andSpecCodeIdIn(specCodeIds).andFactoryIdEqualTo(factoryId);
        return litemallProductFactoryMapper.selectByExample(example);
    }

    public List<Map<String,Object>> warnList(String goodsCode, String codeName, String specsCode, LocalDate beginDate, LocalDate endDate, Integer page, Integer limit) {
        LocalDateTime startDateTime=null;
        LocalDateTime endDateTime=null;
        if (null != beginDate) {
            startDateTime = beginDate.atStartOfDay();
        }
        if (null != endDate) {
            endDateTime = endDate.atTime(LocalTime.MAX);
        }
        PageHelper.startPage(page, limit);
        return productFactoryMapper.warnStockList(goodsCode,codeName,specsCode,startDateTime,endDateTime);
    }

}
