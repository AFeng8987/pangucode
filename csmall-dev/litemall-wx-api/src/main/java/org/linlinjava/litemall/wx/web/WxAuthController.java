package org.linlinjava.litemall.wx.web;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.security.MD5Encoder;
import org.linlinjava.litemall.core.notify.NotifyService;
import org.linlinjava.litemall.core.notify.NotifyType;
import org.linlinjava.litemall.core.util.*;
import org.linlinjava.litemall.core.util.bcrypt.BCryptPasswordEncoder;
import org.linlinjava.litemall.db.domain.LitemallCoupon;
import org.linlinjava.litemall.db.domain.LitemallCouponUser;
import org.linlinjava.litemall.db.domain.LitemallUser;
import org.linlinjava.litemall.db.service.LitemallCouponService;
import org.linlinjava.litemall.db.service.LitemallCouponUserService;
import org.linlinjava.litemall.db.service.LitemallNodeRelationService;
import org.linlinjava.litemall.db.service.LitemallUserService;
import org.linlinjava.litemall.db.util.ShareCodeUtil;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.linlinjava.litemall.wx.dto.UserInfo;
import org.linlinjava.litemall.wx.service.CaptchaCodeManager;
import org.linlinjava.litemall.wx.service.CaptchaIpManager;
import org.linlinjava.litemall.wx.service.ThirdPartyLoginHelper;
import org.linlinjava.litemall.wx.service.UserTokenManager;
import org.linlinjava.litemall.wx.util.WxResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sun.security.provider.MD5;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.linlinjava.litemall.wx.util.WxResponseCode.*;

/**
 * 鉴权服务
 */
@RestController
@RequestMapping("/wx/auth")
@Validated
public class WxAuthController {
    private final Log logger = LogFactory.getLog(WxAuthController.class);

    @Autowired
    private LitemallUserService userService;

    @Autowired
    private WxMaService wxService;

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private ThirdPartyLoginHelper thirdPartyLoginHelper;

    @Autowired
    private LitemallCouponService couponService;

    @Autowired
    private LitemallCouponUserService couponUserService;

    @Autowired
    private LitemallNodeRelationService nodeRelationService;

    /**
     * 账号登录
     *
     * @param body    请求内容，{ username: xxx, password: xxx }
     * @param request 请求对象
     * @return 登录结果
     */
    @PostMapping("login")
    public Object login(@RequestBody String body, HttpServletRequest request) {
        String username = JacksonUtil.parseString(body, "username");
        String password = JacksonUtil.parseString(body, "password");
        String code = JacksonUtil.parseString(body, "code");
        String captchaType = JacksonUtil.parseString(body, "type");
        if (StringUtils.isAllBlank(username,password, code)) {
            return ResponseUtil.badArgument();
        }
        List<LitemallUser> userList = userService.queryByUsername(username);
        LitemallUser user;
        switch(userList.size()){
            case 0:
                return ResponseUtil.fail(AUTH_INVALID_ACCOUNT, "账号不存在");
            case 1:
                user = userList.get(0);
                break;
            default:
                return ResponseUtil.serious();
        }
        if (1 == (int) user.getStatus()) {
            return ResponseUtil.fail(WxResponseCode.USER_NAME_PROHIBITION_OF_USE, "账号被禁用了");
        }
        if (StringUtils.isNotBlank(password)) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (!encoder.matches(password, user.getPassword())) {
                return ResponseUtil.fail(AUTH_INVALID_ACCOUNT, "账号密码不对");
            }
        } else {
            String captchaCode = CaptchaCodeManager.getCachedCaptcha(username, captchaType);
            if (StringUtils.isBlank(captchaCode)) {
                return ResponseUtil.fail(500, "验证码过期，请重新获取。");
            }
            if (!StringUtils.equalsIgnoreCase(captchaCode, code)) {
                return ResponseUtil.fail(500, "验证码错误，请重新输入。");
            }
        }
        // token
        String token = UserTokenManager.generateToken(user.getId());
        // 更新登录情况
        user.setLastLoginTime(LocalDateTime.now());
        user.setToken(token);
        user.setLastLoginIp(IpUtil.getIpAddr(request));

        if (userService.updateById(user) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        // userInfo
        UserInfo userInfo = new UserInfo();
        userInfo.setNickName(username);
        userInfo.setAvatarUrl(user.getAvatar());

        Map<Object, Object> result = new HashMap<Object, Object>();
        result.put("token", token);
        result.put("userInfo", userInfo);
        result.put("userLevel", user.getUserLevel() == 1);
        return ResponseUtil.ok(result);
    }

    @PostMapping("/weixin/login")
    @ApiOperation(value = "微信登录")
    public Object loginByWeiXin(@RequestBody String body, HttpServletRequest request) {
        String code = JacksonUtil.parseString(body, "code");
        UserInfo userInfo = new UserInfo();
        String wxToken = null;
        String openId = null;
        Map<String, String> map = new HashMap<String, String>();
        try {
            logger.info("微信登录--code----:" + code);
            map = thirdPartyLoginHelper.getWxTokenAndOpenid(code);
            logger.info("code转换token及openid----" + map.toString());
            wxToken = map.get("access_token");
            openId = map.get("openId");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 通过code拿到openId和WX_token后直接查用户表，存在就登录，返回token
        LitemallUser user = userService.queryByOid(openId);
        if (user != null) {
            if (1 == (int) user.getStatus()) {
                return ResponseUtil.fail(WxResponseCode.USER_NAME_PROHIBITION_OF_USE, "账号被禁用了");
            }
            // 更新登录情况
            user.setLastLoginTime(LocalDateTime.now());
            user.setLastLoginIp(IpUtil.getIpAddr(request));
            if (userService.updateById(user) == 0) {
                return ResponseUtil.updatedDataFailed();
            }
            // userInfo
            userInfo = new UserInfo();
            userInfo.setNickName(user.getNickname());
            userInfo.setAvatarUrl(user.getAvatar());
            userInfo.setUsername(user.getUsername());
            // 转换成JwtToken
            String jwtToken = UserTokenManager.generateToken(user.getId());
            Map<Object, Object> result = new HashMap<Object, Object>();
            result.put("token", jwtToken);
            result.put("userInfo", userInfo);
            result.put("userLevel", user.getUserLevel() == 1);
            return ResponseUtil.ok(result);
        }
        if (wxToken == null || openId == null) {
            return ResponseUtil.fail();
        }
        // openID不存在进一步取wx端信息。
        try {
            userInfo = thirdPartyLoginHelper.getWxUserinfo(wxToken, openId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (userInfo == null) {
            return ResponseUtil.fail();
        } else {
            logger.info("打印wx用户信息" + userInfo.toString());
            return ResponseUtil.fail(WxResponseCode.USER_NAME_NO_EXIST, "该微信用户还未绑定手机号", userInfo);
        }
    }

    /**
     * @param body
     * @return
     * @Description:第三方登录未绑定手机号码校验验证码,已存在的手机帐号就做绑定，未存在的手机帐号，就新注册，且下发密码
     * @author kevin Tam
     */
    @PostMapping("/wxLogin/checkCaptcha")
    @ApiOperation(value = "微信登录绑手机号", notes = "不需要token")
    public Object thirdPartyCheckCaptcha(@RequestBody String body, HttpServletRequest request) {
        String phoneNumber = JacksonUtil.parseString(body, "mobile");
        String code = JacksonUtil.parseString(body, "code");
        String captchaType = JacksonUtil.parseString(body, "type");
        String invitationCode = JacksonUtil.parseString(body, "invitationCode");
        UserInfo wxUserInfo = JacksonUtil.parseObject(body, "userInfo", UserInfo.class);
        if (StringUtils.isAnyBlank(phoneNumber, code) && null == wxUserInfo) {
            return ResponseUtil.badArgument();
        }
        if (StringUtils.equalsIgnoreCase(code, CaptchaCodeManager.getCachedCaptcha(phoneNumber, captchaType))) {
            if (StringUtils.isAnyBlank(wxUserInfo.getOpenId(), wxUserInfo.getAvatarUrl(), wxUserInfo.getNickName())) {
                return ResponseUtil.badArgument();
            }
            // 如果绑定的手机号已存在帐号，只做绑定操作，且登录
            LitemallUser user = userService.queryByMobileAndStatus(phoneNumber, null);
            if (null != user) {
                if (1 == (int) user.getStatus()) {
                    return ResponseUtil.fail(WxResponseCode.USER_NAME_PROHIBITION_OF_USE, "账号被禁用了");
                } else {
                    // 绑定openId
                    user.setWeixinOpenid(wxUserInfo.getOpenId());
                    // 更新登录情况
                    user.setLastLoginTime(LocalDateTime.now());
                    user.setLastLoginIp(IpUtil.getIpAddr(request));
                    String jwtToken = UserTokenManager.generateToken(user.getId());
                    user.setToken(jwtToken);
                    if (userService.updateById(user) == 0) {
                        return ResponseUtil.updatedDataFailed();
                    }
                    // userInfo
                    UserInfo userInfo = new UserInfo();
                    userInfo.setNickName(user.getNickname());
                    userInfo.setAvatarUrl(user.getAvatar());
                    // 转换成JwtToken
                    Map<Object, Object> result = new HashMap<Object, Object>();
                    result.put("token", jwtToken);
                    result.put("userLevel", user.getUserLevel() == 1);
                    result.put("userInfo", userInfo);
                    return ResponseUtil.ok(result);
                }
            } else {
                // 当绑定的手机号码不存在，注册成为新用户
                int referralUserId = 0;
                //邀请码不为空时，确认邀请码是否有效
                if (StringUtils.isNotBlank(invitationCode)) {
                    LitemallUser referralUser = userService.selByInvitationCode(invitationCode);
                    if (ObjectUtils.isEmpty(referralUser)) {
                        return ResponseUtil.fail(-1, "无效邀请码");
                    } else {
                        referralUserId = referralUser.getId();
                    }
                }
                LitemallUser wxUser = new LitemallUser();
                wxUser.setNickname(wxUserInfo.getNickName());
                //随机密码
                String newPassword = ShareCodeUtil.toSerialCode(Integer.parseInt(code));
                String md5Pw=DigestUtils.md5Hex(newPassword);
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                String encodedNewPassword = encoder.encode(md5Pw);
                wxUser.setUsername(phoneNumber);
                wxUser.setPassword(encodedNewPassword);
                wxUser.setAvatar(wxUserInfo.getAvatarUrl());
                wxUser.setGender(wxUserInfo.getGender());
                wxUser.setWeixinOpenid(wxUserInfo.getOpenId());
                wxUser.setMobile(phoneNumber);
                wxUser.setReferralUserId(referralUserId);

                // 初次登录情况
                wxUser.setLastLoginTime(LocalDateTime.now());
                wxUser.setLastLoginIp(IpUtil.getIpAddr(request));
                userService.addUserAndNodeAndCoupon(wxUser);
                String jwtToken = UserTokenManager.generateToken(wxUser.getId());

                //下发密码到手机号上
                logger.info("下发密码----" + newPassword);
                notifyService.notifySmsTemplate(phoneNumber, NotifyType.ORIGINAL, new String[]{newPassword});
//                aliyunNotifyService.sendSms(phoneNumber, newPassword, CommonConstant.THIRDPARTY_LOGINPWD_TEM);
                // 登录返回的用户信息
                UserInfo resultUserInfo = new UserInfo();
                resultUserInfo.setNickName(wxUser.getNickname());
                resultUserInfo.setAvatarUrl(wxUser.getAvatar());
                // 转换成JwtToken
                Map<Object, Object> result = new HashMap<Object, Object>();
                result.put("token", jwtToken);
                result.put("userInfo", resultUserInfo);
                result.put("userLevel", user.getUserLevel() == 1);
                return ResponseUtil.ok(result);
            }
        } else {
            return ResponseUtil.fail(CUSTOMIZE_FAIL_CODE, "验证码错误，校验失败");
        }
    }

    /**
     * @param body
     * @return
     * @Description:新增用户
     * @author kevin Tam
     */
    @PostMapping("create")
    @ApiOperation(value = "用户注册", notes = "用户注册")
    public Object createUser(@RequestBody String body) {
        String phoneNumber = JacksonUtil.parseString(body, "mobile");
        String CaptchaCode = JacksonUtil.parseString(body, "code");
        String newPassword = JacksonUtil.parseString(body, "password");
        String invitationCode = JacksonUtil.parseString(body, "invitationCode");
        String captchaType = JacksonUtil.parseString(body, "type");
        if (StringUtils.isAnyBlank(phoneNumber, CaptchaCode, newPassword)) {
            return ResponseUtil.badArgumentValue("手机号码+验证码+密码为必传参数");
        }
        if (!RegexUtil.isMobileExact(phoneNumber)) {
            return ResponseUtil.fail(WxResponseCode.AUTH_INVALID_MOBILE, "无效的手机号码");
        }
        LitemallUser litemallUser = userService.selByUserName(phoneNumber);
        if (litemallUser != null) {
            return ResponseUtil.fail(500, "该手机号已被注册");
        }
        int referralUserId = 0;
        //邀请码不为空时，确认邀请码是否有效
        if (StringUtils.isNotBlank(invitationCode)) {
            LitemallUser referralUser = userService.selByInvitationCode(invitationCode);
            if (ObjectUtils.isEmpty(referralUser)) {
                return ResponseUtil.fail(-1, "无效邀请码");
            } else {
                referralUserId = referralUser.getId();
            }
        }

        logger.info("邀请码：" + invitationCode + "----邀请人ID：" + referralUserId);
        if (StringUtils.isBlank(CaptchaCodeManager.getCachedCaptcha(phoneNumber, captchaType))) {
            return ResponseUtil.fail(500, "验证码过期，请重新获取。");
        }
        if (StringUtils.equalsIgnoreCase(CaptchaCode, CaptchaCodeManager.getCachedCaptcha(phoneNumber, captchaType))) {
            logger.info("注册中，验证码通过校验");
            LitemallUser user = new LitemallUser();
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String encodedNewPassword = encoder.encode(newPassword);
            user.setReferralUserId(referralUserId);
            user.setUsername(phoneNumber);
            user.setPassword(encodedNewPassword);
            user.setAvatar("https://yanxuan.nosdn.127.net/80841d741d7fa3073e0ae27bf487339f.jpg?imageView&quality=90&thumbnail=64x64");
            user.setNickname("CS" + System.currentTimeMillis());
            //Status数据库默认为0
            user.setStatus((byte) 0);
            user.setMobile(phoneNumber);
            user.setUserLevel((byte) 0);
            userService.addUserAndNodeAndCoupon(user);
            return ResponseUtil.ok();

        } else {
            logger.info("新用户注册:" + phoneNumber + "的邀请码:" + invitationCode + "-邀请人ID：" + (int) ShareCodeUtil.codeToId(invitationCode) + "-注册中，验证码错误，请重新输入");
            return ResponseUtil.fail(500, "验证码错误，请重新输入。");
        }
    }

    /**
     * 请求验证码
     * <p>
     * TODO
     * 这里需要一定机制防止短信验证码被滥用
     *
     * @param body 手机号码 { mobile: xxx, type: xxx }
     * @return
     */
    @PostMapping("captcha")
    public Object captcha(@RequestBody String body, HttpServletRequest request) {
        String phoneNumber = JacksonUtil.parseString(body, "mobile");
        String captchaType = JacksonUtil.parseString(body, "type");
        String ip = IpUtil.getIpAddr(request);
        if (StringUtils.isAnyBlank(phoneNumber, captchaType)) {
            return ResponseUtil.badArgument();
        }
        if (!RegexUtil.isMobileExact(phoneNumber)){
            return ResponseUtil.badArgumentValue("手机号码格式错误!");
        }
        //IP请求限制
        if (!CaptchaIpManager.limitIp(ip)) {
            return ResponseUtil.frequently();
        }
        LitemallUser user=userService.selByUserName(phoneNumber);
        //获取验证码 typ: login(登陆)  register(注册)  forget(找回) binding(微信登陆绑定手机号码)
        switch(captchaType){
            case "login":
                if (null==user||user.getDeleted()){
                    return ResponseUtil.fail(-1,"账号不存在");
                }
                break;
            case "register":
                if (null!=user){
                    return ResponseUtil.fail(-1,"账号已存在");
                }
                break;
            case "forget":
                if (null==user||user.getDeleted()){
                    return ResponseUtil.fail(-1,"账号不存在");
                }
                break;
            case "binding":
                break;
            default:
                return ResponseUtil.fail();
        }
        String code = CharUtil.getRandomNum(6);
        boolean successful = CaptchaCodeManager.addToCache(phoneNumber, code, captchaType);
        if (!successful) {
            return ResponseUtil.fail(AUTH_CAPTCHA_FREQUENCY, "验证码未超时1分钟，不能发送");
        }
        //腾讯云短线发送
        notifyService.notifySmsTemplate(phoneNumber, NotifyType.CAPTCHA, new String[]{code});
        logger.info("验证码：" + code);
        return ResponseUtil.ok();
    }

    /**
     * 账号密码重置
     *
     * @param body    请求内容
     *                {
     *                password: xxx,
     *                mobile: xxx
     *                code: xxx
     *                }
     *                其中code是手机验证码，目前还不支持手机短信验证码
     * @param request 请求对象
     * @return 登录结果
     * 成功则 { errno: 0, errmsg: '成功' }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @PostMapping("reset")
    public Object reset(@RequestBody String body, HttpServletRequest request) {
        String password = JacksonUtil.parseString(body, "password");
        String mobile = JacksonUtil.parseString(body, "mobile");
        String code = JacksonUtil.parseString(body, "code");
        String captchaType = JacksonUtil.parseString(body, "type");

        if (mobile == null || code == null || password == null) {
            return ResponseUtil.badArgument();
        }

        //判断验证码是否正确
        String cacheCode = CaptchaCodeManager.getCachedCaptcha(mobile, captchaType);
        if (StringUtils.isBlank(cacheCode) || !StringUtils.equalsIgnoreCase(cacheCode, code)) {
            return ResponseUtil.fail(AUTH_CAPTCHA_UNMATCH, "验证码错误");
        }

        LitemallUser user = userService.selByUserName(mobile);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(password);
        user.setPassword(encodedPassword);

        if (userService.updateById(user) == 0) {
            return ResponseUtil.updatedDataFailed();
        }

        return ResponseUtil.ok();
    }

    /**
     * 验证用户名是否被注册
     *
     * @param body
     * @param request
     * @return
     */
    @PostMapping("verify")
    public Object verifyUserName(@RequestBody String body, HttpServletRequest request) {
        String userName = JacksonUtil.parseString(body, "mobile");
        LitemallUser user = userService.selByUserName(userName);

        if (user != null) {
            return ResponseUtil.fail(AUTH_MOBILE_UNREGISTERED, "手机号已注册");
        }

        return ResponseUtil.ok("手机号未注册");
    }

    /**
     * 账号手机号码重置
     *
     * @param body    请求内容
     *                {
     *                password: xxx,
     *                mobile: xxx
     *                code: xxx
     *                }
     *                其中code是手机验证码，目前还不支持手机短信验证码
     * @param request 请求对象
     * @return 登录结果
     * 成功则 { errno: 0, errmsg: '成功' }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @PostMapping("resetPhone")
    public Object resetPhone(@LoginUser Integer userId, @RequestBody String body, HttpServletRequest request) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        String password = JacksonUtil.parseString(body, "password");
        String mobile = JacksonUtil.parseString(body, "mobile");
        String code = JacksonUtil.parseString(body, "code");
        String captchaType = JacksonUtil.parseString(body, "type");

        if (mobile == null || code == null || password == null) {
            return ResponseUtil.badArgument();
        }

        //判断验证码是否正确
        String cacheCode = CaptchaCodeManager.getCachedCaptcha(mobile, captchaType);
        if (cacheCode == null || cacheCode.isEmpty() || !cacheCode.equals(code)) {
            return ResponseUtil.fail(AUTH_CAPTCHA_UNMATCH, "验证码错误");
        }

        List<LitemallUser> userList = userService.queryByMobile(mobile);
        LitemallUser user = null;
        if (userList.size() > 1) {
            return ResponseUtil.fail(AUTH_MOBILE_REGISTERED, "手机号已注册");
        }
        user = userService.findById(userId);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(password, user.getPassword())) {
            return ResponseUtil.fail(AUTH_INVALID_ACCOUNT, "账号密码不对");
        }

        user.setMobile(mobile);
        if (userService.updateById(user) == 0) {
            return ResponseUtil.updatedDataFailed();
        }

        return ResponseUtil.ok();
    }

    /**
     * 账号信息更新
     *
     * @param body    请求内容
     *                {
     *                password: xxx,
     *                mobile: xxx
     *                code: xxx
     *                }
     *                其中code是手机验证码，目前还不支持手机短信验证码
     * @param request 请求对象
     * @return 登录结果
     * 成功则 { errno: 0, errmsg: '成功' }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @PostMapping("profile")
    public Object profile(@LoginUser Integer userId, @RequestBody String body, HttpServletRequest request) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        String avatar = JacksonUtil.parseString(body, "avatar");
        Byte gender = JacksonUtil.parseByte(body, "gender");
        String nickname = JacksonUtil.parseString(body, "nickname");

        LitemallUser user = userService.findById(userId);
        if (StringUtils.isNotBlank(avatar)) {
            user.setAvatar(avatar);
        }
        if (null != gender) {
            user.setGender(gender);
        }
        if (StringUtils.isNotBlank(nickname)) {
            user.setNickname(nickname);
        }

        if (userService.updateById(user) == 0) {
            return ResponseUtil.updatedDataFailed();
        }

        return ResponseUtil.ok();
    }

    /**
     * 微信手机号码绑定
     *
     * @param userId
     * @param body
     * @return
     */
    @PostMapping("bindPhone")
    public Object bindPhone(@LoginUser Integer userId, @RequestBody String body) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        LitemallUser user = userService.findById(userId);
        String encryptedData = JacksonUtil.parseString(body, "encryptedData");
        String iv = JacksonUtil.parseString(body, "iv");
        WxMaPhoneNumberInfo phoneNumberInfo = this.wxService.getUserService().getPhoneNoInfo(user.getSessionKey(), encryptedData, iv);
        String phone = phoneNumberInfo.getPhoneNumber();
        user.setMobile(phone);
        if (userService.updateById(user) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok();
    }

    @PostMapping("logout")
    public Object logout(@LoginUser Integer userId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        return ResponseUtil.ok();
    }

    @GetMapping("info")
    public Object info(@LoginUser Integer userId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        LitemallUser user = userService.findById(userId);
        Map<Object, Object> data = new HashMap<Object, Object>();
        data.put("nickName", user.getNickname());
        data.put("avatar", user.getAvatar());
        data.put("gender", user.getGender());
        data.put("mobile", user.getUsername());
        return ResponseUtil.ok(data);
    }
}
