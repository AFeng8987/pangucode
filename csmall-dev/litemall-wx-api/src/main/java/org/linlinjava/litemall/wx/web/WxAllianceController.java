package org.linlinjava.litemall.wx.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.LitemallAlliance;
import org.linlinjava.litemall.db.domain.LitemallAllianceSale;
import org.linlinjava.litemall.db.domain.LitemallUser;
import org.linlinjava.litemall.db.service.LitemallAllianceGroupService;
import org.linlinjava.litemall.db.service.LitemallAllianceSaleService;
import org.linlinjava.litemall.db.service.LitemallOrderService;
import org.linlinjava.litemall.db.service.LitemallUserService;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.linlinjava.litemall.wx.service.WxOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 加盟商
 */
@RestController
@RequestMapping("/wx/Alliance")
@Validated
public class WxAllianceController {

    private final Log logger = LogFactory.getLog(WxAllianceController.class);

    @Autowired
    private WxOrderService wxOrderService;

    @Autowired
    private LitemallUserService userService;

    @Autowired
    private LitemallAllianceSaleService allianceSaleService;

    /**
     * 加盟商数据
     * @param userId 当前用ID
     * @param type  0近一个月，1 近3个月，2近6个月
     * @return
     */
    @GetMapping("total")
    public Object total(@LoginUser Integer userId,@RequestParam(defaultValue = "0") Integer type) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        LitemallUser user=userService.findById(userId);
        if (user.getUserLevel()==0){
            return ResponseUtil.fail(-1,"当前用户非加盟商");
        }
        if (type!=0&&type!=1&&type!=2){
            return ResponseUtil.fail(-1,"时间类型错误");
        }
        logger.info("用户："+userId+"查询成为加盟商后的总销售额和利润");
        Map data=allianceSaleService.totalAllianceData(userId,type);
        return ResponseUtil.ok(data);
    }
    /**
     * 加盟商数据
     */
    @GetMapping("list")
    public Object list(@LoginUser Integer userId,@RequestParam(defaultValue = "1") Integer type,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        LitemallUser user=userService.findById(userId);
        if (user.getUserLevel()==0){
            return ResponseUtil.fail(-1,"当前用户非加盟商");
        }
        List<Map<String,Object>> list=allianceSaleService.queryByAllianceUserId(userId,type,page,limit);
        return ResponseUtil.okList(list);
    }



    /**
     * 加盟商查看订单详情 脱敏订单详情
     * @param userId  用户ID
     * @param orderGoodsId 订单子表ID
     * @return 订单详情
     */
    @GetMapping("detail")
    public Object detail(@LoginUser Integer userId, @NotNull Integer orderGoodsId) {
        return wxOrderService.insensitiveDetail(userId, orderGoodsId);
    }




}