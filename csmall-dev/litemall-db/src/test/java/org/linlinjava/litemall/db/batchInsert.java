package org.linlinjava.litemall.db;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.linlinjava.litemall.db.dao.LitemallGoodsSpecMapper;
import org.linlinjava.litemall.db.domain.LitemallGoodsCode;
import org.linlinjava.litemall.db.domain.LitemallGoodsSpec;
import org.linlinjava.litemall.db.service.LitemallGoodsCodeService;
import org.linlinjava.litemall.db.service.LitemallGoodsSpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
public class batchInsert {

    @Autowired
    private LitemallGoodsSpecService goodsSpecService;

    @Autowired
    private LitemallGoodsCodeService goodsCodeService;

    @Test
    public void batchInsert(){
//       List<LitemallGoodsCode> arrayList= goodsCodeService.querySelective("","",1,10);
//
//        System.out.println(arrayList.size());



    }

}
