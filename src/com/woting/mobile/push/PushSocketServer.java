package com.woting.mobile.push;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import com.spiritdata.framework.util.JsonUtils;
import com.woting.mobile.MobileUtils;
import com.woting.mobile.model.MobileKey;
import com.woting.mobile.push.mem.PushMemoryManage;
import com.woting.mobile.push.model.Message;

public class PushSocketServer extends Thread {
    private PushConfig pc=null;
    private static ServerSocket serverSocket=null;
    private static PushMemoryManage pmm=PushMemoryManage.getInstance();

    public static void closeServerSocket() {
        if (PushSocketServer.serverSocket!=null) {
            try {
                PushSocketServer.serverSocket.close();
            } catch(Exception e) {
            }
        }
    }

    public PushSocketServer(PushConfig pc) {
        super("推送服务监控进程["+pc.getPORT_PUSHSERVER()+"]");
        this.pc=pc;
    }

    public void run() {
        try {
            PushSocketServer.serverSocket=new ServerSocket(this.pc.getPORT_PUSHSERVER());
            System.out.println("地址["+PushSocketServer.serverSocket.getInetAddress().getHostAddress()+"]:端口["+pc.getPORT_PUSHSERVER()+"]启动推送服务监控进程");
            //加一个关闭jvm时可调用的方法，关闭此线程池
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        System.out.println("JVM退出时关闭推送服务监控进程");
                        PushSocketServer.closeServerSocket();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            pmm.setServerIsRuning(true);
            while (true) {
                Socket client = serverSocket.accept();
                new HandlerSocket(client);
            }
        } catch(Exception e) {
            pmm.setServerIsRuning(false);
        } finally {
            PushSocketServer.closeServerSocket();
        }
    }

    /*
     * 处理Socket的线程
     */
    private static class HandlerSocket implements Runnable {
        private Socket socket=null;
        public HandlerSocket(Socket client) {
            socket = client;
            new Thread(this, "处理Socket["+socket.getInetAddress().getHostAddress()+":"+socket.getLocalPort()+"]").start();
        }

        public void run() {
            BufferedReader in=null;
            PrintWriter out=null;
            try {
                //接收到的消息放入队列
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                String revMsgStr = in.readLine();
                pmm.getReceiveMemory().addPureQueue(revMsgStr);

                //得到客户端标识
                //从Json字符串转换为Map
                @SuppressWarnings("unchecked")
                Map<String, Object> recMap = (Map<String, Object>)JsonUtils.jsonToObj(revMsgStr, Map.class);
                MobileKey mk = MobileUtils.getMobileKey(recMap);

                //返回消息
                List<Message> msg=pmm.getSendMessages(mk);
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
                //组装返回字符串
                
                out.println(JsonUtils.objToJson(msg));
            } catch (Exception e) {
                System.out.println("服务器 run 异常: " + e.getMessage());
            } finally {
                try {
                    if (in!=null)
                        try {in.close();in=null;} catch(Exception e) {in=null;throw e;};
                    if (out!=null)
                        try {out.close();out=null;} catch(Exception e) {out=null;throw e;};
                    if (socket!=null)
                        try {socket.close();socket=null;} catch (Exception e) {socket = null;throw e;};
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}