package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.linlinjava.litemall.db.dao.GoodsMapper;
import org.linlinjava.litemall.db.dao.LitemallGoodsMapper;
import org.linlinjava.litemall.db.domain.LitemallGoods;
import org.linlinjava.litemall.db.domain.LitemallGoods.Column;
import org.linlinjava.litemall.db.domain.LitemallGoodsExample;
import org.linlinjava.litemall.db.dto.LitemallGoodsDto;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class LitemallGoodsService {
    Column[] columns = new Column[]{Column.id, Column.name, Column.brief, Column.picUrl, Column.isHot, Column.isNew, Column.counterPrice, Column.retailPrice,Column.sales};
    @Resource
    private LitemallGoodsMapper litemallGoodsMapper;

    @Resource
    private GoodsMapper goodsMapper;


    /**
     * 获取热卖商品
     *
     * @param offset
     * @param limit
     * @return
     */
    public List<LitemallGoods> queryByHot(int offset, int limit) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIsHotEqualTo(true).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        example.setOrderByClause("update_time desc");
        PageHelper.startPage(offset, limit);

        return litemallGoodsMapper.selectByExampleSelective(example, columns);
    }

    /**
     * 获取热卖商品，可查询
     * @param page
     * @param limit
     * @return
     */
    public List<Map<String, Object>> queryByHot(String goodsCode,String goodsName,int page, int limit) {
        if (StringUtils.isNotBlank(goodsCode)) {
            goodsCode="%" + goodsCode + "%";
        }
        if (StringUtils.isNotBlank(goodsName)) {
            goodsName="%" + goodsName + "%";
        }

        PageHelper.startPage(page, limit);
        return goodsMapper.queryHotGoodsList(goodsCode,  goodsName );
    }

    /**
     * 获取新品上市
     *
     * @param offset
     * @param limit
     * @return
     */
    public List<LitemallGoods> queryByNew(int offset, int limit) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIsNewEqualTo(true).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        example.setOrderByClause("add_time desc");
        PageHelper.startPage(offset, limit);

        return litemallGoodsMapper.selectByExampleSelective(example, columns);
    }

    /**
     * 获取分类下的商品
     *
     * @param catList
     * @param offset
     * @param limit
     * @return
     */
    public List<LitemallGoods> queryByCategory(List<Integer> catList, int offset, int limit) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andCategoryIdIn(catList).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        example.setOrderByClause("add_time  desc");
        PageHelper.startPage(offset, limit);

        return litemallGoodsMapper.selectByExampleSelective(example, columns);
    }

    /**
     * 获取分页下的商品
     *
     * @param page
     * @param limit
     * @return
     */
    public List<LitemallGoods> queryByPage(int page, int limit) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        example.setOrderByClause("update_time  desc");
        PageHelper.startPage(page, limit);

        return litemallGoodsMapper.selectByExampleSelective(example, columns);
    }

    /**
     * 获取首页分页下的商品
     *
     * @param page
     * @param limit
     * @return
     */
    public List<LitemallGoods> queryHomeByPage(int page, int limit) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIsOnSaleEqualTo(true).andIsHomeEqualTo(true).andDeletedEqualTo(false);
        example.setOrderByClause("home_sort_order asc, update_time  desc");
        PageHelper.startPage(page, limit);
        return litemallGoodsMapper.selectByExampleSelective(example, columns);
    }


    /**
     * 获取分类下的商品
     *
     * @param catId
     * @param offset
     * @param limit
     * @return
     */
    public List<LitemallGoods> queryByCategory(Integer catId, int offset, int limit) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andCategoryIdEqualTo(catId).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        example.setOrderByClause("add_time desc");
        PageHelper.startPage(offset, limit);

        return litemallGoodsMapper.selectByExampleSelective(example, columns);
    }

    /**
     * 获取二级类目下的在售商品
     *
     * @param categoryId
     * @return
     */
    public List<LitemallGoods> queryByCategory(Integer categoryId) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andCategoryIdEqualTo(categoryId).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        return litemallGoodsMapper.selectByExampleSelective(example, columns);
    }


    public List<LitemallGoods> querySelective(Integer catId, Integer brandId, String keywords, Boolean isHot, Boolean isNew, Integer offset, Integer limit, String sort, String order) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        LitemallGoodsExample.Criteria criteria1 = example.or();
        LitemallGoodsExample.Criteria criteria2 = example.or();

        if (null != catId && catId != 0) {
            criteria1.andCategoryIdEqualTo(catId);
            criteria2.andCategoryIdEqualTo(catId);
        }
        if (null != brandId) {
            criteria1.andBrandIdEqualTo(brandId);
            criteria2.andBrandIdEqualTo(brandId);
        }
        if (null != isNew ) {
            criteria1.andIsNewEqualTo(isNew);
            criteria2.andIsNewEqualTo(isNew);
        }
        if (null != isHot ) {
            criteria1.andIsHotEqualTo(isHot);
            criteria2.andIsHotEqualTo(isHot);
        }
        if (StringUtils.isNotBlank(keywords)) {
            criteria1.andKeywordsLike("%" + keywords + "%");
            criteria2.andNameLike("%" + keywords + "%");
        }
        criteria1.andIsOnSaleEqualTo(true);
        criteria2.andIsOnSaleEqualTo(true);
        criteria1.andDeletedEqualTo(false);
        criteria2.andDeletedEqualTo(false);

        if (StringUtils.isNotBlank(sort) && StringUtils.isNotBlank(order)) {
            if (sort.equals("synthesize")) {
                example.setOrderByClause("sales desc, retail_price desc");
            } else {
                example.setOrderByClause(sort + " " + order);
            }
        }
        PageHelper.startPage(offset, limit);
        return litemallGoodsMapper.selectByExampleSelective(example, columns);
    }

    public List<Map<String, Object>> queryList(Integer goodsId, String goodsCode, String goodsName, String categoryName, Boolean isHome, Boolean isOnSale,Integer page, Integer size) {

        if (StringUtils.isNotBlank(goodsCode)) {
            goodsCode="%" + goodsCode + "%";
        }
        if (StringUtils.isNotBlank(goodsName)) {
            goodsName="%" + goodsName + "%";
        }
        if (StringUtils.isNotBlank(categoryName)) {
            categoryName="%" + categoryName + "%";
        }
        if(null!=isHome){
            isHome=true;
        }
        PageHelper.startPage(page, size);
        return goodsMapper.queryGoodsList(goodsId, goodsCode, categoryName, goodsName, isHome, isOnSale);
    }

    /**
     * 获取某个商品信息,包含完整信息
     *
     * @param id
     * @return
     */
    public LitemallGoods findById(Integer id) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return litemallGoodsMapper.selectOneByExampleWithBLOBs(example);
    }


    /**
     * 获取某个商品信息,包含完整信息
     *
     * @param id
     * @return
     */
    public LitemallGoods queryBuyById(Integer id) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIdEqualTo(id).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        return litemallGoodsMapper.selectOneByExampleWithBLOBs(example);
    }

    /**
     * 检测该商品是否可以购买
     *
     * @param id
     * @return
     */
    public boolean checkBuyByGoodsId(Integer id) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIdEqualTo(id).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        return litemallGoodsMapper.selectOneByExample(example)==null?false:true;
    }

    /**
     * 获取某个商品信息，仅展示相关内容
     *
     * @param id
     * @return
     */
    public LitemallGoods findByIdVO(Integer id) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIdEqualTo(id).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        return litemallGoodsMapper.selectOneByExampleSelective(example, columns);
    }


    /**
     * 获取所有在售物品总数
     *
     * @return
     */
    public Integer queryOnSale() {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        return (int) litemallGoodsMapper.countByExample(example);
    }

    public int updateById(LitemallGoods goods) {
        goods.setUpdateTime(LocalDateTime.now());
        return litemallGoodsMapper.updateByPrimaryKeySelective(goods);
    }

    /**
     * 批量更新商品为首页展示商品
     * @param goodsIds
     * @return
     */
    public int updateHomeGoodsByIds(List<Integer> goodsIds,Boolean isHome) {
        LitemallGoods goods=new LitemallGoods();
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIdIn(goodsIds).andDeletedEqualTo(false);
        goods.setIsHome(isHome);
        goods.setUpdateTime(LocalDateTime.now());
        return litemallGoodsMapper.updateByExampleSelective(goods,example);
    }

    public void deleteById(Integer id) {
        litemallGoodsMapper.logicalDeleteByPrimaryKey(id);
    }

    public int add(LitemallGoods goods) {
        goods.setAddTime(LocalDateTime.now());
        goods.setUpdateTime(LocalDateTime.now());
        return litemallGoodsMapper.insertSelective(goods);
    }

    /**
     * 获取所有物品总数，包括在售的和下架的，但是不包括已删除的商品
     *
     * @return
     */
    public int count() {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andDeletedEqualTo(false);
        return (int) litemallGoodsMapper.countByExample(example);
    }

    public List<Integer> getCatIds(Integer brandId, String keywords, Boolean isHot, Boolean isNew) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        LitemallGoodsExample.Criteria criteria1 = example.or();
        LitemallGoodsExample.Criteria criteria2 = example.or();

        if (null !=brandId ) {
            criteria1.andBrandIdEqualTo(brandId);
            criteria2.andBrandIdEqualTo(brandId);
        }
        if (null !=isNew) {
            criteria1.andIsNewEqualTo(isNew);
            criteria2.andIsNewEqualTo(isNew);
        }
        if (null !=isHot) {
            criteria1.andIsHotEqualTo(isHot);
            criteria2.andIsHotEqualTo(isHot);
        }
        if (StringUtils.isNotBlank(keywords)) {
            criteria1.andKeywordsLike("%" + keywords + "%");
            criteria2.andNameLike("%" + keywords + "%");
        }
        criteria1.andIsOnSaleEqualTo(true);
        criteria2.andIsOnSaleEqualTo(true);
        criteria1.andDeletedEqualTo(false);
        criteria2.andDeletedEqualTo(false);

        List<LitemallGoods> goodsList = litemallGoodsMapper.selectByExampleSelective(example, Column.categoryId);
        List<Integer> cats = new ArrayList<Integer>();
        for (LitemallGoods goods : goodsList) {
            cats.add(goods.getCategoryId());
        }
        return cats;
    }

    public boolean checkExistByName(String name) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andNameEqualTo(name).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        return litemallGoodsMapper.countByExample(example) != 0;
    }

    public List<LitemallGoods> queryByIds(Integer[] ids) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIdIn(Arrays.asList(ids)).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        return litemallGoodsMapper.selectByExampleSelective(example, columns);
    }

    public List<LitemallGoods> listByGoodsId(List<Integer> goodsIds) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIdIn(goodsIds);
        example.setOrderByClause("add_time desc");
        return litemallGoodsMapper.selectByExample(example);
    }

    public List<LitemallGoodsDto> activityAPPGoods(Integer activityId, Integer goodsId, String goodsName) {
        return goodsMapper.activityAPPGoods(activityId, goodsId, goodsName);
    }
    public List<LitemallGoodsDto> adminActivityGoods(Integer activityId, String goodsCode, String goodsName) {
        return goodsMapper.activityGoods(activityId, goodsCode, goodsName);
    }
    public List<LitemallGoodsDto> activityQueryGoods(Integer activityId,String goodsCode,  String goodsName) {
        if (StringUtils.isNotBlank(goodsName)) {
            goodsName="%" + goodsName + "%";
        }
        if (StringUtils.isNotBlank(goodsCode)) {
            goodsCode="%" + goodsCode + "%";
        }
        return goodsMapper.activityQueryGoods(activityId, goodsCode, goodsName);
    }


    /**
     * 验证商品编码是否有在售商品关联
     * @param goodsCodeId
     * @return
     */
    public boolean countByGoodsCodeId(Integer goodsCodeId) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andGoodsCodeIdEqualTo(goodsCodeId).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        return litemallGoodsMapper.countByExample(example) > 0 ? true : false;
    }

    /**
     * 验证商品编码是否有在售商品关联
     * @param goodsCodeId
     * @return
     */
    public LitemallGoods queryByGoodsCodeId(Integer goodsCodeId) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andGoodsCodeIdEqualTo(goodsCodeId).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        return litemallGoodsMapper.selectOneByExample(example);
    }
}
