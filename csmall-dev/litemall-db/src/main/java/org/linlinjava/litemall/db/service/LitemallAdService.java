package org.linlinjava.litemall.db.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.linlinjava.litemall.db.dao.LitemallAdMapper;
import org.linlinjava.litemall.db.dao.LitemallGoodsMapper;
import org.linlinjava.litemall.db.domain.LitemallAd;
import org.linlinjava.litemall.db.domain.LitemallAdExample;
import org.linlinjava.litemall.db.domain.LitemallGoods;
import org.linlinjava.litemall.db.dto.LitemallAdDto;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LitemallAdService {
    @Resource
    private LitemallAdMapper adMapper;
    @Resource
    private LitemallGoodsMapper litemallGoodsMapper;

    private LitemallAdDto modelToDto(LitemallAd ad) {
        LitemallAdDto dto = new LitemallAdDto();
        dto.setId(ad.getId());
        dto.setName(ad.getName());
        dto.setUrl(ad.getUrl());
        dto.setPosition(ad.getPosition());
        dto.setContent(ad.getContent());
        dto.setStatus(ad.getStatus());
        dto.setEnabled(ad.getEnabled());
        dto.setAddTime(ad.getAddTime());
        dto.setUpdateTime(ad.getUpdateTime());
        dto.setDeleted(ad.getDeleted());
        dto.setSortOrder(ad.getSortOrder());
        if (ad.getStatus()) {
            dto.setGoodsName("-");
            dto.setGoodsPic("-");
            dto.setLink(ad.getLink());
        }else {
            LitemallGoods litemallGoods = litemallGoodsMapper.selectByPrimaryKey(Integer.valueOf(ad.getLink()));
            dto.setGoodsName(litemallGoods.getName());
            dto.setGoodsPic(litemallGoods.getPicUrl());
            dto.setLink("-");
        }

        return dto;
    }

    public List<LitemallAd> queryIndex() {
        LitemallAdExample example = new LitemallAdExample();
        example.or().andPositionEqualTo((byte) 1).andDeletedEqualTo(false).andEnabledEqualTo(true);
        example.setOrderByClause("sort_order asc,update_time desc");
        return adMapper.selectByExample(example);
    }

    public PageInfo querySelective(String name, String content, Integer pageNum, Integer pageSize, String sort, String order) {
        LitemallAdExample example = new LitemallAdExample();
        LitemallAdExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        if (!StringUtils.isEmpty(content)) {
            criteria.andContentLike("%" + content + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause("sort_order asc,"+sort + " " + order);
        }

        List<LitemallAd> adList = adMapper.selectByExample(example);

        List<LitemallAdDto> adDtoList = adList.stream().map(item -> modelToDto(item)).collect(Collectors.toList());

        //创建Page类
        Page page = new Page(pageNum, pageSize);
        //为Page类中的total属性赋值
        page.setTotal(adDtoList.size());
        //计算当前需要显示的数据下标起始值
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, adDtoList.size());
        //从链表中截取需要显示的子链表，并加入到Page
        page.addAll(adDtoList.subList(startIndex, endIndex));
        //以Page创建PageInfo
        PageInfo pageInfo = new PageInfo<>(page);

        return pageInfo;
    }


    public int updateById(LitemallAd ad) {
        ad.setUpdateTime(LocalDateTime.now());
        return adMapper.updateByPrimaryKeySelective(ad);
    }

    public void deleteById(Integer id) {
        adMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallAd ad) {
        ad.setAddTime(LocalDateTime.now());
        ad.setUpdateTime(LocalDateTime.now());
        adMapper.insertSelective(ad);
    }

    public LitemallAd findById(Integer id) {
        return adMapper.selectByPrimaryKey(id);
    }
}
