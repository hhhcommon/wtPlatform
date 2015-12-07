package com.woting.mobile.push.mem;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.woting.mobile.model.MobileKey;
import com.woting.mobile.push.model.Message;
import com.woting.mobile.push.model.SendMessageList;

/**
 * 已发送消息的内存结构
 * @author wanghui
 */
public class SendedMemory {
    //java的占位单例模式===begin
    private static class InstanceHolder {
        public static SendedMemory instance = new SendedMemory();
    }
    public static SendedMemory getInstance() {
        return InstanceHolder.instance;
    }
    //java的占位单例模式===end

    protected ConcurrentHashMap<MobileKey, SendMessageList> msgMap;//已发送的信息情况

    /**
     * 初始化已发送消息列表
     */
    private SendedMemory() {
        msgMap=new ConcurrentHashMap<MobileKey, SendMessageList>();
    }

    protected Map<MobileKey, SendMessageList> getMsgMap() {
        return this.msgMap;
    }

    /**
     * 向某一设移动设备的已发送列表插入数据
     * @param mk
     * @param msg
     * @return 插入成功返回true，否则返回false
     * @throws IllegalAccessException 
     */
    public boolean addMsg(MobileKey mk, Message msg) throws IllegalAccessException {
        SendMessageList mobileSendedList=this.msgMap.get(mk);
        if (mobileSendedList==null) {
            mobileSendedList=new SendMessageList(mk);
            this.msgMap.put(mk, mobileSendedList);
        }
        return mobileSendedList.add(msg);
    }

    /**
     * 根据某一移动设备标识，获得已发送消息列表
     * @param mk
     * @return
     */
    public SendMessageList getSendedMessagList(MobileKey mk) {
        return this.msgMap.get(mk);
    }
}