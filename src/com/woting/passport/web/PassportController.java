package com.woting.passport.web;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.woting.passport.UGA.service.UserService;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.woting.mobile.MobileUtils;
import com.woting.mobile.model.MobileParam;
import com.woting.mobile.session.mem.SessionMemoryManage;
import com.woting.mobile.session.model.MobileSession;
import com.woting.mobile.session.model.SessionKey;
import com.woting.passport.UGA.persistence.pojo.User;
import com.woting.passport.login.persistence.pojo.MobileUsed;
import com.woting.passport.login.service.MobileUsedService;

@Controller
@RequestMapping(value="/passport/")
public class PassportController {
    @Resource
    private UserService userService;
    @Resource
    private MobileUsedService muService;

    private SessionMemoryManage smm=SessionMemoryManage.getInstance();

    /**
     * 进入App
     * @param request 请求对象。数据包含在Data流中，以json格式存储，其中必须包括手机串号。如：{"imei":"123456789023456789"}
     * @return 分为如下情况<br/>
     *   若有异常：{ReturnType:T, TClass:exception.class, Message: e.getMessage()}
     *   已经登录：{ReturnType:1001, sessionId:sid, userInfo:{userName:un, mphone:138XXXX2345, email:a@b.c, realName:实名, headImg:hiUrl}}
     *     其中用户信息若没有相关内容，则相关的key:value对就不存在
     *   还未登录：{ReturnType:1002, sessionId:sid}
     */
    @RequestMapping(value="entryApp.do")
    @ResponseBody
    public Map<String,Object> entryApp(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<String, Object>();
        try {
            //0-获得参数
            Map<String, Object> m=MobileUtils.getDataFromRequest(request);
            if (m==null) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获得需要的参数");
                return map;
            }
            MobileParam mp=MobileUtils.getMobileParam(m);
            SessionKey sk=(mp==null?null:mp.getSessionKey());
            String sessionId=(mp==null?null:mp.getSessionId());
            sessionId=(sessionId==null?SequenceUUID.getUUIDSubSegment(4):sessionId);
            map.put("SessionId", sessionId);
            //1-没有IMEI，按未登录处理
            if (sk==null) {
                map.put("ReturnType", "1002");
                return map;

            }
            sk.setSessionId(sessionId);
            smm.expireAllSessionByIMEI(sk.getMobileId()); //作废所有imei对应的Session
            //3-处理
            MobileUsed mu = muService.getUsedInfo(sk.getMobileId());
            MobileSession ms=null;
            if (mu==null||mu.getStatus()==2) {//上次未登陆
                boolean canCreateSession=false;
                if (mu==null) canCreateSession=true;
                else { //上次是注销
                    ms=smm.getSession(sk); //看看Session中是否有
                    if (ms==null) canCreateSession=true;
                }
                if (canCreateSession) {
                    ms=new MobileSession(sk);
                    smm.addOneSession(ms);
                } else if (ms!=null) {
                    ms.access();
                    ms.clearBody();
                }
                map.put("ReturnType", "1002");
                map.put("SessionId", sessionId);
            } else { //上次是登录
                ms=smm.getUserSession(mu.getUserId(), sk.getMobileId());
                if (ms==null) { //找不到对应的移动会话
                    smm.expireAllSessionByIMEI(sk.getMobileId()); //作废所有imei对应的Session
                    ms=new MobileSession(sk);
                    smm.addOneSession(ms);
                } else { //找到了对应的对话，直接应用
                    ms.access();
                }
                User u = userService.getUserById(mu.getUserId());
                ms.addAttribute("user", u);

                map.put("ReturnType", "1001"); //已登录
                map.put("UserInfo", u.toHashMap4Mobile());
            }
            return map;
        } catch(Exception e) {
            e.printStackTrace();
            map.put("ReturnType", "T");
            map.put("TClass", e.getClass().getName());
            map.put("Message", e.getMessage());
            return map;
        }
    }

    /**
     * 用户登录
     * @throws IOException
     */
    @RequestMapping(value="mlogin.do")
    @ResponseBody
    public Map<String,Object> login(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<String, Object>();
        try {
            //0-获得参数
            Map<String, Object> m=MobileUtils.getDataFromRequest(request);
            if (m==null) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获得需要的参数");
                return map;
            }
            MobileParam mp=MobileUtils.getMobileParam(m);
            SessionKey sk=(mp==null?null:mp.getSessionKey());
            String sessionId=(mp==null?null:mp.getSessionId());
            sessionId=(sessionId==null?SequenceUUID.getUUIDSubSegment(4):sessionId);
            map.put("SessionId", sessionId);

            String ln=(String)m.get("UserName");
            String pwd=(String)m.get("Password");
            String errMsg="";
            if (StringUtils.isNullOrEmptyOrSpace(ln)) errMsg+=",用户名为空";
            if (StringUtils.isNullOrEmptyOrSpace(pwd)) errMsg+=",密码为空";
            if (!StringUtils.isNullOrEmptyOrSpace(errMsg)) {
                errMsg=errMsg.substring(1);
                map.put("ReturnType", "0000");
                map.put("Message", errMsg+",无法登陆");
                return map;
            }
            User u=userService.getUserByLoginName(ln);
            //1-判断是否存在用户
            if (u==null) { //无用户
                map.put("ReturnType", "1002");
                map.put("Message", "无登录名为["+ln+"]的用户.");
                return map;
            }
            //2-判断密码是否匹配
            if (!u.getPassword().equals(pwd)) {
                map.put("ReturnType", "1003");
                map.put("Message", "密码不匹配.");
                return map;
            }
            //3-用户登录成功
            if (sk!=null) {
                sk.setSessionId(sessionId);
                //3.1-处理Session
                smm.expireAllSessionByIMEI(sk.getSessionId()); //作废所有imei对应的Session
                MobileSession ms=new MobileSession(sk);
                ms.addAttribute("user", u);
                smm.addOneSession(ms);
                //3.2-保存使用情况
                MobileUsed mu=new MobileUsed();
                mu.setImei(sk.getMobileId());
                mu.setStatus(1);
                mu.setUserId(u.getUserId());
                muService.saveMobileUsed(mu);
            }
            //4-没有IMEI，返回成功
            map.put("ReturnType", "1001");
            map.put("UserInfo", u.toHashMap4Mobile());
            return map;
        } catch(Exception e) {
            map.put("ReturnType", "T");
            map.put("TClass", e.getClass().getName());
            map.put("Message", e.getMessage());
            return map;
        }
    }

    /**
     * 用户注册
     * @throws IOException
     */
    @RequestMapping(value="register.do")
    @ResponseBody
    public Map<String,Object> register(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<String, Object>();
        try {
            //0-获得参数
            Map<String, Object> m=MobileUtils.getDataFromRequest(request);
            if (m==null) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获得需要的参数");
                return map;
            }
            MobileParam mp=MobileUtils.getMobileParam(m);
            SessionKey sk=(mp==null?null:mp.getSessionKey());
            String sessionId=(mp==null?null:mp.getSessionId());
            sessionId=(sessionId==null?SequenceUUID.getUUIDSubSegment(4):sessionId);
            map.put("SessionId", sessionId);

            String ln=(String)m.get("UserName");
            String pwd=(String)m.get("Password");
            String errMsg="";
            if (StringUtils.isNullOrEmptyOrSpace(ln)) errMsg+=",用户名为空";
            if (StringUtils.isNullOrEmptyOrSpace(pwd)) errMsg+=",密码为空";
            if (!StringUtils.isNullOrEmptyOrSpace(errMsg)) {
                errMsg=errMsg.substring(1);
                map.put("ReturnType", "0000");
                map.put("Message", errMsg+",无法注册");
                return map;
            }
            User nu=new User();
            nu.setLoginName(ln);
            nu.setPassword(pwd);
            //1-判断是否有重复的用户
            User oldUser=userService.getUserByLoginName(ln);
            if (oldUser!=null) { //重复
                map.put("ReturnType", "1004");
                map.put("Message", "登录名重复,无法注册.");
                return map;
            }
            //2-保存用户
            nu.setCTime(new Timestamp(System.currentTimeMillis()));
            nu.setUserType(1);
            nu.setUserId(SequenceUUID.getUUIDSubSegment(4));
            int rflag=userService.insertUser(nu);
            if (rflag!=1) {
                map.put("ReturnType", "0000");
                map.put("Message", "注册失败.");
                return map;
            }
            //3-注册成功后，自动登陆，及后处理
            if (sk!=null) {
                sk.setSessionId(sessionId);
                //3.1-处理Session
                smm.expireAllSessionByIMEI(sk.getSessionId()); //作废所有imei对应的Session
                MobileSession ms=new MobileSession(sk);
                ms.addAttribute("user", nu);
                smm.addOneSession(ms);
                //3.2-保存使用情况
                MobileUsed mu=new MobileUsed();
                mu.setImei(sk.getMobileId());
                mu.setStatus(1);
                mu.setUserId(nu.getUserId());
                muService.saveMobileUsed(mu);
            }
            //4-没有IMEI，返回成功
            map.put("ReturnType", "1001");
            map.put("UserId", nu.getUserId());
            return map;
        } catch(Exception e) {
            e.printStackTrace();
            map.put("ReturnType", "T");
            map.put("SessionId", e.getMessage());
            return map;
        }
    }

    /**
     * 用户注销
     * @throws IOException
     */
    @RequestMapping(value="mlogout.do")
    @ResponseBody
    public Map<String,Object> mlogout(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<String, Object>();
        try {
            //0-获得参数
            Map<String, Object> m=MobileUtils.getDataFromRequest(request);
            if (m==null) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获得需要的参数");
                return map;
            }
            MobileParam mp=MobileUtils.getMobileParam(m);
            SessionKey sk=(mp==null?null:mp.getSessionKey());
            String sessionId=(mp==null?null:mp.getSessionId());
            sessionId=(sessionId==null?SequenceUUID.getUUIDSubSegment(4):sessionId);
            map.put("SessionId", sessionId);
            //2-处理Session并获得UserId
            String userId=(String)m.get("UserId");
            if (sk!=null) {
                sk.setSessionId(sessionId);
                MobileSession ms=smm.getSession(sk);
                if (ms==null) {
                    ms=new MobileSession(sk);
                    smm.addOneSession(ms);
                } else { //删除掉所有的信息？？？
                    ms.access();
                    ms.clearBody();
                    if (StringUtils.isNullOrEmptyOrSpace(userId)) {
                        User u = (User)ms.getAttribute("user");
                        if (u!=null) userId = u.getUserId();
                    }
                }
            }
            //3-保存使用情况
            if (mp!=null&&!StringUtils.isNullOrEmptyOrSpace(sk.getMobileId())&&!StringUtils.isNullOrEmptyOrSpace(userId)) {
                MobileUsed mu=new MobileUsed();
                mu.setImei(sk.getMobileId());
                mu.setStatus(2);
                mu.setUserId(userId);
                muService.saveMobileUsed(mu);
            }
            //4-没有IMEI，返回成功
            map.put("ReturnType", "1001");
            return map;
        } catch(Exception e) {
            map.put("ReturnType", "T");
            map.put("SessionId", e.getMessage());
            return map;
        }
    }

    /**
     * 绑定用户的其他信息，目前有手机/eMail/头像/实名等信息
     * @throws IOException
     */
    @RequestMapping(value="bindExtUserInfo.do")
    @ResponseBody
    public Map<String,Object> bindExtUserInfo(HttpServletRequest request) {
        System.out.println("===================");
        //返回登录的情况
        return null;
    }

    /**
     * 找回密码——通过手机
     * @throws IOException
     */
    @RequestMapping(value="retrieveByPwd.do")
    @ResponseBody
    public Map<String,Object> retrieveByPwd(HttpServletRequest request) {
        System.out.println("===================");
        //返回登录的情况
        return null;
    }

    /**
     * 找回密码——通过邮箱
     */
    @RequestMapping(value="retrieveByEmail.do")
    @ResponseBody
    public Map<String,Object> retrieveByEmail(HttpServletRequest request) {
        System.out.println("===================");
        //返回登录的情况
        return null;
    }

    /**
     * 得到好友列表
     */
    @RequestMapping(value="getFriendList.do")
    @ResponseBody
    public Map<String,Object> getFriendList(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<String, Object>();
        try {
            //0-获得参数
            Map<String, Object> m=MobileUtils.getDataFromRequest(request);
            if (m==null) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获得需要的参数");
                return map;
            }
            MobileParam mp=MobileUtils.getMobileParam(m);
            SessionKey sk=(mp==null?null:mp.getSessionKey());
            String sessionId=(mp==null?null:mp.getSessionId());
            sessionId=(sessionId==null?SequenceUUID.getUUIDSubSegment(4):sessionId);
            map.put("SessionId", sessionId);
            //2-获得UserId
            String userId=(String)m.get("UserId");
            if (sk!=null) {
                sk.setSessionId(sessionId);
                MobileSession ms=smm.getSession(sk);
                if (ms==null) {
                    ms=new MobileSession(sk);
                    smm.addOneSession(ms);
                } else {
                    ms.access();
                    if (StringUtils.isNullOrEmptyOrSpace(userId)) {
                        User u = (User)ms.getAttribute("user");
                        if (u!=null) userId = u.getUserId();
                    }
                }
            }
            if (StringUtils.isNullOrEmptyOrSpace(userId)) {
                map.put("ReturnType", "1002");
                map.put("Message", "无法找到用户信息");
            } else {
                List<User> ul=userService.getFriendList(userId);
                if (ul!=null&&ul.size()>0) {
                    List<Map<String, Object>> rul=new ArrayList<Map<String, Object>>();
                    Map<String, Object> um;
                    for (User u: ul) {
                        if (!u.getUserId().equals(userId)) {
                            um=new HashMap<String, Object>();
                            um.put("UserId", u.getUserId());
                            um.put("UserName", u.getLoginName());
                            um.put("Portrait", "images/person.png");//还要改变！！！
                            rul.add(um);
                        }
                    }
                    map.put("ReturnType", "1001");
                    map.put("UserList", rul);
                } else {
                    map.put("ReturnType", "1003");
                    map.put("Message", "没有好友");
                }
            }
            return map;
        } catch(Exception e) {
            map.put("ReturnType", "T");
            map.put("TClass", e.getClass().getName());
            map.put("Message", e.getMessage());
            return map;
        }
    }

    /**
     * 得到历史访问列表
     */
    @RequestMapping(value="getHistoryUG.do")
    @ResponseBody
    public Map<String,Object> getHistoryUG(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<String, Object>();
        try {
            //0-获得参数
            Map<String, Object> m=MobileUtils.getDataFromRequest(request);
            if (m==null) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获得需要的参数");
                return map;
            }
            MobileParam mp=MobileUtils.getMobileParam(m);
            List<Map<String, Object>> hl = new ArrayList<Map<String, Object>>();
            Map<String, Object> u, g;
            g = new HashMap<String, Object>();
            g.put("ObjType", "Group");
            g.put("GroupId", "334466");
            g.put("GroupName", "用户组3");
            g.put("GroupCount", "3");
            g.put("GroupImg", "images/group.png");
            hl.add(g);
            u = new HashMap<String, Object>();
            u.put("ObjType", "User");
            u.put("UserId", "123456");
            u.put("UserName", "张先生1");
            u.put("Portrait", "images/person.png");
            hl.add(u);
            u = new HashMap<String, Object>();
            u.put("ObjType", "User");
            u.put("UserId", "336655");
            u.put("UserName", "张先生3");
            u.put("Portrait", "images/person.png");
            g = new HashMap<String, Object>();
            g.put("ObjType", "Group");
            g.put("GroupId", "311466");
            g.put("GroupName", "用户组1");
            g.put("GroupCount", "11");
            g.put("GroupImg", "images/group.png");
            hl.add(g);
            map.put("ReturnType", "1001");
            map.put("SessionId", SequenceUUID.getUUIDSubSegment(4));
            map.put("HistoryList", hl);
            //2-看是否有IMEI
            if (mp!=null&&!StringUtils.isNullOrEmptyOrSpace(mp.getImei())) {
                return map;
            }
            return map;
        } catch(Exception e) {
            map.put("ReturnType", "T");
            map.put("SessionId", e.getMessage());
            return map;
        }
    }
}

//
//List<Map<String, Object>> ul = new ArrayList<Map<String, Object>>();
//Map<String, Object> u = new HashMap<String, Object>();
//u.put("UserId", "123456");
//u.put("UserName", "张先生1");
//u.put("Portrait", "images/person.png");
//ul.add(u);
//u = new HashMap<String, Object>();
//u.put("UserId", "334455");
//u.put("UserName", "张先生2");
//u.put("Portrait", "images/person.png");
//ul.add(u);
//u = new HashMap<String, Object>();
//u.put("UserId", "336655");
//u.put("UserName", "张先生3");
//u.put("Portrait", "images/person.png");
//ul.add(u);
//u = new HashMap<String, Object>();
//u.put("UserId", "333sd5");
//u.put("UserName", "张先生4");
//u.put("Portrait", "images/person.png");
//ul.add(u);
//map.put("ReturnType", "1001");
//map.put("SessionId", SequenceUUID.getUUIDSubSegment(4));
//map.put("UserList", ul);
//