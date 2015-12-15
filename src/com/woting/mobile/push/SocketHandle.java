package com.woting.mobile.push;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.framework.util.StringUtils;
import com.woting.mobile.MobileUtils;
import com.woting.mobile.model.MobileKey;
import com.woting.mobile.push.mem.PushMemoryManage;

/**
 * 处理Socket的线程，此线程是处理一个客户端连接的基础线程。其中还包括三个线程——
 * 发送—心跳线程：每500毫秒发送
 * 发送：
 */
public class SocketHandle extends Thread {
    private Socket socket=null;
    private Thread sendMsg, receiveMsg;
    private boolean run=true;

    private PushMemoryManage pmm=PushMemoryManage.getInstance();

    /*
     * 构造函数，同时线程注册到Map中。
     * @param client 客户端Socket
     */
    public SocketHandle(Socket client) {
        this.socket=client;
        //PushSocketServer.hsMap.put(this.hashCode()+"", this);
    }
    /*
     * 运行程序，接收和发送消息
     */
    public void run() {
//        this.sendMsg=new Thread(new SendMsg(), "Socket["+socket.getInetAddress().getHostAddress()+":"+socket.getLocalPort()+",socketKey="+socket.hashCode()+"]发送消息");
//        this.sendMsg.start();
//        this.receiveMsg=new Thread(new ReceiveMsg(), "Socket["+socket.getInetAddress().getHostAddress()+":"+socket.getLocalPort()+",socketKey="+socket.hashCode()+"]发送消息");
//        this.receiveMsg.start();
//
//        BufferedReader in=null;
//        PrintWriter out=null;
//        try {
//            while(pmm.isServerRuning()&& run) {
//                String msgId=null;
//                boolean isAffirm=false;
//                MobileKey mk=null;
//                //接收到的消息放入队列
//                in=new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
//                String revMsgStr=in.readLine();
//                if (!StringUtils.isNullOrEmptyOrSpace(revMsgStr)) {
//                    //得到客户端标识
//                    //从Json字符串转换为Map
//                    @SuppressWarnings("unchecked")
//                    Map<String, Object> recMap=(Map<String, Object>)JsonUtils.jsonToObj(revMsgStr, Map.class);
//                    if (recMap!=null&&recMap.size()>0) {
//                        recMap.put("_S_STR", revMsgStr);
//                        String __tmp=(String)recMap.get("NeedAffirm");
//                        isAffirm=__tmp==null?false:__tmp.trim().equals("1");
//                        if (isAffirm) msgId=(String)recMap.get("MsgId");
//                        mk=MobileUtils.getMobileKey(recMap);
//                        if (mk!=null) pmm.getReceiveMemory().addPureQueue(recMap);
//                    }
//                }
//
//                //返回消息
//                List<Message> msgList=null;
//                if (mk!=null) {
//                    msgList=pmm.getSendMessages(mk);
//                }
//                out=new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
//                String outStr="[{\"returnType\":\"-1\"}]";//空，无内容
//                if (isAffirm) {
//                    if (StringUtils.isNullOrEmptyOrSpace(msgId)) {
//                        msgId="{\"returnType\":\"-2\"}";//错误内容
//                    } else {
//                        msgId="{\"returnType\":\"0\",\"\":{\"MsgId\":\""+msgId+"\",\"dealFlag\":\"1\"}}";//消息Id为msgId的消息已经处理，处理环节为：已收到
//                    }
//                }
//                //outStr="nothing";
//                if (msgList!=null&&msgList.size()>0) {
//                    outStr="";
//                    for (Message m: msgList) {
//                        outStr+=","+m.getMsgContent();
//                    }
//                    outStr=outStr.substring(1);
//                    if (isAffirm) outStr="["+msgId+","+outStr+"]";
//                    else  outStr="["+outStr+"]";
//                } else {
//                    if (isAffirm) outStr="["+msgId+"]";
//                }
//                out.println(outStr);
//            }
//        } catch (Exception e) {
//            run=false;
//            System.out.println("服务器 run 异常: " + e.getMessage());
//        } finally {
//            try {
//                if (in!=null)
//                    try {in.close();in=null;} catch(Exception e) {in=null;throw e;};
//                if (out!=null)
//                    try {out.close();out=null;} catch(Exception e) {out=null;throw e;};
//                if (socket!=null)
//                    try {socket.close();socket=null;} catch (Exception e) {socket=null;throw e;};
//            } catch(Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

//=====================================================================================
        /*
         * 发送消息线程
         */
        class SendMsg implements Runnable {
            public void run() {
                PrintWriter out=null;
                try {
//                    while(pmm.isServerRuning()&&HandleSocket.this.run) {
//                        //返回消息
//                        List<Message> msgList=null;
//                        if (mk!=null) {
//                            msgList=pmm.getSendMessages(mk);
//                        }
//                        out=new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
//                        String outStr="[{\"returnType\":\"-1\"}]";//空，无内容
//                        if (isAffirm) {
//                            if (StringUtils.isNullOrEmptyOrSpace(msgId)) {
//                                msgId="{\"returnType\":\"-2\"}";//错误内容
//                            } else {
//                                msgId="{\"returnType\":\"0\",\"\":{\"MsgId\":\""+msgId+"\",\"dealFlag\":\"1\"}}";//消息Id为msgId的消息已经处理，处理环节为：已收到
//                            }
//                        }
//                        //outStr="nothing";
//                        if (msgList!=null&&msgList.size()>0) {
//                            outStr="";
//                            for (Message m: msgList) {
//                                outStr+=","+m.getMsgContent();
//                            }
//                            outStr=outStr.substring(1);
//                            if (isAffirm) outStr="["+msgId+","+outStr+"]";
//                            else  outStr="["+outStr+"]";
//                        } else {
//                            if (isAffirm) outStr="["+msgId+"]";
//                        }
//                        out.println(outStr);
//                    }
                } catch (Exception e) {
                    run=false;
                    System.out.println("服务器 run 异常: " + e.getMessage());
                } finally {
                    try {
                        if (out!=null)
                            try {out.close();out=null;} catch(Exception e) {out=null;throw e;};
                        if (socket!=null)
                            try {socket.close();socket=null;} catch (Exception e) {socket=null;throw e;};
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
//        /*
//         * 接收消息线程
//         */
//        class ReceiveMsg implements Runnable{
//            public void run() {
//                BufferedReader in=null;
//                PrintWriter out=null;//若是确认消息才用得到
//                try {
//                    while(pmm.isServerRuning()&&HandleSocket.this.run) {
//                        String msgId=null;
//                        boolean isAffirm=false;
//                        MobileKey mk=null;
//                        //接收到的消息放入队列
//                        in=new BufferedReader(new InputStreamReader(HandleSocket.this.socket.getInputStream(), "UTF-8"));
//                        String revMsgStr=in.readLine();
//                        if (!StringUtils.isNullOrEmptyOrSpace(revMsgStr)) {
//                            //得到客户端标识
//                            //从Json字符串转换为Map
//                            @SuppressWarnings("unchecked")
//                            Map<String, Object> recMap=(Map<String, Object>)JsonUtils.jsonToObj(revMsgStr, Map.class);
//                            if (recMap!=null&&recMap.size()>0) {
//                                recMap.put("_S_STR", revMsgStr);
//                                String __tmp=(String)recMap.get("NeedAffirm");
//                                isAffirm=__tmp==null?false:__tmp.trim().equals("1");
//                                if (isAffirm) msgId=(String)recMap.get("MsgId");
//                                mk=MobileUtils.getMobileKey(recMap);
//                                if (mk!=null) pmm.getReceiveMemory().addPureQueue(recMap);
//                            }
//                        }
//                        out=new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
//                        String outStr="[{\"returnType\":\"-1\"}]";//空，无内容
//                        if (isAffirm) {
//                            if (StringUtils.isNullOrEmptyOrSpace(msgId)) {
//                                msgId="{\"returnType\":\"-2\"}";//错误内容
//                            } else {
//                                msgId="{\"returnType\":\"0\",\"\":{\"MsgId\":\""+msgId+"\",\"dealFlag\":\"1\"}}";//消息Id为msgId的消息已经处理，处理环节为：已收到
//                            }
//                        }
//                        out.println(outStr);
//                        out.flush();
//                    }
//                } catch (Exception e) {
//                    HandleSocket.this.run=false;
//                    System.out.println("消息接收异常" + e.getMessage());
//                } finally {
//                    try {
//                        if (in!=null)
//                            try {in.close();in=null;} catch(Exception e) {in=null;throw e;};
//                        if (out!=null)
//                            try {out.close();out=null;} catch(Exception e) {out=null;throw e;};
//                    } catch(Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }

}