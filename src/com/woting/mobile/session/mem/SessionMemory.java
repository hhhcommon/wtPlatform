package com.woting.mobile.session.mem;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.woting.mobile.session.MobileSessionConfig;
import com.woting.mobile.session.model.MobileSession;
import com.woting.mobile.session.model.SessionKey;

public class SessionMemory {
    //java的占位单例模式===begin
    private static class InstanceHolder {
        public static SessionMemory instance = new SessionMemory();
    }
    public static SessionMemory getInstance() {
        return InstanceHolder.instance;
    }
    //java的占位单例模式===end

    //移动会话Map
    protected Map<SessionKey, MobileSession> mSessionMap = null;
    //移动会话的配置信息
    protected MobileSessionConfig myConfig = null;

    /**
     * 初始化会话内存
     * @param msc 移动会话配置
     */
    public void init(MobileSessionConfig msc) {
        myConfig=(msc==null?new MobileSessionConfig():msc);
        mSessionMap=new ConcurrentHashMap<SessionKey, MobileSession>();
    }

    /**
     * 增加一个Session结构
     * @param ms
     */
    public void add(MobileSession ms) {
        mSessionMap.put(ms.getKey(), ms);
    }
}