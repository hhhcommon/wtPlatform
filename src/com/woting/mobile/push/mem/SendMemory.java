package com.woting.mobile.push.mem;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.woting.mobile.push.model.Message;

public class SendMemory {
    //java的占位单例模式===begin
    private static class InstanceHolder {
        public static SendMemory instance = new SendMemory();
    }
    public static SendMemory getInstance() {
        return InstanceHolder.instance;
    }
    //java的占位单例模式===end

    private ConcurrentHashMap<String, ConcurrentLinkedQueue<Message>> typeMsgMap;//
    private SendMemory() {
    }
}