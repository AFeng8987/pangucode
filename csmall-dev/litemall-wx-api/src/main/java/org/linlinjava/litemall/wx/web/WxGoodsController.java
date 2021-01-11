package org.linlinjava.litemall.wx.web;

import com.github.pagehelper.PageInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.system.SystemConfig;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.dto.LitemallGoodsDto;
import org.linlinjava.litemall.db.service.*;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.*;

/**
 * 商品服务
 */
@RestController
@RequestMapping("/wx/goods")
@Validated
public class WxGoodsController {
    private final Log logger = LogFactory.getLog(WxGoodsController.class);

    @Autowired
    private LitemallGoodsService goodsService;

    @Autowired
    private LitemallIssueService goodsIssueService;

    @Autowired
    private LitemallGoodsAttributeService goodsAttributeService;

    @Autowired
    private LitemallUserService userService;

    @Autowired
    private LitemallFootprintService footprintService;

    @Autowired
    private LitemallCategoryService categoryService;

    @Autowired
    private LitemallSearchHistoryService searchHistoryService;

    @Autowired
    private LitemallGoodsSpecService goodsSpecService;

    @Autowired
    private LitemallGoodsActivityService goodsActivityService;
    @Autowired
    private LitemallGoodsProductService goodsProductService;
    @Autowired
    private LitemallSpecodeProductService specodeProductService;

    private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(9);

    private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(16, 16, 1000, TimeUnit.MILLISECONDS, WORK_QUEUE, HANDLER);

    /**
     * 商品详情
     * <p>
     * 用户可以不登录。
     * 如果用户登录，则记录用户足迹以及返回用户收藏信息。
     *
     * @param userId 用户ID
     * @param id     商品ID
     * @return 商品详情
     */
    @GetMapping("detail")
    public Object detail(@LoginUser Integer userId, @NotNull Integer id) {
        // 商品信息
        LitemallGoods info = goodsService.findById(id);

        // 商品属性
        Callable<List> goodsAttributeListCallable = () -> goodsAttributeService.queryByGid(id);

        // 商品规格对应的数量和价格
        LitemallGoods litemallGoods = goodsService.findById(id);
        Callable<List> specodeProductListCallable = () -> specodeProductService.queryByGid(litemallGoods.getGoodsCodeId());
        Callable<List> goodsSpecListCallable = () -> goodsSpecService.queryByGid(litemallGoods.getGoodsCodeId());

        Callable<List> productListCallable = () -> goodsProductService.queryByGid(id);

        // 商品问题，这里是一些通用问题
        Callable<List> issueCallable = () -> goodsIssueService.querySelective("", 1, 4, "", "");


        // 记录用户的足迹 异步处理
        if (userId != null) {
            executorService.execute(() -> {
                LitemallFootprint footprint = new LitemallFootprint();
                footprint.setUserId(userId);
                footprint.setGoodsId(id);
                footprintService.add(footprint);
            });
        }
        FutureTask<List> goodsAttributeListTask = new FutureTask<>(goodsAttributeListCallable);
        FutureTask<List> productListCallableTask = new FutureTask<>(productListCallable);
        FutureTask<List> issueCallableTask = new FutureTask<>(issueCallable);
        FutureTask<List> goodsSpecTask = new FutureTask<>(goodsSpecListCallable);
        FutureTask<List> specodeProductListTask = new FutureTask<>(specodeProductListCallable);

        executorService.submit(goodsAttributeListTask);
        executorService.submit(productListCallableTask);
        executorService.submit(issueCallableTask);
        executorService.submit(goodsSpecTask);
        executorService.submit(specodeProductListTask);

        Map<Object, Object> data = new HashMap<>();

        try {
            List<LitemallGoodsSpec> litemallGoodsSpecs = goodsSpecService.queryByGid(litemallGoods.getGoodsCodeId());
            Map<String, List> map = new HashMap<>();
            List detailList;
            Map detailMap;
            List list = new ArrayList();
            //遍历商品规格表
            for (LitemallGoodsSpec goodsSpec : litemallGoodsSpecs) {
                //将相同的商品规格名称放一起
                if (map.get(goodsSpec.getSpec()) != null) {
                    detailList = map.get(goodsSpec.getSpec());
                    detailMap = new HashMap();
                    detailMap.put("id", goodsSpec.getId());
                    detailMap.put("value", goodsSpec.getValue());
                    detailList.add(detailMap);
                    map.put(goodsSpec.getSpec(), detailList);
                } else {
                    detailList = new ArrayList();
                    detailMap = new HashMap();
                    detailMap.put("id", goodsSpec.getId());
                    detailMap.put("value", goodsSpec.getValue());
                    detailList.add(detailMap);
                    map.put(goodsSpec.getSpec(), detailList);
                }

            }
            //封装数据给APP端
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();

                if (key != null) {
                    Map map1 = new HashMap();
                    map1.put(key, map.get(key));
                    list.add(map1);
                }
            }


            data.put("info", info);
//			data.put("userHasCollect", userHasCollect);
            data.put("issue", issueCallableTask.get());
//			data.put("comment", commentsCallableTsk.get());
//            data.put("specificationList", objectCallableTask.get());
//			data.put("productList", productListCallableTask.get());
//            data.put("productFactoryList", productFactoryListTask.get());
            data.put("attribute", goodsAttributeListTask.get());
//			data.put("brand", brandCallableTask.get());
//			data.put("groupon", grouponRulesCallableTask.get());
            //SystemConfig.isAutoCreateShareImage()
//			data.put("share", SystemConfig.isAutoCreateShareImage());
            data.put("specodeProductList", specodeProductListTask.get());
            data.put("goodsSpecList", list);
            data.put("freight", SystemConfig.getFreight());
            data.put("freightLimit", SystemConfig.getFreightLimit());

        } catch (Exception e) {
            e.printStackTrace();
        }

        //商品分享图片地址
        data.put("shareImage", info.getShareUrl());
        return ResponseUtil.ok(data);
    }

    /**
     * 商品分类类目
     *
     * @param id 分类类目ID
     * @return 商品分类类目
     */
    @GetMapping("category")
    public Object category(@NotNull Integer id) {
        LitemallCategory cur = categoryService.findById(id);
        LitemallCategory parent = null;
        List<LitemallCategory> children = null;

        if (cur.getPid() == 0) {
            parent = cur;
            children = categoryService.queryByPid(cur.getId());
            cur = children.size() > 0 ? children.get(0) : cur;
        } else {
            parent = categoryService.findById(cur.getPid());
            children = categoryService.queryByPid(cur.getPid());
        }
        Map<String, Object> data = new HashMap<>();
        data.put("currentCategory", cur);
        data.put("parentCategory", parent);
        data.put("brotherCategory", children);
        return ResponseUtil.ok(data);
    }

    /**
     * 根据条件搜素商品
     * <p>
     * 1. 这里的前五个参数都是可选的，甚至都是空
     * 2. 用户是可选登录，如果登录，则记录用户的搜索关键字
     *
     * @param categoryId 分类类目ID，可选
     * @param brandId    品牌商ID，可选
     * @param keyword    关键字，可选
     * @param isNew      是否新品，可选
     * @param isHot      是否热买，可选
     * @param userId     用户ID
     * @param page       分页页数
     * @param limit      分页大小
     * @param sort       排序方式，支持"add_time", "retail_price"或"name"
     * @param order      排序类型，顺序或者降序
     * @return 根据条件搜素的商品详情
     */
    @GetMapping("list")
    public Object list(Integer categoryId, Integer brandId, String keyword, Boolean isNew, Boolean isHot,
                       @LoginUser Integer userId,@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort(accepts = {"sales", "retail_price", "synthesize"}) @RequestParam(defaultValue = "synthesize") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {

        //添加到搜索历史
        if (userId != null && !StringUtils.isEmpty(keyword)) {
            LitemallSearchHistory searchHistoryVo = new LitemallSearchHistory();
            searchHistoryVo.setKeyword(keyword);
            searchHistoryVo.setUserId(userId);
            searchHistoryVo.setFrom("app");
            searchHistoryService.save(searchHistoryVo);
        }
        //查询列表数据
        List<LitemallGoods> goodsList = goodsService.querySelective(categoryId, brandId, keyword, isHot, isNew, page, limit, sort, order);
        //查询类目下关键字
        LitemallCategory category = new LitemallCategory();
        if (!StringUtils.isEmpty(categoryId)) {
            category = categoryService.findById(categoryId);
        }
        PageInfo<LitemallGoods> pagedList = PageInfo.of(goodsList);
        Map<String, Object> entity = new HashMap<>();
        entity.put("list", goodsList);
        entity.put("total", pagedList.getTotal());
        entity.put("page", pagedList.getPageNum());
        entity.put("limit", pagedList.getPageSize());
        entity.put("pages", pagedList.getPages());
        entity.put("keyword", category.getKeywords());


        // 因为这里需要返回额外的filterCategoryList参数，因此不能方便使用ResponseUtil.okList
        return ResponseUtil.ok(entity);
    }

    /**
     * 商品详情页面“大家都在看”推荐商品
     *
     * @param id, 商品ID
     * @return 商品详情页面推荐商品
     */
    @GetMapping("related")
    public Object related(@NotNull Integer id) {
        LitemallGoods goods = goodsService.findById(id);
        if (goods == null) {
            return ResponseUtil.badArgumentValue();
        }

        // 目前的商品推荐算法仅仅是推荐同类目的其他商品
        int cid = goods.getCategoryId();

        // 查找六个相关商品
        int related = 6;
        List<LitemallGoods> goodsList = goodsService.queryByCategory(cid, 0, related);
        return ResponseUtil.okList(goodsList);
    }

    /**
     * 在售的商品总数
     *
     * @return 在售的商品总数
     */
    @GetMapping("count")
    public Object count() {
        Integer goodsCount = goodsService.queryOnSale();
        return ResponseUtil.ok(goodsCount);
    }

    /**
     * 查询活动商品
     *
     * @return
     */
    @GetMapping("/activityGoods")
    public Object activityGoods(Integer activityId, Integer goodsId, String goodsName) {
        if (StringUtils.isEmpty(activityId)) {
            return ResponseUtil.fail(500, "数据为空，请输入正确条件");
        }
        List<LitemallGoodsDto> dtoList = goodsActivityService.activityGoods(activityId, goodsId, goodsName);
        return ResponseUtil.ok(dtoList);
    }

    /**
     * 分享商品
     *
     * @param goodsId
     * @return
     */
    @GetMapping("/shareGoods")
    public Object shareGoods(@LoginUser Integer userId, @NotNull Integer goodsId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        Map<String, Object> entity = new HashMap<>();
        LitemallGoods goods = goodsService.findById(goodsId);
        LitemallUser user = userService.findById(userId);
        entity.put("goodsName", goods.getName());
        entity.put("goodsPic", goods.getPicUrl());
        entity.put("counterPrice", goods.getCounterPrice());
        entity.put("retailPrice", goods.getRetailPrice());
        entity.put("userName", user.getNickname());
        entity.put("invitationCode", user.getInvitationCode());
        return ResponseUtil.ok(entity);
    }

}
