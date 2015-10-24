package com.woting.mobile.session.mem;

import org.apache.log4j.Logger;

import com.spiritdata.framework.util.StringUtils;
import com.woting.mobile.session.model.MobileSession;
import com.woting.mobile.session.model.SessionKey;

/**
 * 会话管理类，也就是会话的服务类
 * @author wh
 */
public class SessionMemoryManage {
    protected Logger log = Logger.getLogger(this.getClass());

    //java的占位单例模式===begin
    private static class InstanceHolder {
        public static SessionMemoryManage instance = new SessionMemoryManage();
    }
    public static SessionMemoryManage getInstance() {
        SessionMemoryManage smm = InstanceHolder.instance;
        smm.setSessionMemory();
        return smm;
    }
    //java的占位单例模式===end

    /**
     * 移动会话内存数据
     */
    protected SessionMemory sm = null;
    protected void setSessionMemory() {
        this.sm = SessionMemory.getInstance();
    }

    /**
     * 清除过期的会话
     */
    public void clean() {
        //System.out.println("【"+(new java.util.Date())+"】：清除过期会话");
        //清除会话
        if (this.sm.mSessionMap!=null&&this.sm.mSessionMap.size()>0) {
            for (SessionKey sKey: this.sm.mSessionMap.keySet()) {
                MobileSession ms = this.sm.mSessionMap.get(sKey);
                if (ms.expired()) this.sm.mSessionMap.remove(sKey);
            }
        }
    }

    /**
     * 把所有IMEI对应的Session设置为过期
     * @param imei
     */
    public void expireAllSessionByIMEI(String imei) {
        if (this.sm.mSessionMap!=null&&this.sm.mSessionMap.size()>0
            &&!StringUtils.isNullOrEmptyOrSpace(imei)) {
            for (SessionKey sKey: this.sm.mSessionMap.keySet()) {
                if (sKey.getMobileId().equals(imei)) {
                    MobileSession ms = this.sm.mSessionMap.get(sKey);
                    ms.expire();
                }
            }
        }
    }

    /**
     * 加入一个Session
     * @param ms 移动会话
     */
    public void addOneSession(MobileSession ms) {
        this.sm.add(ms);
    }

    /**
     * 得到SessionKey对应的Session
     * @param sk Session的key
     * @return 对应的Session
     */
    public MobileSession getSession(SessionKey sk) {
        return this.sm.mSessionMap.get(sk);
    }
}