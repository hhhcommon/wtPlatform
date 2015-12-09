package com.woting.mobile.push.monitor;

import java.util.TimerTask;
import com.woting.mobile.push.mem.PushMemoryManage;

public class CleanPushMemoryTask extends TimerTask {
    @Override
    public void run() {
        try {
            PushMemoryManage ppm = PushMemoryManage.getInstance();
            ppm.clean();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
