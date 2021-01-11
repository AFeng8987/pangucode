package org.linlinjava.litemall.admin.web;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.admin.service.LogHelper;
import org.linlinjava.litemall.core.util.RegexUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallAlliance;
import org.linlinjava.litemall.db.domain.LitemallUser;
import org.linlinjava.litemall.db.dto.LitemallUserDto;
import org.linlinjava.litemall.db.service.LitemallUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/admin/user")
@Validated
public class AdminUserController {
    private final Log logger = LogFactory.getLog(AdminUserController.class);

    @Autowired
    private LitemallUserService userService;

    @Autowired
    private LogHelper logHelper;

    @RequiresPermissions("admin:user:list")
    @RequiresPermissionsDesc(menu = {"用户管理", "会员信息"}, button = "查询")
    @GetMapping("/list")
    public Object list(String username, String nickName, String userLevel,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<LitemallUser> data = userService.querySelective(username, nickName, page, limit, sort, order, userLevel);
        logger.info("会员信息查询");
        return ResponseUtil.okList(data);
    }

    @RequiresPermissions("admin:user:allianceAdd")
    @RequiresPermissionsDesc(menu = {"用户管理", "会员信息"}, button = "添加加盟商")
    @PostMapping("/allianceAdd")
    public Object allianceAdd(@RequestBody LitemallAlliance alliance) {
        logger.info("添加加盟商,添加内容："+alliance.toString());
        //添加参数校验
        if (StringUtils.isEmpty(alliance.getUserId())) {
            return ResponseUtil.fail(500, "参数不对");
        }
        if (!RegexUtil.isIDCard18(alliance.getAllianceCardId())) {
            return ResponseUtil.badArgumentValue( "身份证号码格式不正确");
        }
        LitemallUser user= userService.findById(alliance.getUserId());
        if (null==user){
            return ResponseUtil.fail(500, "参数不对");
        }
        userService.allianceAdd(alliance);
        logHelper.logOtherSucceed("添加加盟商","添加用户ID："+alliance.getUserId());
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:user:allianceDel")
    @RequiresPermissionsDesc(menu = {"用户管理", "会员信息"}, button = "删除加盟商")
    @PostMapping("/allianceDel/{id}")
    public Object allianceDel(@PathVariable Integer id) {
            //  删除加盟商，应该做的严禁一点，另外删除了，下面和他的关系也要变
        //目前只改了加盟商表和user表
        logger.info("删除加盟商，用户ID"+id);
        userService.allianceDel(id);
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:user:subordinateView")
    @RequiresPermissionsDesc(menu = { "用户管理", "会员信息" }, button = "加盟商团队查看")
    @ApiOperation(value = "加盟商我的团队", notes = "加盟商团队查看")
    @GetMapping("/subordinate")
    public Object subordinateUserView(@RequestParam int userId,
                                      @RequestParam(defaultValue = "1") Integer type,
                                      @RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer limit) {
        LitemallUser user = userService.findById(userId);
        if (null == user ) {
            return ResponseUtil.badArgument();
        }

        List<Map<String, Object>> subordinateUser;
        switch(type){
            case 1:
                //查当前用户下直属会员
                subordinateUser= userService.subordinateUser(userId,page,limit);
                break;
            case 2:
                //查当前用户下非直属会员
                subordinateUser = userService.nonSubordinateUser(userId,page,limit);
                break;
            default:
                subordinateUser=new ArrayList<>();
                break;
        }
        return ResponseUtil.okList(subordinateUser);
    }


}
