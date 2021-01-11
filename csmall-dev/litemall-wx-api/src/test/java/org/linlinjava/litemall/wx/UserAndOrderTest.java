package org.linlinjava.litemall.wx;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.linlinjava.litemall.db.domain.LitemallUser;
import org.linlinjava.litemall.db.service.LitemallUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class UserAndOrderTest {


    @Autowired
    private LitemallUserService userService;



    @Test
    public void testUser(){
        LitemallUser litemallUser = userService.findById(5);
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("nickName", litemallUser.getNickname());
        userInfo.put("avatar", litemallUser.getAvatar());
        userInfo.put("invitationCode", litemallUser.getInvitationCode());
        if (litemallUser.getUserLevel().byteValue() == 1) {
            userInfo.put("alliance", true);
        } else {
            userInfo.put("alliance", false);
        }
        System.out.println(userInfo.toString());
        System.out.println(userInfo.values().toString());
    }
}
