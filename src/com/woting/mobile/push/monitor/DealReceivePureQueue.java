package com.woting.mobile.push.monitor;

/**
 * 
 * @author wanghui
 */
public class DealReceivePureQueue extends Thread{
    /**
     * 给线程起一个名字的构造函数
     * @param name 线程名称
     */
    public DealReceivePureQueue(String name) {
        super("原生消息接收队列处理线程"+((name==null||name.trim().length()==0)?"":"::"+name));
        
    }

    @Override
    public void run() {
        System.out.println(this.getName()+"开始执行");
        
        
    }
}