package org.linlinjava.litemall.admin.web;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.admin.dto.GoodsAllinone;
import org.linlinjava.litemall.core.util.RegexUtil;
import org.linlinjava.litemall.db.domain.LitemallGoods;
import org.linlinjava.litemall.db.dto.LitemallPlantDto;
import org.linlinjava.litemall.admin.service.AdminPlantService;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.LitemallPlant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 工厂板块
 */
@RestController
@RequestMapping("/admin/plant")
@Validated
public class AdminPlantController {

    private final Log logger = LogFactory.getLog(AdminPlantController.class);

    @Autowired
    private AdminPlantService adminPlantService;

    @RequiresPermissions("admin:plant:add")
    @RequiresPermissionsDesc(menu = {"用户管理", "工厂信息"}, button = "添加")
    @PostMapping("/add")
    public Object add(@RequestBody LitemallPlant plant) {
        Object error = validate(plant);
        if (error != null) {
            return error;
        }
        return  adminPlantService.add(plant);
    }

    @RequiresPermissions("admin:plant:update")
    @RequiresPermissionsDesc(menu = {"用户管理", "工厂信息"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallPlant plant) {
        Object error = validate(plant);
        if (error != null) {
            return error;
        }
        plant.setAddTime(null);
        adminPlantService.update(plant);
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:plant:detail")
    @RequiresPermissionsDesc(menu = {"用户管理", "工厂信息"}, button = "详情")
    @GetMapping("/detail")
    public Object detail(Integer id) {
        return ResponseUtil.ok(adminPlantService.detail(id));
    }

    @RequiresPermissions("admin:plant:del")
    @RequiresPermissionsDesc(menu = {"用户管理", "工厂信息"}, button = "删除")
    @PostMapping("/del/{id}")
    public Object del(@PathVariable String id) {
        logger.info("删除工厂:"+id);
        return adminPlantService.del(Integer.valueOf(id));
    }

    @RequiresPermissions("admin:plant:sel")
    @RequiresPermissionsDesc(menu = {"用户管理", "工厂信息"}, button = "查询列表")
    @GetMapping("/sel")
    public Object sel(String name, String phone, String contacts, String addTime,
                      @RequestParam(defaultValue = "1") Integer page,
                      @RequestParam(defaultValue = "10") Integer limit) {
        List<LitemallPlant> list = adminPlantService.sel(name, phone, contacts, addTime, page, limit);
        return ResponseUtil.okList(list);
    }


    private Object validate(LitemallPlant plant) {
        if (StringUtils.isAnyBlank(plant.getPlantName(), plant.getPlantAddress(),plant.getPlantPhone(), plant.getPlantContacts(),
                plant.getReceiveAddress(),plant.getReceiveContacts(),plant.getReceivePhone(),
                plant.getSendPhone(),plant.getSendAddress(),plant.getSendContacts())) {
            return ResponseUtil.badArgumentValue("必传参数不可为空");
        }
        if (plant.getPlantContacts().length()>30||plant.getReceiveContacts().length()>30||plant.getSendContacts().length()>30
        ){
            return ResponseUtil.badArgumentValue("联系人的长度不能超过30个字符");
        }
        if (plant.getPlantAddress().length()>80||plant.getReceiveAddress().length()>80||plant.getSendAddress().length()>80
        ){
            return ResponseUtil.badArgumentValue("联系地址不能超过80个字符");
        }
        if (plant.getSendPhone().length()>30||plant.getPlantPhone().length()>30||plant.getReceivePhone().length()>30
        ){
            return ResponseUtil.badArgumentValue("联系方式不能超过30个字符");
        }

        if (plant.getPlantName().length()>50){
            return ResponseUtil.badArgumentValue("工厂名称不能超过50字符");
        }
        return null;
    }

}
