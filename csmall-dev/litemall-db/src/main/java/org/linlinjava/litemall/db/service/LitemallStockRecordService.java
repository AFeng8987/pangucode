package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.linlinjava.litemall.db.dao.LitemallGoodsCodeMapper;
import org.linlinjava.litemall.db.dao.LitemallSpecodeProductMapper;
import org.linlinjava.litemall.db.dao.LitemallStockRecordMapper;
import org.linlinjava.litemall.db.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class LitemallStockRecordService {

    @Resource
    private LitemallStockRecordMapper stockRecordMapper;
    @Resource
    private LitemallSpecodeProductService specsCodeProductService;
    @Resource
    private LitemallGoodsCodeService goodsCodeService;
    @Autowired
    private LitemallPlantService plantService;

    private DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * @param specsId 规格编码ID
     * @param factory  工厂ID
     * @param number   数量
     * @param status   1：出库，2入库
     * @param remarks  操作信息“下单，取消订单，过期未支付，管理员库存调整”
     * @param operator  下单人或者后台管理人员
     * @return
     */
    public int add(Integer specsId,Integer factory,Integer number,int status,String remarks,String operator) {
        LitemallSpecodeProduct specsCodeProduct=specsCodeProductService.findById(specsId);
        LitemallPlant plant= plantService.findById(factory);
        LitemallGoodsCode goodsCode=goodsCodeService.findById(specsCodeProduct.getGoodsCodeId());
        LitemallStockRecord stockRecord=new LitemallStockRecord();
        stockRecord.setGoodsCode(goodsCode.getGoodsCode());
        stockRecord.setCodeName(goodsCode.getCodeName());
        stockRecord.setSpecsCode(specsCodeProduct.getSpecCode());
        stockRecord.setSpecsDesc(specsCodeProduct.getSpecsDesc());
        stockRecord.setPlantName(plant.getPlantName());
        stockRecord.setNumber(number);
        stockRecord.setUrl(specsCodeProduct.getUrl());
        stockRecord.setStatus(status);
        stockRecord.setAddTime(LocalDateTime.now());
        stockRecord.setOperator(operator);
        stockRecord.setRemarks(remarks);
        return stockRecordMapper.insert(stockRecord);
    }


    public void batchInsert(List<LitemallStockRecord> data) {
        stockRecordMapper.batchInsert(data);
    }


    public List<LitemallStockRecord> querySelective(String goodsCode, String codeName, String specsCode, String planName, LocalDate beginDate, LocalDate endDate, Integer page, Integer limit, Integer status) {
        LitemallStockRecordExample example = new LitemallStockRecordExample();
        LitemallStockRecordExample.Criteria criteria = example.createCriteria();

        LocalDateTime startDateTime=null;
        LocalDateTime endDateTime=null;
        if (null != beginDate) {
            startDateTime = beginDate.atStartOfDay();
            criteria.andAddTimeGreaterThanOrEqualTo(startDateTime);
        }
        if (null != endDate) {
            endDateTime = endDate.atTime(LocalTime.MAX);
            criteria.andAddTimeLessThanOrEqualTo(endDateTime);
        }

        if (StringUtils.isNotBlank(goodsCode)) {
            criteria.andGoodsCodeEqualTo(goodsCode);
        }
        if (StringUtils.isNotBlank(codeName)) {
            criteria.andCodeNameLike("%" + codeName + "%");
        }
        if (StringUtils.isNotBlank(specsCode)) {
            criteria.andSpecsCodeEqualTo(specsCode);
        }
        if (StringUtils.isNotBlank(planName)) {
            criteria.andPlantNameEqualTo(planName);
        }
        criteria.andStatusEqualTo(status);
        PageHelper.startPage(page, limit);
        return stockRecordMapper.selectByExample(example);

    }
}
