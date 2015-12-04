package com.woting.mobile.push.monitor;

import com.woting.mobile.push.PushConfig;
import com.woting.mobile.push.mem.ReceiveMemory;

public class PushListener extends Thread {
    private static PushConfig pc=null;//推送服务的配置信息

    public static void begin(PushConfig pc) {
        PushListener.pc=pc;
        PushListener pl = new PushListener();
        pl.start();
    }

    @Override
    public void run() {
        try {
            sleep(5000);//多少毫秒后启动任务处理，先让系统的其他启动任务完成，这里设置死为10秒钟
            //初始化内存结构
            ReceiveMemory.getInstance();
            //启动读取线程
            int i=0;
            for (;i<pc.getTHREADCOUNT_DEALRECEIVEQUEUE(); i++) {
                DealReceivePureQueue drpq = new DealReceivePureQueue(""+i);
                drpq.setDaemon(true);
                drpq.start();
            }
            //启动读取-接收队列-线程
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}