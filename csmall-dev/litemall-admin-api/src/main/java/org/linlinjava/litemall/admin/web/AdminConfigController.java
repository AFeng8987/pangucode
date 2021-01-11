package org.linlinjava.litemall.admin.web;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.core.system.SystemConfig;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.core.util.RegexUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.LitemallAllianceGroup;
import org.linlinjava.litemall.db.dto.LitemallAllianceGroupDto;
import org.linlinjava.litemall.db.service.LitemallAllianceGroupService;
import org.linlinjava.litemall.db.service.LitemallSystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/config")
@Validated
public class AdminConfigController {
    private final Log logger = LogFactory.getLog(AdminConfigController.class);

    @Autowired
    private LitemallSystemConfigService systemConfigService;


    @RequiresPermissions("admin:config:customer:list")
    @RequiresPermissionsDesc(menu = {"配置管理", "客服配置"}, button = "详情")
    @GetMapping("/customer")
    public Object customerService() {
        Map<String, String> data = systemConfigService.listCustomer();
        return ResponseUtil.ok(data);
    }

    @RequiresPermissions("admin:config:hotNumber")
    @RequiresPermissionsDesc(menu = {"配置管理", "热卖配置"}, button = "详情")
    @GetMapping("/hotNumber")
    public Object hotSale() {
        Map<String, String> data = systemConfigService.hotNumber();
        return ResponseUtil.ok(data);
    }

    @RequiresPermissions("admin:config:hotNumber-update")
    @RequiresPermissionsDesc(menu = {"配置管理", "热卖配置"}, button = "修改")
    @PostMapping("/hotNumber")
    public Object hotSale(@RequestBody String body) {
        logger.info("订单配置:修改数据"+body);
        String hotNumber=JacksonUtil.parseString(body,SystemConfig.LITEMALL_WX_INDEX_HOT);
        Map<String,String> data=new HashMap<>();
        data.put(SystemConfig.LITEMALL_WX_INDEX_HOT,hotNumber);
        systemConfigService.updateConfig(data);
        SystemConfig.updateConfigs(data);
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:config:customer:updateConfigs")
    @RequiresPermissionsDesc(menu = {"配置管理", "客服配置"}, button = "编辑")
    @PostMapping("/customer")
    public Object customerService(@RequestBody String body) {
        logger.info("订单配置:修改数据"+body);
        String customerWx=JacksonUtil.parseString(body,SystemConfig.LITEMALL_CUSTOMER_WX);
        String customerQq=JacksonUtil.parseString(body,SystemConfig.LITEMALL_CUSTOMER_QQ);
        String customerPhone=JacksonUtil.parseString(body,SystemConfig.LITEMALL_CUSTOMER_PHONE);
        String email=JacksonUtil.parseString(body,SystemConfig.LITEMALL_CUSTOMER_EMAIL);
        if (!RegexUtil.isTel(customerPhone)&&!RegexUtil.isMobileExact(customerPhone)){
            return ResponseUtil.badArgumentValue("客服联系电话不符合规则");
        }
        if (!RegexUtil.isMatch(RegexUtil.REGEX_QQWX,customerQq)||!RegexUtil.isMatch(RegexUtil.REGEX_QQWX,customerWx)){
            return  ResponseUtil.badArgumentValue("客服QQ号或微信号不符合规则");
        }
        if (!RegexUtil.isMatch(RegexUtil.REGEX_EMAIL,email)){
            return  ResponseUtil.badArgumentValue("客服邮箱不符合规则");
        }
        Map<String,String> data=new HashMap<>();
        data.put(SystemConfig.LITEMALL_CUSTOMER_WX,customerWx);
        data.put(SystemConfig.LITEMALL_CUSTOMER_QQ,customerQq);
        data.put(SystemConfig.LITEMALL_CUSTOMER_PHONE,customerPhone);
        data.put(SystemConfig.LITEMALL_CUSTOMER_EMAIL,email);
        systemConfigService.updateConfig(data);
        SystemConfig.updateConfigs(data);
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:config:express:list")
    @RequiresPermissionsDesc(menu = {"配置管理", "运费配置"}, button = "详情")
    @GetMapping("/express")
    public Object listExpress() {
        Map<String, String> data = systemConfigService.listExpress();
        return ResponseUtil.ok(data);
    }



    @RequiresPermissions("admin:config:order:list")
    @RequiresPermissionsDesc(menu = {"配置管理", "订单配置"}, button = "详情")
    @GetMapping("/order")
    public Object lisOrder() {
        Map<String, String> data = systemConfigService.listOrder();
        return ResponseUtil.ok(data);
    }

    @RequiresPermissions("admin:config:order:updateConfigs")
    @RequiresPermissionsDesc(menu = {"配置管理", "订单配置"}, button = "编辑")
    @PostMapping("/order")
    public Object updateOrder(@RequestBody String body) {
        logger.info("订单配置:修改数据"+body);
        Integer orderUnpaid=JacksonUtil.parseInteger(body,SystemConfig.LITEMALL_ORDER_UNPAID);
        Integer orderUnConfirm=JacksonUtil.parseInteger(body,SystemConfig.LITEMALL_ORDER_UNCONFIRM);
        if (null==orderUnpaid||null==orderUnConfirm||orderUnpaid>60||orderUnpaid<1||orderUnConfirm>30||orderUnConfirm<1){
            return ResponseUtil.badArgumentValue();
        }
        Map<String,String> data=new HashMap<>();
        data.put(SystemConfig.LITEMALL_ORDER_UNPAID,orderUnpaid.toString());
        data.put(SystemConfig.LITEMALL_ORDER_UNCONFIRM,orderUnConfirm.toString());
        systemConfigService.updateConfig(data);
        return ResponseUtil.ok();
    }





   /*
    @RequiresPermissions("admin:config:mall:list")
    @RequiresPermissionsDesc(menu = {"配置管理", "商场配置"}, button = "详情")
    @GetMapping("/mall")
    public Object listMall() {
        Map<String, String> data = systemConfigService.listMail();
        return ResponseUtil.ok(data);
    }

    @RequiresPermissions("admin:config:mall:updateConfigs")
    @RequiresPermissionsDesc(menu = {"配置管理", "商场配置"}, button = "编辑")
    @PostMapping("/mall")
    public Object updateMall(@RequestBody String body) {
        Map<String, String> data = JacksonUtil.toMap(body);
        systemConfigService.updateConfig(data);
        SystemConfig.updateConfigs(data);
        return ResponseUtil.ok();
    }

     @RequiresPermissions("admin:config:express:updateConfigs")
    @RequiresPermissionsDesc(menu = {"配置管理", "运费配置"}, button = "编辑")
    @PostMapping("/express")
    public Object updateExpress(@RequestBody String body) {
        Map<String, String> data = JacksonUtil.toMap(body);
        systemConfigService.updateConfig(data);
        SystemConfig.updateConfigs(data);
        return ResponseUtil.ok();
    }
    @RequiresPermissions("admin:config:wx:list")
    @RequiresPermissionsDesc(menu = {"配置管理", "小程序配置"}, button = "详情")
    @GetMapping("/wx")
    public Object listWx() {
        Map<String, String> data = systemConfigService.listWx();
        return ResponseUtil.ok(data);
    }

    @RequiresPermissions("admin:config:wx:updateConfigs")
    @RequiresPermissionsDesc(menu = {"配置管理", "小程序配置"}, button = "编辑")
    @PostMapping("/wx")
    public Object updateWx(@RequestBody String body) {
        Map<String, String> data = JacksonUtil.toMap(body);
        systemConfigService.updateConfig(data);
        SystemConfig.updateConfigs(data);
        return ResponseUtil.ok();
    }
    */
}
