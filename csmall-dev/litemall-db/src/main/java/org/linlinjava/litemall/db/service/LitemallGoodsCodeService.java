package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.linlinjava.litemall.db.dao.LitemallGoodsCodeMapper;
import org.linlinjava.litemall.db.domain.LitemallGoodsCode;
import org.linlinjava.litemall.db.domain.LitemallGoodsCode.Column;
import org.linlinjava.litemall.db.domain.LitemallGoodsCodeExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallGoodsCodeService {
    Column[] columns = new Column[]{Column.id, Column.goodsCode, Column.codeName};
    @Resource
    private LitemallGoodsCodeMapper goodsCodeMapper;


    /**
     * 获取所有分类
     *
     * @param page
     * @param size
     * @return
     */
    public List<LitemallGoodsCode> querySelective(String goodsCode, String codeName,Integer page, Integer size) {
        LitemallGoodsCodeExample example = new LitemallGoodsCodeExample();
        LitemallGoodsCodeExample.Criteria criteria=example.createCriteria();

        if (StringUtils.isNotBlank(goodsCode)){
            criteria.andGoodsCodeLike("%"+goodsCode+"%");
        }
        if (StringUtils.isNotBlank(codeName)){
            criteria.andCodeNameLike("%"+codeName+"%");
        }
        example.setOrderByClause("add_time desc");
        PageHelper.startPage(page, size);
        return goodsCodeMapper.selectByExampleSelective(example, columns);
    }


    public void create(LitemallGoodsCode goodsCode) {
        goodsCode.setAddTime(LocalDateTime.now());
        goodsCode.setUpdateTime(LocalDateTime.now());
        goodsCodeMapper.insertSelective(goodsCode);
    }

    public boolean checkCode(String code) {
        LitemallGoodsCodeExample example = new LitemallGoodsCodeExample();
        example.or().andGoodsCodeEqualTo(code);
        return goodsCodeMapper.countByExample(example)>0?true:false;
    }

    public void deleteById(Integer id) {
        goodsCodeMapper.deleteByPrimaryKey(id);
    }

    /**
     * 获取某个商品信息,包含完整信息
     *
     * @param id
     * @return
     */
    public LitemallGoodsCode findById(Integer id) {
        return goodsCodeMapper.selectByPrimaryKey(id);
    }

    public int updateById(LitemallGoodsCode goodsCode) {
        goodsCode.setUpdateTime(LocalDateTime.now());
        return goodsCodeMapper.updateByPrimaryKeySelective(goodsCode);
    }

}
