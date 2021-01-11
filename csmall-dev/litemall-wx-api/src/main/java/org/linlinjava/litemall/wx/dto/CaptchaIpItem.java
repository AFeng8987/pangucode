package org.linlinjava.litemall.wx.dto;

import java.time.LocalDateTime;

public class CaptchaIpItem {
    private String ip;
    private int times;
    private LocalDateTime expireTime;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }
}
