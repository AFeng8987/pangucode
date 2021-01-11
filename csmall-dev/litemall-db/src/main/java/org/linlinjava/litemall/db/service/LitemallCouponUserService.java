package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallCouponUserMapper;
import org.linlinjava.litemall.db.domain.LitemallCouponUser;
import org.linlinjava.litemall.db.domain.LitemallCouponUserExample;
import org.linlinjava.litemall.db.util.CouponUserConstant;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallCouponUserService {
    @Resource
    private LitemallCouponUserMapper couponUserMapper;

    public Integer countCoupon(Integer couponId) {
        LitemallCouponUserExample example = new LitemallCouponUserExample();
        example.or().andCouponIdEqualTo(couponId).andDeletedEqualTo(false);
        return (int)couponUserMapper.countByExample(example);
    }

    public Integer countUserAndCoupon(Integer userId, Integer couponId) {
        LitemallCouponUserExample example = new LitemallCouponUserExample();
        example.or().andUserIdEqualTo(userId).andCouponIdEqualTo(couponId).andDeletedEqualTo(false);
        return (int)couponUserMapper.countByExample(example);
    }

    public void add(LitemallCouponUser couponUser) {
        couponUser.setAddTime(LocalDateTime.now());
        couponUser.setUpdateTime(LocalDateTime.now());
        couponUserMapper.insertSelective(couponUser);
    }

    public List<LitemallCouponUser> queryList(Integer userId, Integer couponId, Short status, Integer page, Integer size, String sort, String order) {
        LitemallCouponUserExample example = new LitemallCouponUserExample();
        LitemallCouponUserExample.Criteria criteria = example.createCriteria();
        if (userId != null) {
            criteria.andUserIdEqualTo(userId);
        }
        if(couponId != null){
            criteria.andCouponIdEqualTo(couponId);
        }
        if (status != null) {
            criteria.andStatusEqualTo(status);
        }
        if (status.compareTo(CouponUserConstant.STATUS_EXPIRED)==0) {
            criteria.andStatusEqualTo(status);
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        if (!StringUtils.isEmpty(page) && !StringUtils.isEmpty(size)) {
            PageHelper.startPage(page, size);
        }

        return couponUserMapper.selectByExample(example);
    }

    public List<LitemallCouponUser> queryMyList(Integer userId,  Short status, Integer page, Integer size) {
        LitemallCouponUserExample example = new LitemallCouponUserExample();
        LitemallCouponUserExample.Criteria criteria = example.createCriteria();
        if (userId != null) {
            criteria.andUserIdEqualTo(userId);
        }
        if (status.equals(CouponUserConstant.STATUS_EXPIRED)) {
            criteria.andEndTimeLessThan(LocalDateTime.now());
        }
        if (status.equals(CouponUserConstant.STATUS_USABLE)) {
            criteria.andEndTimeGreaterThan(LocalDateTime.now());
        }
        criteria.andStatusEqualTo(status);
        criteria.andDeletedEqualTo(false);
        example.setOrderByClause("add_time desc");
        if (!StringUtils.isEmpty(page) && !StringUtils.isEmpty(size)) {
            PageHelper.startPage(page, size);
        }
        return couponUserMapper.selectByExample(example);
    }



    public LitemallCouponUser queryOne(Integer userId, Integer couponId) {
        List<LitemallCouponUser> couponUserList = queryList(userId, couponId, CouponUserConstant.STATUS_USABLE, 1, 1, "add_time", "desc");
        if(couponUserList.size() == 0){
            return null;
        }
        return couponUserList.get(0);
    }

    public LitemallCouponUser findById(Integer id) {

        return couponUserMapper.selectByPrimaryKey(id);
    }

    public LitemallCouponUser findNotUsedById(Integer id) {
        LitemallCouponUserExample example = new LitemallCouponUserExample();
        example.or().andIdEqualTo(id).andStatusEqualTo(CouponUserConstant.STATUS_USABLE).andEndTimeGreaterThan(LocalDateTime.now()).andDeletedEqualTo(false);
        return couponUserMapper.selectOneByExample(example);
    }


    public int update(LitemallCouponUser couponUser) {
        couponUser.setUpdateTime(LocalDateTime.now());
        return couponUserMapper.updateByPrimaryKeySelective(couponUser);
    }

    public List<LitemallCouponUser> queryExpired() {
        LitemallCouponUserExample example = new LitemallCouponUserExample();
        example.or().andStatusEqualTo(CouponUserConstant.STATUS_USABLE).andEndTimeLessThan(LocalDateTime.now()).andDeletedEqualTo(false);
        return couponUserMapper.selectByExample(example);
    }

    public List<LitemallCouponUser> queryByPayOrderSnAndUserId(String payOrderSn,Integer userId) {
        LitemallCouponUserExample example = new LitemallCouponUserExample();
        example.or().andStatusEqualTo(CouponUserConstant.STATUS_USED).andDeletedEqualTo(false).andPayOrderSnEqualTo(payOrderSn).andUserIdEqualTo(userId);
        return couponUserMapper.selectByExample(example);
    }


    public List<LitemallCouponUser> selectList(Integer userId) {
        LitemallCouponUserExample example = new LitemallCouponUserExample();
        example.or().andStatusEqualTo(CouponUserConstant.STATUS_USABLE).andDeletedEqualTo(false).andUserIdEqualTo(userId).andEndTimeGreaterThan(LocalDateTime.now());
        return couponUserMapper.selectByExample(example);
    }
}
