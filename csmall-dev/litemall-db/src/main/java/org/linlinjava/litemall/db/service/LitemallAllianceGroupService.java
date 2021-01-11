package org.linlinjava.litemall.db.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.linlinjava.litemall.db.dao.LitemallAllianceGroupMapper;
import org.linlinjava.litemall.db.dao.LitemallOrderMapper;
import org.linlinjava.litemall.db.domain.LitemallAllianceGroup;
import org.linlinjava.litemall.db.domain.LitemallAllianceGroupExample;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.domain.LitemallOrderExample;
import org.linlinjava.litemall.db.dto.LitemallAllianceGroupDto;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LitemallAllianceGroupService {
    @Resource
    private LitemallAllianceGroupMapper allianceGroupMapper;
    @Resource
    private LitemallOrderMapper litemallOrderMapper;


    public List<String> groupName() {
        List<String> list = allianceGroupMapper.groupName();
        return list;
    }

    public void groupAdd(LitemallAllianceGroup allianceGroup) {
        allianceGroup.setAddTime(LocalDateTime.now());
        allianceGroupMapper.insertSelective(allianceGroup);
    }

    public void groupUpdate(LitemallAllianceGroup allianceGroup) {
        allianceGroupMapper.updateByPrimaryKeySelective(allianceGroup);
    }

    public void groupDel(Integer id) {
        allianceGroupMapper.logicalDeleteByPrimaryKey(id);
    }

    public LitemallAllianceGroup findById(Integer id) {
        LitemallAllianceGroupExample example = new LitemallAllianceGroupExample();
        example.or().andIdEqualTo(id);
        return allianceGroupMapper.selectOneByExample(example);
    }

    public LitemallAllianceGroup groupDetail(Integer id) {
        LitemallAllianceGroupExample example = new LitemallAllianceGroupExample();
        example.or().andIdEqualTo(id);
        LitemallAllianceGroup allianceGroup = allianceGroupMapper.selectOneByExample(example);


        return allianceGroup;
    }

    public LitemallAllianceGroup selByAddress(String address) {
        address = "%" + address + "%";
        return allianceGroupMapper.selByAddress(address);
    }

}
