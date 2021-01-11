package org.linlinjava.litemall.admin.web;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.admin.service.LogHelper;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.core.util.RegexUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.util.bcrypt.BCryptPasswordEncoder;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallAdmin;
import org.linlinjava.litemall.db.service.LitemallAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;
import java.util.List;

import static org.linlinjava.litemall.admin.util.AdminResponseCode.*;

@RestController
@RequestMapping("/admin/admin")
@Validated
public class AdminAdminController {
    private final Log logger = LogFactory.getLog(AdminAdminController.class);

    @Autowired
    private LitemallAdminService adminService;
    @Autowired
    private LogHelper logHelper;

    @RequiresPermissions("admin:admin:list")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(String username,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        logger.info("管理员管理-----------查询");
        List<LitemallAdmin> adminList = adminService.querySelective(username, page, limit, sort, order);
        return ResponseUtil.okList(adminList);
    }

    private Object validate(LitemallAdmin admin) {
        String name = admin.getUsername();
        if (StringUtils.isBlank(name)||!RegexUtil.isUsername(name)||name.length()>10) {
            return ResponseUtil.fail(ADMIN_INVALID_NAME, "管理员名称不符合规定");
        }
        return null;
    }

    @RequiresPermissions("admin:admin:create")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallAdmin admin) {
        logger.info("管理员管理-----------添加");
        Object error = validate(admin);
        if (error != null) {
            return error;
        }
        String password = admin.getPassword();
        if (StringUtils.isEmpty(password) || password.length()!=32) {
            return ResponseUtil.fail(ADMIN_INVALID_PASSWORD, "管理员密码错误");
        }
        String username = admin.getUsername();
        List<LitemallAdmin> adminList = adminService.findAdmin(username);
        if (adminList.size() > 0) {
            return ResponseUtil.fail(ADMIN_NAME_EXIST, "管理员已经存在");
        }
        if (admin.getStatus() == 1) {
            if (null==admin.getPlantId()) {
                return ResponseUtil.fail(500, "工厂信息不存在");
            }
        }
        String rawPassword = admin.getPassword();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(rawPassword);
        admin.setPassword(encodedPassword);
        adminService.add(admin);
        logHelper.logAuthSucceed("添加管理员", username);
        return ResponseUtil.ok(admin);
    }

    @RequiresPermissions("admin:admin:read")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        logger.info("管理员管理-----------详情");
        LitemallAdmin admin = adminService.findById(id);
        return ResponseUtil.ok(admin);
    }

    @RequiresPermissions("admin:admin:update")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallAdmin admin) {
        logger.info("管理员管理-----------编辑");
        Object error = validate(admin);
        if (error != null) {
            return error;
        }
        Integer anotherAdminId = admin.getId();
        if (anotherAdminId == null) {
            return ResponseUtil.badArgument();
        }
        // 不允许管理员通过编辑接口修改密码
        admin.setPassword(null);
        if (adminService.updateById(admin) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        logHelper.logAuthSucceed("编辑管理员信息", admin.getUsername());
        return ResponseUtil.ok(admin);
    }

    @RequiresPermissions("admin:admin:delete")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallAdmin admin) {
        logger.info("管理员管理-----------删除");
        Integer anotherAdminId = admin.getId();
        if (anotherAdminId == null) {
            return ResponseUtil.badArgument();
        }

        // 管理员不能删除自身账号
        Subject currentUser = SecurityUtils.getSubject();
        LitemallAdmin currentAdmin = (LitemallAdmin) currentUser.getPrincipal();
        if (currentAdmin.getId().equals(anotherAdminId)) {
            return ResponseUtil.fail(ADMIN_DELETE_NOT_ALLOWED, "管理员不能删除自己账号");
        }

        adminService.deleteById(anotherAdminId);
        logHelper.logAuthSucceed("删除管理员", admin.getUsername());
        return ResponseUtil.ok();
    }


    @RequiresPermissions("admin:admin:updatepswd")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "修改指定账号密码")
    @PostMapping("/updatepswd")
    public Object updatepswd(@RequestBody String body) {
        LitemallAdmin currentAdmin;
        Subject currentUser = SecurityUtils.getSubject();
        if (currentUser != null) {
            currentAdmin = (LitemallAdmin) currentUser.getPrincipal();
            if (null == currentAdmin) {
                return ResponseUtil.fail(-1, "当前账号不能为null");
            }
        } else {
            return ResponseUtil.fail(-1, "当前账号不能为null");
        }
        Integer adminId = JacksonUtil.parseInteger(body, "adminId");
        String username = JacksonUtil.parseString(body, "username");
        String password = JacksonUtil.parseString(body, "password");
        logger.info("管理员管理------"+currentAdmin.getUsername()+"-----修改指定账号" + username + "--的密码");
        if ( null==adminId) {
            return ResponseUtil.badArgument();
        }
        if (StringUtils.isEmpty(password) || password.length()!=32) {
            return ResponseUtil.fail(ADMIN_INVALID_PASSWORD, "管理员密码格式错误");
        }
        LitemallAdmin admin=adminService.findAdmin(adminId);
        if (null==admin) {
            return ResponseUtil.badArgument();
        }
        if (!StringUtils.equals(admin.getUsername(),username)){
            return ResponseUtil.badArgument();
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(password);
        // 不允许管理员通过编辑接口修改密码
        admin.setPassword(encodedPassword);
        if (adminService.updateById(admin) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        logHelper.logAuthSucceed("管理员管理---修改指定账号密码", currentAdmin.getUsername()+"-----修改指定账号" + username + "--的密码");
        return ResponseUtil.ok();
    }
}
