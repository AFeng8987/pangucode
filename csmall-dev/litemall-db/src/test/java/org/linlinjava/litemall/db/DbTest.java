package org.linlinjava.litemall.db;

import com.alibaba.druid.filter.config.ConfigTools;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.linlinjava.litemall.db.dao.LitemallCartMapper;
import org.linlinjava.litemall.db.dao.LitemallNodeRelationMapper;
import org.linlinjava.litemall.db.domain.LitemallCart;
import org.linlinjava.litemall.db.domain.LitemallCartExample;
import org.linlinjava.litemall.db.domain.LitemallNodeRelation;
import org.linlinjava.litemall.db.domain.LitemallNodeRelationExample;
import org.linlinjava.litemall.db.service.LitemallCartService;
import org.linlinjava.litemall.db.service.LitemallNodeRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
public class DbTest {


    @Autowired
    private LitemallNodeRelationService nodeRelationService;

    @Resource
    private LitemallNodeRelationMapper litemallNodeRelationMapper;
    @Test
    @Transactional
    public void test() {
        List<LitemallNodeRelation> nodeRelations=new ArrayList<>();
        for (int i = 1; i <5 ; i++) {
            LitemallNodeRelation nodeRelation=new LitemallNodeRelation();
            nodeRelation.setAncestor(111+i);
            nodeRelation.setDescendant(222-i);
            nodeRelation.setDistance((byte) i);
            nodeRelation.setNodeFlag(true);
            add(nodeRelation);
            nodeRelations.add(nodeRelation);
        }
        System.out.println("查询出来的数据："+nodeRelations.toString());
        List<LitemallNodeRelation> nodeRelations1=query();
        System.out.println("查询出来的数据："+nodeRelations1.toString());
    }

    public int add(LitemallNodeRelation nodeRelation){
        return litemallNodeRelationMapper.insert(nodeRelation);
    }

    public List<LitemallNodeRelation>  query(){
        LitemallNodeRelationExample example=new LitemallNodeRelationExample();
        example.or().andAncestorGreaterThan(111);
        return litemallNodeRelationMapper.selectByExample(example);
    }



    @Test
    public void test1() {
        ConfigTools configTools=new ConfigTools();

    }





}
