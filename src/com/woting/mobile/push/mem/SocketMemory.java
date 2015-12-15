package com.woting.mobile.push.mem;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.woting.mobile.model.MobileKey;
import com.woting.mobile.push.SocketHandle;
import com.woting.mobile.push.model.Message;

public class SocketMemory {
    //java的占位单例模式===begin
    private static class InstanceHolder {
        public static SocketMemory instance = new SocketMemory();
    }
    public static SocketMemory getInstance() {
        return InstanceHolder.instance;
    }
    //java的占位单例模式===end

    protected Map<String, SocketHandle> msgMap;//将要发送的消息列表

    /*
     * 初始化发送消息列表
     */
    private SocketMemory() {
        msgMap=new ConcurrentHashMap<String, SocketHandle>();
    }
}