package org.linlinjava.litemall.admin.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.LitemallGoodsCode;
import org.linlinjava.litemall.db.domain.LitemallProductFactory;
import org.linlinjava.litemall.db.domain.LitemallSpecodeProduct;
import org.linlinjava.litemall.db.dto.LitemallPlantDto;
import org.linlinjava.litemall.db.domain.LitemallPlant;
import org.linlinjava.litemall.db.service.LitemallGoodsCodeService;
import org.linlinjava.litemall.db.service.LitemallPlantService;
import org.linlinjava.litemall.db.service.LitemallProductFactoryService;
import org.linlinjava.litemall.db.service.LitemallSpecodeProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminPlantService {

    @Autowired
    private LitemallPlantService plantService;
    @Autowired
    private LitemallGoodsCodeService goodsCodeService;
    @Autowired
    private LitemallSpecodeProductService specodeProductService;
    @Autowired
    private LitemallProductFactoryService factoryService;
    @Autowired
    private LogHelper logHelper;

    private static LitemallPlant DtoToModel(LitemallPlantDto plantDto) {
        LitemallPlant litemallPlant = new LitemallPlant();
        if (!StringUtils.isEmpty(plantDto.getId())) {
            litemallPlant.setId(plantDto.getId());
        }
        litemallPlant.setPlantName(plantDto.getPlantName());
        litemallPlant.setPlantContacts(plantDto.getPlantContacts());
        litemallPlant.setPlantPhone(plantDto.getPlantPhone());
        litemallPlant.setPlantAddress(plantDto.getPlantAddress());
        litemallPlant.setSendAddress(plantDto.getSendAddress());
        litemallPlant.setSendContacts(plantDto.getSendContacts());
        litemallPlant.setSendPhone(plantDto.getSendPhone());
        litemallPlant.setReceiveAddress(plantDto.getReceiveAddress());
        litemallPlant.setReceiveContacts(plantDto.getReceiveContacts());
        litemallPlant.setReceivePhone(plantDto.getReceivePhone());
        litemallPlant.setAddTime(LocalDateTime.now());
        return litemallPlant;
    }

    private static LitemallPlantDto ModelToDto(LitemallPlant plant) {
        LitemallPlantDto dto = new LitemallPlantDto();

        dto.setId(plant.getId());
        dto.setPlantName(plant.getPlantName());
        dto.setPlantContacts(plant.getPlantContacts());
        dto.setPlantPhone(plant.getPlantPhone());
        dto.setPlantAddress(plant.getPlantAddress());
        dto.setSendAddress(plant.getSendAddress());
        dto.setSendContacts(plant.getSendContacts());
        dto.setSendPhone(plant.getSendPhone());
        dto.setReceiveAddress(plant.getReceiveAddress());
        dto.setReceiveContacts(plant.getReceiveContacts());
        dto.setReceivePhone(plant.getReceivePhone());
        dto.setAddTime(plant.getAddTime());
        dto.setType(0);
        dto.setInventory(0);
        dto.setSales(0);
        return dto;
    }

    @Transactional
    public Object add(LitemallPlant plant) {
        LitemallPlant litemallPlant=plantService.findByPlantName(plant.getPlantName());
        if (ObjectUtils.isNotEmpty(litemallPlant)){
            return ResponseUtil.badArgumentValue("工厂名称已存在");
        }
        plant.setAddTime(LocalDateTime.now());
        plant.setDeleted(false);
        plantService.add(plant);
        logHelper.logGeneralSucceed("添加工厂","工厂名称："+plant.getPlantName());
        return ResponseUtil.ok();
    }

    @Transactional
    public Object update(LitemallPlant plant) {
        LitemallPlant updatePlant=plantService.findById(plant.getId());
        if (null== updatePlant){
            return ResponseUtil.badArgumentValue("修改数据不存在");
        }
        if (plantService.update(plant)==0){
            return ResponseUtil.updatedDataFailed();
        }
        logHelper.logGeneralSucceed("编辑工厂","工厂名称："+plant.getPlantName());
        return  ResponseUtil.ok();
    }

    public Object del(Integer id) {
        List<LitemallProductFactory> factoryList=factoryService.queryByPid(id);

        if (CollectionUtils.isEmpty(factoryList)){
            plantService.del(id);
            logHelper.logGeneralSucceed("删除工厂");
            return ResponseUtil.ok();
        }else {
            LitemallGoodsCode goodsCode=goodsCodeService.findById(factoryList.get(0).getGoodsCodeId());
            LitemallSpecodeProduct specodeProduct=specodeProductService.findById(factoryList.get(0).getSpecCodeId());
            return ResponseUtil.fail(500,"该工厂供货商品编码名称："+goodsCode.getCodeName()+"， 规格编码："+specodeProduct.getSpecCode()+"，暂不支持删除");
        }
    }

    public List<LitemallPlant> sel(String name, String phone, String contacts, String addTime, Integer page, Integer limit) {
        return plantService.sel(name, phone, contacts, addTime, page, limit);
    }

    public LitemallPlant detail(Integer id) {
        return plantService.findById(id);
    }

}
