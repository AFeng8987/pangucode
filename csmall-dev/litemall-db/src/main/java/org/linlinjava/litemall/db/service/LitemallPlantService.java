package org.linlinjava.litemall.db.service;


import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallPlantMapper;
import org.linlinjava.litemall.db.dao.LitemallProductFactoryMapper;
import org.linlinjava.litemall.db.domain.LitemallPlant;
import org.linlinjava.litemall.db.domain.LitemallPlantExample;
import org.linlinjava.litemall.db.domain.LitemallProductFactory;
import org.linlinjava.litemall.db.domain.LitemallProductFactoryExample;
import org.linlinjava.litemall.db.dto.LitemallPlantDto;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service("planService")
public class LitemallPlantService {
    @Resource
    private LitemallPlantMapper litemallPlantMapper;
    @Resource
    private LitemallProductFactoryMapper factoryMapper;


    public void add(LitemallPlant litemallPlant){
        litemallPlantMapper.insertSelective(litemallPlant);
    }


    public int update(LitemallPlant litemallPlant){
       return litemallPlantMapper.updateByPrimaryKeySelective(litemallPlant);
    }

    public int del(Integer id) {
      return litemallPlantMapper.deleteByPrimaryKey(id);
    }

    public List<LitemallPlant> sel(String name, String phone, String contacts, String addTime, Integer page, Integer limit) {
        if (!StringUtils.isEmpty(name)) {
            name = "%" + name + "%";
        }
        if (!StringUtils.isEmpty(contacts)) {
            contacts = "%" + contacts + "%";
        }
        if (!StringUtils.isEmpty(addTime)) {
            addTime = addTime + "%";
        }
        PageHelper.startPage(page, limit);
        return litemallPlantMapper.sel(name, phone, contacts, addTime);
    }

    public LitemallPlant findById(Integer factory) {
        return  litemallPlantMapper.selectByPrimaryKey(factory);
    }

    public LitemallPlant findByPlantName(String plantName) {
        LitemallPlantExample example=new LitemallPlantExample();
        example.or().andPlantNameEqualTo(plantName);
        return  litemallPlantMapper.selectOneByExample(example);
    }
}
