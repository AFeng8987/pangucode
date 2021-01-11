package org.linlinjava.litemall.admin;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.linlinjava.litemall.admin.service.AdminGoodsService;
import org.linlinjava.litemall.db.domain.LitemallActivity;
import org.linlinjava.litemall.db.domain.LitemallGoods;
import org.linlinjava.litemall.db.domain.LitemallSpecodeProduct;
import org.linlinjava.litemall.db.service.LitemallActivityService;

import org.linlinjava.litemall.db.service.LitemallAdminService;
import org.linlinjava.litemall.db.service.LitemallGoodsActivityService;
import org.linlinjava.litemall.db.service.LitemallNodeRelationService;
import org.linlinjava.litemall.db.service.LitemallSpecodeProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
public class DbTest {
    @Autowired
    private Environment environment;
    @Autowired
    private AdminGoodsService adminGoodsService;
    @Autowired
    private LitemallActivityService litemallActivity;
    @Autowired
    private LitemallGoodsActivityService goodsActivityService;

    @Autowired
    private LitemallSpecodeProductService specodeProductService;
    @Autowired
    private LitemallAdminService adminService;

    @Autowired
    private LitemallNodeRelationService nodeRelationService;


    @Test
    public void test() {
        String user = environment.getProperty("spring.datasource.druid.username");
        String password = environment.getProperty("spring.datasource.druid.password");
        String url = environment.getProperty("spring.datasource.druid.url");
        int index1 = url.indexOf("3306/");
        int index2 = url.indexOf("?");
        String db = url.substring(index1 + 5, index2);
        System.out.println(user);
        System.out.println(password);
        System.out.println(db);
    }

    @Test
    public void testFileCreate() throws IOException {
        LocalDate localDate = LocalDate.now();
        String fileName = localDate.toString() + ".sql";
        System.out.println(fileName);

        File file = new File("backup", fileName);
        file.getParentFile().mkdirs();
        file.createNewFile();
    }

    @Test
    public void testFileDelete() throws IOException {
        LocalDate localDate = LocalDate.now();
        String fileName = localDate.toString() + ".sql";
        System.out.println(fileName);

        File file = new File("backup", fileName);
        file.deleteOnExit();
    }

    @Test
    public void updateGoods() {
        LitemallGoods litemallGoods = new LitemallGoods();
        litemallGoods.setId(1152008);
        litemallGoods.setIsOnSale(true);
        adminGoodsService.updateGoods(litemallGoods);
    }


    @Test
    public void testactivityList() {
//        List<LitemallActivity> list = litemallActivity.activityList(page, limit);
//        System.out.println(JSONObject.valueToString(list));
//        String activityId = "1";
//        List<String> goodsIds = new ArrayList<>();
//        goodsIds.add("1006002");
//        goodsIds.add("1009009");
//        goodsActivityService.add(activityId, goodsIds);
        System.out.println("start:"+ LocalDateTime.now());
        Integer activityId = 1;
        Integer pageNum = 1;
        Integer pageSize = 10;
//        List<String> list = goodsActivityService.list(activityId, null, null);
//        System.out.println(JSONObject.valueToString(adminGoodsService.listByGoodsId(list, pageNum, pageSize)));
        System.out.println(JSONObject.valueToString(adminGoodsService.activityGoods(activityId, null, null, pageNum, pageSize)));
        System.out.println("end:"+ LocalDateTime.now());
//        System.out.println(JSONObject.valueToString(adminGoodsService.list(null, null, null, page, limit, "add_time", "desc")));

    }

    @Test
    public void adminList() {
        System.out.println(JSONObject.valueToString(adminService.querySelective(null, 1, 10, "add_time", "desc")));
    }

    public static final String DBDRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String DBURL = "jdbc:mysql://localhost:3306/csmall?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&verifyServerCertificate=false&useSSL=false";
    public static final String DBUSER = "root";
    public static final String DBPASS = "zxcvbnm";


    @Test
    public void testJDBC() throws Exception {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String str = "1006002,1009013";
        String sql = "select * from litemall_goods where goods_sn in (" + str + ")";
        Class.forName(DBDRIVER);
        conn = DriverManager.getConnection(DBURL, DBUSER, DBPASS);
        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);
        while (rs.next()) {

            System.out.println(rs.getString(1) + "," + rs.getString(2));
        }
        rs.close();
        stmt.close();
        conn.close();
    }

    @Test
    public  void testBatchUpdate(){
        List<LitemallSpecodeProduct> list=new ArrayList<>();
        for (int i = 1; i <4; i++) {
            LitemallSpecodeProduct specodeProduct=new LitemallSpecodeProduct();
            specodeProduct.setId(i);
            specodeProduct.setGoodsCodeId(2);
            specodeProduct.setSpecCode("babtest"+i);
            specodeProduct.setShopPrice(new BigDecimal(88+i));
            list.add(specodeProduct);
        }
        specodeProductService.updateBatch(list);

    }

    @Test
    public void testNodeRelation(){
        nodeRelationService.updateNodeRelation(3, true);
    }
}
