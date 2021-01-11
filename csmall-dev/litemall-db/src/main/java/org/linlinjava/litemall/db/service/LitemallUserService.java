package org.linlinjava.litemall.db.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.RegExUtils;
import org.linlinjava.litemall.db.dao.LitemallAllianceMapper;
import org.linlinjava.litemall.db.dao.LitemallUserMapper;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.dto.LitemallUserDto;
import org.linlinjava.litemall.db.util.ShareCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LitemallUserService {
    @Resource
    private LitemallUserMapper userMapper;
    @Resource
    private LitemallAllianceMapper allianceMapper;
    @Autowired
    private LitemallCouponService couponService;
    @Autowired
    private LitemallCouponUserService couponUserService;
    @Resource
    private LitemallNodeRelationService nodeRelationService;

    /**
     * @param mobile,即账号
     * @param status     即状态 0停用或者1可用
     * @return nickname and username
     * @Description:根据手机号码和用户状态获取未被软删除掉的用户
     * @author kevin Tam
     */
    public LitemallUser queryByMobileAndStatus(String mobile, Integer status) {
        LitemallUserExample example = new LitemallUserExample();
        LitemallUserExample.Criteria criteria = example.createCriteria();
        if (null != status) {
            int newStatus = status;
            criteria.andStatusEqualTo((byte) newStatus);
        }
        criteria.andUsernameEqualTo(mobile).andDeletedEqualTo(false);
        return userMapper.selectOneByExampleSelective(example);
    }

    public LitemallUser findById(Integer userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    public LitemallUser checkById(Integer userId) {
        LitemallUserExample example = new LitemallUserExample();
        LitemallUserExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(userId).andDeletedEqualTo(false).andStatusEqualTo((byte)0);
        return userMapper.selectOneByExample(example);
    }

    public UserVo findUserVoById(Integer userId) {
        LitemallUser user = findById(userId);
        UserVo userVo = new UserVo();
        userVo.setNickname(user.getNickname());
        userVo.setAvatar(user.getAvatar());
        return userVo;
    }

    public LitemallUser queryByOid(String openId) {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andWeixinOpenidEqualTo(openId).andDeletedEqualTo(false);
        return userMapper.selectOneByExample(example);
    }

    /**
     * 用户注册赠送优惠券，插入node关系表
     *
     * @param user
     */
    @Transactional(rollbackFor = Exception.class)
    public void addUserAndNodeAndCoupon(LitemallUser user) {
        add(user);
        user.setInvitationCode(ShareCodeUtil.toSerialCode(user.getId()));
        updateById(user);
/*        //新用户注册送新人优惠券
        LitemallCouponUser couponUser = new LitemallCouponUser();
        couponUser.setUserId(user.getId());
        couponUser.setCouponId(1);
        LitemallCoupon couponNew = couponService.findById(1);
        couponUser.setStartTime(LocalDateTime.now());
        couponUser.setEndTime(LocalDateTime.now().plusDays(couponNew.getDays()));
        couponUserService.add(couponUser);
        //送推荐人优惠券,如果推荐人为0，即不用送，不为0即有推荐人
        if (0 != user.getReferralUserId()) {
            LitemallCouponUser couponReferralUser = new LitemallCouponUser();
            couponReferralUser.setUserId(user.getReferralUserId());
            couponReferralUser.setCouponId(2);
            LitemallCoupon rebateCoupon = couponService.findById(2);
            couponUser.setStartTime(LocalDateTime.now());
            couponUser.setEndTime(LocalDateTime.now().plusDays(rebateCoupon.getDays()));
            couponUserService.add(couponReferralUser);
        }*/
        //插入关系
        nodeRelationService.insertNewNodeRelation(user.getId(), user.getReferralUserId());
    }

    public void add(LitemallUser user) {
        user.setAddTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insertSelective(user);
    }

    public int updateById(LitemallUser user) {
        user.setUpdateTime(LocalDateTime.now());
        return userMapper.updateByPrimaryKeySelective(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void allianceAdd(LitemallAlliance alliance) {
        LitemallUser updateUser=new LitemallUser();
        updateUser.setId(alliance.getUserId());
        updateUser.setUserLevel((byte) 1);
        //生成新的邀请码
        updateUser.setInvitationCode(ShareCodeUtil.toSerialCode(alliance.getUserId()));
        updateById(updateUser);
        alliance.setAddTime(LocalDateTime.now());
        allianceMapper.insertSelective(alliance);
        //添加成为加盟商，传true
        nodeRelationService.updateNodeRelation(alliance.getUserId(), true);
    }

    public LitemallAlliance selAlliance(Integer userId) {
        LitemallAllianceExample example = new LitemallAllianceExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        return allianceMapper.selectOneByExample(example);
    }

    public LitemallUser selByUserName(String userName) {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andUsernameEqualTo(userName);
        return userMapper.selectOneByExample(example);
    }

    public LitemallUser selByInvitationCode(String invitationCode) {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andInvitationCodeEqualTo(invitationCode);
        return userMapper.selectOneByExample(example);
    }

    public LitemallAlliance selAllianceByAddress(String address) {
        return allianceMapper.selByAddress(address);
    }

    @Transactional(rollbackFor = Exception.class)
    public void allianceDel(Integer id) {
        LitemallUser user = userMapper.selectByPrimaryKey(id);
        user.setUserLevel((byte) 0);
        userMapper.updateByPrimaryKey(user);
        allianceMapper.delByUserId(user.getId());
        //false为删除加盟
        nodeRelationService.updateNodeRelation(user.getId(), false);
    }

    public long allianceTotalByGroupId(Integer groupId) {
        LitemallAllianceExample example = new LitemallAllianceExample();
        example.or().andGroupIdEqualTo(groupId);
        return allianceMapper.countByExample(example);
    }

    public void allianceUpdate(LitemallAlliance alliance) {
        allianceMapper.updateByPrimaryKey(alliance);
    }

    public List<LitemallUser> querySelective(String username, String nickName, Integer pageNum, Integer pageSize, String sort, String order, String userLevel) {
        LitemallUserExample example = new LitemallUserExample();
        LitemallUserExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(username)) {
            criteria.andUsernameEqualTo(username);
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(nickName)) {
            criteria.andNicknameLike("%" + nickName + "%");
        }
        if (!StringUtils.isEmpty(userLevel)) {
            criteria.andUserLevelEqualTo(Byte.valueOf(userLevel));
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }
        PageHelper.startPage(pageNum, pageSize);
        List<LitemallUser> liteMallUsers = userMapper.selectByExampleSelective(example, LitemallUser.Column.id,LitemallUser.Column.username,
                LitemallUser.Column.nickname,LitemallUser.Column.userLevel,LitemallUser.Column.addTime,LitemallUser.Column.updateTime);
        return liteMallUsers;
    }

    public int count() {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andDeletedEqualTo(false);

        return (int) userMapper.countByExample(example);
    }

    public List<LitemallUser> queryByUsername(String username) {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andUsernameEqualTo(username).andDeletedEqualTo(false);
        return userMapper.selectByExample(example);
    }

    public boolean checkByUsername(String username) {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andUsernameEqualTo(username).andDeletedEqualTo(false);
        return userMapper.countByExample(example) != 0;
    }

    public List<LitemallUser> queryByMobile(String mobile) {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andMobileEqualTo(mobile).andDeletedEqualTo(false);
        return userMapper.selectByExample(example);
    }

    public List<LitemallUser> queryByOpenid(String openid) {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andWeixinOpenidEqualTo(openid).andDeletedEqualTo(false);
        return userMapper.selectByExample(example);
    }

    public void deleteById(Integer id) {
        userMapper.logicalDeleteByPrimaryKey(id);
    }

    private LitemallUserDto ModelToDto(LitemallUser user) {
        LitemallUserDto dto = new LitemallUserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setUserLevel(user.getUserLevel());
        dto.setNickname(user.getNickname());
        dto.setAvatar(user.getAvatar());
        dto.setStatus(user.getStatus());
        dto.setAddTime(user.getAddTime());
        dto.setInvitationCode(user.getInvitationCode());
        dto.setReferralUserId(user.getReferralUserId());
        dto.setUpdateTime(user.getUpdateTime());
        return dto;
    }

    public static boolean isIDNumber(String IDNumber) {
        if (IDNumber == null || "".equals(IDNumber)) {
            return false;
        }
        // 定义判别用户身份证号的正则表达式（15位或者18位，最后一位可以为字母）
        String regularExpression = "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|" +
                "(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";
        //假设18位身份证号码:41000119910101123X  410001 19910101 123X
        //^开头
        //[1-9] 第一位1-9中的一个      4
        //\\d{5} 五位数字           10001（前六位省市县地区）
        //(18|19|20)                19（现阶段可能取值范围18xx-20xx年）
        //\\d{2}                    91（年份）
        //((0[1-9])|(10|11|12))     01（月份）
        //(([0-2][1-9])|10|20|30|31)01（日期）
        //\\d{3} 三位数字            123（第十七位奇数代表男，偶数代表女）
        //[0-9Xx] 0123456789Xx其中的一个 X（第十八位为校验值）
        //$结尾

        //假设15位身份证号码:410001910101123  410001 910101 123
        //^开头
        //[1-9] 第一位1-9中的一个      4
        //\\d{5} 五位数字           10001（前六位省市县地区）
        //\\d{2}                    91（年份）
        //((0[1-9])|(10|11|12))     01（月份）
        //(([0-2][1-9])|10|20|30|31)01（日期）
        //\\d{3} 三位数字            123（第十五位奇数代表男，偶数代表女），15位身份证不含X
        //$结尾


        boolean matches = IDNumber.matches(regularExpression);

        //判断第18位校验值
        if (matches) {

            if (IDNumber.length() == 18) {
                try {
                    char[] charArray = IDNumber.toCharArray();
                    //前十七位加权因子
                    int[] idCardWi = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
                    //这是除以11后，可能产生的11位余数对应的验证码
                    String[] idCardY = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
                    int sum = 0;
                    for (int i = 0; i < idCardWi.length; i++) {
                        int current = Integer.parseInt(String.valueOf(charArray[i]));
                        int count = current * idCardWi[i];
                        sum += count;
                    }
                    char idCardLast = charArray[17];
                    int idCardMod = sum % 11;
                    if (idCardY[idCardMod].toUpperCase().equals(String.valueOf(idCardLast).toUpperCase())) {
                        return true;
                    } else {
                        return false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

        }
        return matches;
    }

    public Map<String, Object> userInfo(LitemallUser user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("nickName", user.getNickname());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("invitationCode", user.getInvitationCode());
        if (user.getUserLevel().byteValue() == 1) {
            userInfo.put("alliance", true);
        } else {
            userInfo.put("alliance", false);
        }
        return userInfo;

    }


    public List<Map<String, Object>> subordinateUser(int userId, Integer page, Integer limit) {
        PageHelper.startPage(page,limit);
        return  nodeRelationService.subordinateUser(userId);
    }

    public List<Map<String, Object>> nonSubordinateUser(int userId, Integer page, Integer limit) {
        PageHelper.startPage(page,limit);
        return  nodeRelationService.nonSubordinateUser(userId);
    }
}
