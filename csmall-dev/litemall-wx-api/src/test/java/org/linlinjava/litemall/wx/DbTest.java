package org.linlinjava.litemall.wx;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.linlinjava.litemall.db.domain.LitemallGoodsSpec;
import org.linlinjava.litemall.db.domain.LitemallUser;
import org.linlinjava.litemall.db.service.LitemallGoodsSpecService;
import org.linlinjava.litemall.db.service.LitemallUserService;
import org.linlinjava.litemall.wx.service.WxOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
public class DbTest {
    @Autowired
    private WxOrderService wxOrderService;

    @Autowired
    private LitemallUserService userService;
    @Autowired
    private LitemallGoodsSpecService goodsSpecService;

    private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(2);
    private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();
    private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(16, 16, 1000, TimeUnit.MILLISECONDS, WORK_QUEUE, HANDLER);


    @Test
    public void submitTest() {
        Integer userId = 1;
        String body = "{\"cartId\":0,\"addressId\":1,\"message\":\"测试\"}";
        wxOrderService.submit(userId, body);
    }

    @Test
    public void goodTest() throws Exception {
//        Callable<List> goodsSpecListCallable = () -> goodsSpecService.queryByGid(2);
//        FutureTask<List> goodsSpecTask = new FutureTask<>(goodsSpecListCallable);
//        executorService.submit(goodsSpecTask);
//        List<LitemallGoodsSpec> litemallGoodsSpecs = goodsSpecTask.get();
//        String str = JSONObject.valueToString(goodsSpecTask.get());
//        System.out.println(str);
//
//        List<LitemallGoodsSpec> litemallGoodsSpecs = new ArrayList<>();
//        litemallGoodsSpecs = goodsSpecTask.get().stream().map(item -> com.alibaba.fastjson.JSONObject.parseObject(com.alibaba.fastjson.JSONObject.toJSONString(item), LitemallGoodsSpec.class))
//                .collect(Collectors.toList());
//        System.out.println(JSONObject.valueToString(litemallGoodsSpecs));

        List<LitemallGoodsSpec> litemallGoodsSpecs = goodsSpecService.queryByGid(2);

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

        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            System.out.println(key);
            if (key != null) {
                Map aaa = new HashMap();
                aaa.put(key, map.get(key));
                list.add(aaa);
            }
        }

        System.out.println(JSONObject.valueToString(list));
    }

    @Test
    public  void user(){
        LitemallUser user=userService.findById(1);
        if (user.getReferralUserId()!=null){
            System.out.println("冇问题");
        }
        System.out.println("5245");
    }
}
