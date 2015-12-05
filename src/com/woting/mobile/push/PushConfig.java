package com.woting.mobile.push;

/**
 * 推送相关常量设置
 * @author wanghui
 */
public class PushConfig {
    private int THREADCOUNT_DEALRECEIVEQUEUE=2;//处理原生接收队列线程的个数
    private int PORT_PUSHSERVER=5678;//推送服务端口号

    public int getTHREADCOUNT_DEALRECEIVEQUEUE() {
        return THREADCOUNT_DEALRECEIVEQUEUE;
    }

    public int getPORT_PUSHSERVER() {
        return PORT_PUSHSERVER;
    }
}