package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.ObjectUtils;
import org.linlinjava.litemall.db.dao.LitemallAddressMapper;
import org.linlinjava.litemall.db.domain.LitemallAddress;
import org.linlinjava.litemall.db.domain.LitemallAddressExample;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallAddressService {
    @Resource
    private LitemallAddressMapper addressMapper;

    public List<LitemallAddress> queryByUid(Integer uid) {
        LitemallAddressExample example = new LitemallAddressExample();
        example.or().andUserIdEqualTo(uid).andDeletedEqualTo(false);
        example.setOrderByClause("is_default desc,update_time desc");
        return addressMapper.selectByExample(example);
    }

    public LitemallAddress query(Integer userId, Integer id) {
        LitemallAddressExample example = new LitemallAddressExample();
        example.or().andIdEqualTo(id).andUserIdEqualTo(userId).andDeletedEqualTo(false);
        return addressMapper.selectOneByExample(example);
    }

    @Transactional
    public int add(LitemallAddress address) {
        //如果新增地址为默认地址，那么取消之前的默认地址
        if (address.getIsDefault()) {
            cancelIsDefault(address.getUserId(), null);
        }
        address.setAddTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());
        return addressMapper.insertSelective(address);
    }

    @Transactional
    public int update(LitemallAddress address) {
        //如果新增地址为默认地址，那么取消之前的默认地址
        if (address.getIsDefault()) {
            cancelIsDefault(address.getUserId(), address.getId());
        }
        address.setUpdateTime(LocalDateTime.now());
        return addressMapper.updateByPrimaryKeySelective(address);
    }

    public void delete(Integer id) {
        addressMapper.logicalDeleteByPrimaryKey(id);
    }

    public LitemallAddress findDefault(Integer userId) {
        LitemallAddressExample example = new LitemallAddressExample();
        example.or().andUserIdEqualTo(userId).andIsDefaultEqualTo(true).andDeletedEqualTo(false);
        return addressMapper.selectOneByExample(example);
    }

    public void resetDefault(Integer userId) {
        LitemallAddress address = new LitemallAddress();
        address.setIsDefault(false);
        address.setUpdateTime(LocalDateTime.now());
        LitemallAddressExample example = new LitemallAddressExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        addressMapper.updateByExampleSelective(address, example);
    }

    /**
     * 如果ID刚好是默认地址，就不修改。不过ID不是默认地址将默认地址取消默认，
     *
     * @param userId
     * @param id  收货地址（修改的时候传，新增传null）
     */
    public void cancelIsDefault(Integer userId, Integer id) {
        LitemallAddressExample example = new LitemallAddressExample();
        example.or().andUserIdEqualTo(userId).andIsDefaultEqualTo(true).andDeletedEqualTo(false);
        LitemallAddress address = addressMapper.selectOneByExample(example);
        //是否有默认地址
        if (ObjectUtils.isNotEmpty(address)) {
            //有默认地址,且修改的时候，当前默认地址和修改的地址是同一个时，不修改直接return
            if (null != id && address.getId() == id)
                return;
            address.setIsDefault(false);
            address.setUpdateTime(LocalDateTime.now());
            addressMapper.updateByExample(address, example);
        }
    }
}
