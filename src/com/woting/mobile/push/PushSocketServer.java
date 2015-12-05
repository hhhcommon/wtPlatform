package com.woting.mobile.push;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

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
            System.out.println("用地址["+PushSocketServer.serverSocket.getInetAddress()
            +":"+PushSocketServer.serverSocket.getChannel()
            +":"+PushSocketServer.serverSocket.getLocalSocketAddress()
            +":"+PushSocketServer.serverSocket.getReuseAddress()+"]在端口["+pc.getPORT_PUSHSERVER()+"]启动推送服务监控进程");
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
            new Thread(this, "处理Socket["+socket.getInetAddress()+":"+socket.getLocalPort()+"]").start();
        }

        public void run() {
            try {
                //接收到的消息放入队列
                DataInputStream input = new DataInputStream(socket.getInputStream());
                String revMsgStr=input.readUTF();
                System.out.println("收到内容"+revMsgStr);
                pmm.getReceiveMemory().addPureQueue(revMsgStr);
                //得到客户端标识
                MobileKey mk = new MobileKey();
                //返回消息
                Message msg=pmm.getSendMemory().pollTypeQueue(mk);
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF("这是一个测试:"+(new java.util.Date()).toLocaleString());
                out.close();
                input.close();

                Thread.sleep(10000);
                
                out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF("这是一个测试另一个:"+(new java.util.Date()).toLocaleString());
                out.close();
            } catch (Exception e) {
                System.out.println("服务器 run 异常: " + e.getMessage());
            } finally {
                if (socket!=null) {  
                    try {
                        socket.close();
                    } catch (Exception e) {
                        socket = null; 
                        System.out.println("服务端 finally 异常:" + e.getMessage());
                    }
                }
            }
        }
    }
}