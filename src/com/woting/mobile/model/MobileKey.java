package com.woting.mobile.model;

import java.io.Serializable;

import com.woting.mobile.MobileUtils;

/**
 * 会话key，包括设备ID和用户Id
 * @author wh
 */
public class MobileKey implements Serializable {
    private static final long serialVersionUID = 8584805045595806786L;

    private String mobileId; //设备Id，IMEI
    private String userId; //用户Id，若未登录，则用户Id为IMEI

    public String getMobileId() {
        return mobileId;
    }
    public void setMobileId(String mobileId) {
        this.mobileId = mobileId;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 是否是用户Session，是用户登录成功后的Sessin
     */
    public boolean isUserSession() {
        return MobileUtils.isValidUserId(this.userId);
    }
    /**
     * 是否是设备Session，是用户未登录时的Sessin
     */
    public boolean isMobileSession() {
        return !MobileUtils.isValidUserId(this.userId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj==null||!(obj instanceof MobileKey)) return false;
        if (this.mobileId.equals(((MobileKey)obj).getMobileId())){
            if (this.userId==null&&((MobileKey)obj).getUserId()==null) return true;
            else if (this.userId!=null&&this.userId.equals(((MobileKey)obj).getUserId())) return true;
            else return false;
        }
        return false;
    }

    /**
     * 获得SessionId，SessionId就是UserId
     * @return
     */
    public String getSessionId() {
        return this.userId;
    }
}