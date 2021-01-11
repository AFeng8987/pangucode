package org.linlinjava.litemall.wx.service;

import org.linlinjava.litemall.wx.dto.CaptchaIpItem;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

public class CaptchaIpManager {
    private static ConcurrentHashMap<String, CaptchaIpItem> captchaIpCache = new ConcurrentHashMap<>();

    /**
     * 限制IP次数
     *
     * @param ip 地址
     * @return
     */
    public static boolean limitIp(String ip) {

        if (captchaIpCache.get(ip) != null) {
            if (captchaIpCache.get(ip).getExpireTime().isAfter(LocalDateTime.now())) {
                //时间内请求次数不能超过
                if (captchaIpCache.get(ip).getTimes() >= 2) {
                    return false;
                } else {
                    CaptchaIpItem captchaItem = captchaIpCache.get(ip);
                    captchaItem.setTimes(captchaItem.getTimes() + 1);
                    captchaIpCache.put(ip, captchaItem);
                }

            } else {
                //存在但是已过期，删掉
                captchaIpCache.remove(ip);
            }
        }

        CaptchaIpItem captchaItem = new CaptchaIpItem();
        captchaItem.setIp(ip);
        captchaItem.setTimes(1);

        // 有效期为1分钟
        captchaItem.setExpireTime(LocalDateTime.now().plusMinutes(1));

        captchaIpCache.put(ip, captchaItem);

        return true;

    }
}
