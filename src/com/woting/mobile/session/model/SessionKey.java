package com.woting.mobile.session.model;

import java.io.Serializable;

import com.spiritdata.framework.util.StringUtils;

/**
 * 会话key，包括设备ID和用户Id
 * @author wh
 */
public class SessionKey implements Serializable {
    private static final long serialVersionUID = 8584805045595806786L;

    private String mobileId; //设备Id，IMEI
    private String userId; //用户Id，若未登录，则用户Id为空

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
        return (this.userId!=null);
    }
    /**
     * 是否是设备Session，是用户未登录时的Sessin
     */
    public boolean isMobileSession() {
        return (this.userId==null);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj==null||!(obj instanceof SessionKey)) return false;
        if (this.mobileId.equals(((SessionKey)obj).getMobileId())){
            if (this.userId==null&&((SessionKey)obj).getUserId()==null) return true;
            else if (this.userId!=null&&this.userId.equals(((SessionKey)obj).getUserId())) return true;
            else return false;
        }
        return false;
    }

    /**
     * 获得SessionId，若userId==null,则SessionId=IMEI,否则SessionId=userId
     * @return
     */
    public String getSessionId() {
        return (StringUtils.isNullOrEmptyOrSpace(this.userId)?this.mobileId:this.userId);
    }
}