package com.woting.mobile.push.model;

import java.io.Serializable;

/**
 * 发送或接收到的消息，包括数据和标识信息。<br/>
 * 标识信息类似协议中的包头；<br/>
 * 数据类似协议中的包体；这里规定，包体必须是json串。
 * @author wanghui
 */
/*
 * 消息地址说明：这里的地址只说明设备以及设备上的可分别的状态
 * 标识=>字符串
 * #分类=>字符串
 * 设备标识=>字符串
 * 设备分类=>字符串，目前只有S和M，S表示服务器，M表示移动端
 * 
 * #设备状态=标识::分类
 * 地址={(设备状态)@@(设备标识||设备分类)[地址别名]}
 * #地址别名=地址
 * 地址组=地址,地址,...,地址
 *
 * 特别：
 * 1-地址串保留字——{ } ( ) :: || ,
 * 2-前面带#是可选的属性，若地址不包含这个属性，则此属性前的连接保留字也要省略
 *
 * 例子：
 * 单地址1—{(abf31988c5e2::wt)@@(187020EEE01FC027B3218BA69FEB7D0D||M)}，由UserId和Imei组成的地址
 * 单地址2—{()@@(187020EEE01FC027B3218BA69FEB7D0D||M)}，仅由Imei组成的地址
 * 单地址3—{(intercom)@@(www.woting.com||S)}，我听科技服务器，对讲程序
 * 复杂地址4—{(intercom)@@(www.woting.com||S)[{(tomcat::8090)@@(192.34.23.4||S)}]}，我听服务器对讲程序，也就是192.34.23.4服务器上的端口号为8090的程序是Tomcat
 */
public class Message implements Serializable {
    private transient static final long serialVersionUID = -8842284912678595536L;
    private String msgId; //32位消息id
    private String fromAddr; //消息发送地址
    private String proxyAddrs; //消息中转地址，用逗号隔开
    private String toAddr; //消息目标地址
    private String msgType; //消息类型:0是一般类型；1是回复类型
    private String msgBizType; //业务消息类型，根据此类型，框架会对消息进行分发，目前只有对讲一个业务类型"INTERCOM"
    private String msgStr; //简易消息内容，应是基础数据类型的字符串，入11.2、我听科技、woting、true、false等，特别的，在msgType==1时，这个属性就是所回复的消息的Id
    private String msgContent; //消息内容，应该是严格的Json格式，建议这里面的结构如下：{"bType":数据的业务类型,"Data":真正的数据，仍然要是jon数据类型}

    public String getMsgId() {
        return msgId;
    }
    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
    public String getFromAddr() {
        return fromAddr;
    }
    public void setFromAddr(String fromAddr) {
        this.fromAddr = fromAddr;
    }
    public String getProxyAddrs() {
        return proxyAddrs;
    }
    public void setProxyAddrs(String proxyAddrs) {
        this.proxyAddrs = proxyAddrs;
    }
    public String getToAddr() {
        return toAddr;
    }
    public void setToAddr(String toAddr) {
        this.toAddr = toAddr;
    }
    public String getMsgType() {
        return msgType;
    }
    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }
    public String getMsgBizType() {
        return msgBizType;
    }
    public void setMsgBizType(String msgBizType) {
        this.msgBizType = msgBizType;
    }
    public String getMsgStr() {
        return msgStr;
    }
    public void setMsgStr(String msgStr) {
        this.msgStr = msgStr;
    }
    public String getMsgContent() {
        return msgContent;
    }
    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }    
}