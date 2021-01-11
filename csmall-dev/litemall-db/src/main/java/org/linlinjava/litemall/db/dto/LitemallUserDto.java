package org.linlinjava.litemall.db.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class LitemallUserDto  implements Serializable {


    private static final long serialVersionUID = -6933023191033330088L;
    private Integer id;
    private String username;
    private String nickname;
    private Byte userLevel;
    private String avatar;
    private Byte status;
    private LocalDateTime addTime;
    private LocalDateTime updateTime;
    private String invitationCode;
    private Integer referralUserId;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Byte getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(Byte userLevel) {
        this.userLevel = userLevel;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public LocalDateTime getAddTime() {
        return addTime;
    }

    public void setAddTime(LocalDateTime addTime) {
        this.addTime = addTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    public Integer getReferralUserId() {
        return referralUserId;
    }

    public void setReferralUserId(Integer referralUserId) {
        this.referralUserId = referralUserId;
    }



}
