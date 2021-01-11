package org.linlinjava.litemall.wx.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.util.HttpUtil;
import org.linlinjava.litemall.wx.dto.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ThirdPartyLoginHelper {
    private final Log logger = LogFactory.getLog(ThirdPartyLoginHelper.class);

    @Autowired
    private Environment environment;


    /**
     * 获取微信用户信息
     */
    public UserInfo getWxUserinfo(String token, String openid) throws Exception {
        UserInfo user = new UserInfo();
        String url = "https://api.weixin.qq.com/sns/userinfo";
        url = url + "?access_token=" + token + "&openid=" + openid;
        String res = HttpUtil.get(url);
        // 编码后的json
        String json = new String(res.getBytes("ISO-8859-1"), "UTF-8");
        JSONObject jsonResult = JSON.parseObject(json);
        if (jsonResult.getString("errcode") == null) {
            user.setNickName(jsonResult.getString("nickname"));
            String img = jsonResult.getString("headimgurl");
            if (StringUtils.isNotBlank(img)) {
                user.setAvatarUrl(img);
            }
            String openId = jsonResult.getString("openid");
            if (StringUtils.isNotBlank(openId)) {
                user.setOpenId(openId);
            }
            String sex = jsonResult.getString("sex");
            if ("0".equals(sex)) {
                user.setGender((byte) 2);
            } else {
                user.setGender((byte) 1);
            }
        } else {
            throw new IllegalArgumentException(jsonResult.getString("errmsg"));
        }
        return user;
    }

    /**
     * `获取微信的认证token和用户OpenID
     *
     * @param code
     * @param
     * @return
     */
    public Map<String, String> getWxTokenAndOpenid(String code) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        // 获取令牌
        String tokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token";
        tokenUrl = tokenUrl + "?appid=" + environment.getProperty("litemall.wx.app-id") + "&secret=" + environment.getProperty("litemall.wx.app-secret") + "&code=" + code + "&grant_type=authorization_code";
        logger.info("-----------------请求微信--APPID：----：" + environment.getProperty("litemall.wx.app-id"));
        logger.info("-----------------请求微信--appSecret：----：" + environment.getProperty("litemall.wx.app-secret"));
        logger.info("-----------------请求地址：----" + tokenUrl);
        String tokenRes = HttpUtil.get(tokenUrl);
        JSONObject json = JSON.parseObject(tokenRes);
        if (json.getString("errcode") == null) {
            map.put("access_token", json.getString("access_token"));
            // 获取微信用户的唯一标识openid
            map.put("openId", json.getString("openid"));
        } else {
            throw new IllegalArgumentException("THIRDPARTY.WXLOGIN.NOTOKEN");
        }
        return map;
    }
}
