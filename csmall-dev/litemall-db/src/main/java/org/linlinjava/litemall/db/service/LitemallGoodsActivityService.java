package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.GoodsActivityMapper;

import org.linlinjava.litemall.db.dao.LitemallGoodsActivityMapper;
import org.linlinjava.litemall.db.dao.LitemallGoodsMapper;
import org.linlinjava.litemall.db.domain.LitemallActivityExample;
import org.linlinjava.litemall.db.domain.LitemallGoods;
import org.linlinjava.litemall.db.domain.LitemallGoodsActivity;
import org.linlinjava.litemall.db.domain.LitemallGoodsActivityExample;
import org.linlinjava.litemall.db.dto.LitemallGoodsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LitemallGoodsActivityService {
    @Autowired
    private LitemallSystemConfigService systemConfigService;
    @Autowired
    private LitemallGoodsService goodsService;
    @Resource
    private LitemallGoodsActivityMapper litemallGoodsActivityMapper;
    @Resource
    private GoodsActivityMapper goodsActivityMapper;
    @Resource
    private LitemallGoodsMapper litemallGoodsMapper;


    public List<String> list(String activityId, String goodsId, String goodsName) {
        if (!StringUtils.isEmpty(goodsName)) {
            goodsName = "%" + goodsName + "%";
        }

        return goodsActivityMapper.list(activityId, goodsId, goodsName);
    }

    public void del(Integer activityId, Integer goodsId) {
        goodsActivityMapper.del(activityId, goodsId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void add(Integer activityId, List<Integer> goodsIds) {
        goodsActivityMapper.delList(activityId, goodsIds);
        List<LitemallGoodsActivity> goodsActivityList = new ArrayList<>(goodsIds.size());
        for (Integer goodsId:goodsIds ) {
            LitemallGoodsActivity goodsActivity=new LitemallGoodsActivity();
            goodsActivity.setGoodsId(goodsId);
            goodsActivity.setActivityId(activityId);
            goodsActivity.setAddTime(LocalDateTime.now());
            goodsActivityList.add(goodsActivity);
        }
        goodsActivityMapper.addList(goodsActivityList);
    }


    public List<LitemallGoodsDto> activityGoods(Integer activityId, Integer goodsId, String goodsName) {
        String freight = systemConfigService.selByKey("litemall_express_freight_value");
        if (!StringUtils.isEmpty(goodsName)) {
            goodsName = "%" + goodsName + "%";
        }
        List<LitemallGoodsDto> dtoList = goodsService.activityAPPGoods(activityId, goodsId, goodsName);
        dtoList.stream().forEach(item -> item.setFreight(freight));
        return dtoList;
    }

    public List<LitemallGoodsActivity> queryByActivityId(Integer activityId, List<Integer> goodsIds) {
        LitemallGoodsActivityExample example=new LitemallGoodsActivityExample();
        example.or().andActivityIdEqualTo(activityId).andGoodsIdIn(goodsIds);
        return litemallGoodsActivityMapper.selectByExample(example);
    }

    public List<LitemallGoodsDto> activityQueryGoods(Integer activityId,String goodsCode,  String name,Integer page, Integer limit) {
        PageHelper.startPage(page,limit);
        return goodsService.activityQueryGoods(activityId,goodsCode,name);
    }


}
