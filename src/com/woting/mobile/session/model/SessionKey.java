package com.woting.mobile.session.model;

import java.io.Serializable;

/**
 * 会话key，包括会话Id,和设备ID
 * @author wh
 */
public class SessionKey implements Serializable {
    private static final long serialVersionUID = 8584805045595806786L;

    private String sessionId; //会话ID，uuid
    private String mobileId; //设备Id,IMEI

    public String getSessionId() {
        return sessionId;
    }
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getMobileId() {
        return mobileId;
    }
    public void setMobileId(String mobileId) {
        this.mobileId = mobileId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj==null) return false;
        if (!(obj instanceof SessionKey)) return false;

        if (this==obj
            &&this.sessionId.equals(((SessionKey)obj).getSessionId())
            &&this.mobileId.equals(((SessionKey)obj).getMobileId())) return true;

        return false;
    }
}