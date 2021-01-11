package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.ActivityMapper;
import org.linlinjava.litemall.db.dao.LitemallActivityMapper;
import org.linlinjava.litemall.db.domain.LitemallActivity;
import org.linlinjava.litemall.db.domain.LitemallActivityExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LitemallActivityService {
    @Resource
    private ActivityMapper activityMapper;
    @Resource
    private LitemallActivityMapper litemallActivityMapper;

    public List<LitemallActivity> activityList(Integer page, Integer limit) {
        LitemallActivityExample example=new LitemallActivityExample();
        PageHelper.startPage(page, limit);
        return litemallActivityMapper.selectByExample(example);
    }

    public LitemallActivity getOne(Integer id) {
        return litemallActivityMapper.selectByPrimaryKey(id);
    }

    public int activityUpdate(LitemallActivity litemallActivity) {
       return litemallActivityMapper.updateByPrimaryKey(litemallActivity);
    }


}
