package org.linlinjava.litemall.db.service;

import org.linlinjava.litemall.db.dao.LitemallGoodsSpecMapper;
import org.linlinjava.litemall.db.domain.LitemallGoodsSpec;
import org.linlinjava.litemall.db.domain.LitemallGoodsSpec.Column;
import org.linlinjava.litemall.db.domain.LitemallGoodsSpecExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LitemallGoodsSpecService {

    private Column[] columns = new Column[]{Column.id,Column.goodsCodeId,Column.spec,Column.value};
    
    @Resource
    private LitemallGoodsSpecMapper litemallGoodsSpecMapper;


    public List<LitemallGoodsSpec> queryByGid(Integer id) {
        LitemallGoodsSpecExample example = new LitemallGoodsSpecExample();
        example.or().andGoodsCodeIdEqualTo(id);
        return litemallGoodsSpecMapper.selectByExample(example);
    }

    public LitemallGoodsSpec findById(Integer id) {
        return litemallGoodsSpecMapper.selectByPrimaryKey(id);
    }

    public void deleteByCodeId(Integer gid) {
        LitemallGoodsSpecExample example = new LitemallGoodsSpecExample();
        example.or().andGoodsCodeIdEqualTo(gid);
        litemallGoodsSpecMapper.deleteByExample(example);
    }

    public int batchInsert(List<LitemallGoodsSpec> goodsSpecList) {
        return litemallGoodsSpecMapper.batchInsertSelective(goodsSpecList,Column.goodsCodeId,Column.spec,Column.value);
    }


}
