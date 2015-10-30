package com.woting.passport.web;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
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
     *   若有异常：{ReturnType:T, tClass:exception.class, tMessage: e.getMessage()}
     *   已经登录：{ReturnType:1001, sessionId:sid, userInfo:{userName:un, mphone:138XXXX2345, emain:a@b.c, realName:实名, headImg:hiUrl}}
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
            //2-处理
            String sessionId=StringUtils.isNullOrEmptyOrSpace(mp.getSessionId())?SequenceUUID.getUUIDSubSegment(4):mp.getSessionId();
            if (mp!=null&&!StringUtils.isNullOrEmptyOrSpace(mp.getImei())) {
                SessionKey sk=mp.getSessionKey();
                sk.setSessionId(sessionId);
                MobileSession ms=null;

                MobileUsed mu = muService.getUsedInfo(mp.getImei());
                if (mu==null||mu.getStatus()==2) {
                    if (mu==null) { //没有对应的用户使用记录
                        smm.expireAllSessionByIMEI(mp.getImei()); //作废所有imei对应的Session
                        ms=new MobileSession(sk);
                        smm.addOneSession(ms);
                    } else { //上次是注销
                        ms=smm.getSession(sk); //看看Session中是否有
                        if (ms==null) { //无Session，无用户
                            smm.expireAllSessionByIMEI(mp.getImei()); //作废所有imei对应的Session
                            ms=new MobileSession(sk);
                            smm.addOneSession(ms);
                        } else { //有Session，直接用
                            ms.access();
                            ms.clearBody();
                        }
                    }
                    map.put("ReturnType", "1001"); //未登录
                    map.put("SessionId", sessionId);
                    return map;
                } else { //上次是登录
                    ms=smm.getUserSession(mu.getUserId(), mp.getImei());
                    if (ms==null) { //找不到对应的移动会话
                        smm.expireAllSessionByIMEI(mp.getImei()); //作废所有imei对应的Session
                        ms=new MobileSession(sk);
                        smm.addOneSession(ms);
                        User u = userService.getUserById(mu.getUserId());
                        ms.addAttribute("user", u);
                    } else { //找到了对应的对话，直接应用
                        ms.access();
                    }
                    map.put("ReturnType", "1002"); //未登录
                    map.put("SessionId", sessionId);
                    return map;
                }
            }
            //3-没有IMEI，按未登录处理
            map.put("ReturnType", "1001");
            map.put("SessionId", sessionId);
            return map;
        } catch(Exception e) {
            map.put("ReturnType", "T");
            map.put("SessionId", e.getMessage());
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
            //1-判断是否存在用户
            User u=userService.getUserByLoginName(ln);
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
            if (mp!=null&&!StringUtils.isNullOrEmptyOrSpace(mp.getImei())) {
                //3.1-处理Session
                mp.setSessionId(SequenceUUID.getUUIDSubSegment(4));
                smm.expireAllSessionByIMEI(mp.getImei()); //作废所有imei对应的Session
                //创建新的Session
                MobileSession ms=new MobileSession(mp.getSessionKey());
                ms.addAttribute("user", u);
                smm.addOneSession(ms);
                //3.2-保存使用情况
                MobileUsed mu=new MobileUsed();
                mu.setImei(mp.getImei());
                mu.setStatus(1);
                mu.setUserId(u.getUserId());
                muService.saveMobileUsed(mu);
            }
            //4-没有IMEI，返回成功
            map.put("ReturnType", "1001");
            map.put("SessionId", mp.getSessionId());
            map.put("UserInfo", u);
            return map;
        } catch(Exception e) {
            map.put("ReturnType", "T");
            map.put("SessionId", e.getMessage());
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
            int rflag=userService.insertUser(nu);
            if (rflag!=1) {
                map.put("ReturnType", "0000");
                map.put("Message", "注册失败.");
                return map;
            }
            //3-注册成功后，自动登陆，及后处理
            String sessionId=SequenceUUID.getUUIDSubSegment(4);
            if (mp!=null&&!StringUtils.isNullOrEmptyOrSpace(mp.getImei())) {
                //3.1-处理Session
                SessionKey sk=mp.getSessionKey();
                if (StringUtils.isNullOrEmptyOrSpace(sk.getSessionId())) sk.setSessionId(sessionId);
                sessionId=sk.getSessionId();
                MobileSession ms=smm.getSession(sk);
                if (ms==null) {
                    ms=new MobileSession(sk);
                    smm.addOneSession(ms);
                } else {
                    ms.access();
                }
                ms.addAttribute("user", nu);
                //3.2-保存使用情况
                MobileUsed mu=new MobileUsed();
                mu.setImei(mp.getImei());
                mu.setStatus(1);
                mu.setUserId(nu.getUserId());
                muService.saveMobileUsed(mu);
            }
            //4-没有IMEI，返回成功
            map.put("ReturnType", "1001");
            map.put("SessionId", sessionId);
            map.put("UserInfo", nu);
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
            //2-处理Session
            String sessionId=SequenceUUID.getUUIDSubSegment(4);
            if (mp!=null&&!StringUtils.isNullOrEmptyOrSpace(mp.getImei())) {
                SessionKey sk=mp.getSessionKey();
                MobileSession ms=null;
                if (StringUtils.isNullOrEmptyOrSpace(sk.getSessionId())) {//没有Session，生成空的Session，也不用注销
                    sk.setSessionId(sessionId);
                    ms=new MobileSession(sk);
                    smm.addOneSession(ms);
                } else {
                    sessionId=sk.getSessionId();
                    ms=smm.getSession(sk);
                    if (ms==null) {
                        ms=new MobileSession(sk);
                        smm.addOneSession(ms);
                    } else { //删除掉所有的信息？？？
                        ms.access();
                        ms.clearBody();
                    }
                }
            }
            //3-保存使用情况
            if (mp!=null&&!StringUtils.isNullOrEmptyOrSpace(mp.getImei())) {
                MobileUsed mu=new MobileUsed();
                mu.setImei(mp.getImei());
                mu.setStatus(2);
                muService.saveMobileUsed(mu);
            }
            //4-没有IMEI，返回成功
            map.put("ReturnType", "1001");
            map.put("SessionId", sessionId);
            return null;
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
     * @throws IOException
     */
    @RequestMapping(value="retrieveByEmail.do")
    @ResponseBody
    public Map<String,Object> retrieveByEmail(HttpServletRequest request) {
        System.out.println("===================");
        //返回登录的情况
        return null;
    }

    /**
     * 创建新的用户组
     * @throws IOException
     */
    @RequestMapping(value="buildGroup.do")
    @ResponseBody
    public Map<String,Object> buildGroup(HttpServletRequest request) {
        System.out.println("===================");
        //返回登录的情况
        return null;
    }

    /**
     * 把一个或多个用户加入用户组
     * @throws IOException
     */
    @RequestMapping(value="addGroup.do")
    @ResponseBody
    public Map<String,Object> addGroup(HttpServletRequest request) {
        System.out.println("===================");
        //返回登录的情况
        return null;
    }

    /**
     * 把一个或多个用户剔出用户组
     * @throws IOException
     */
    @RequestMapping(value="quitGroup.do")
    @ResponseBody
    public Map<String,Object> quitGroup(HttpServletRequest request) {
        System.out.println("===================");
        //返回登录的情况
        return null;
    }
}