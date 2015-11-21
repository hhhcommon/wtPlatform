package com.woting.passport.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.framework.util.StringUtils;
import com.woting.mobile.MobileUtils;
import com.woting.mobile.model.MobileParam;
import com.woting.mobile.session.mem.SessionMemoryManage;
import com.woting.mobile.session.model.MobileSession;
import com.woting.mobile.session.model.SessionKey;
import com.woting.passport.UGA.persistence.pojo.User;
import com.woting.passport.UGA.service.UserService;

@Controller
@RequestMapping(value="/passport/friend/")
public class FriendController {
    @Resource
    private UserService userService;
    private SessionMemoryManage smm=SessionMemoryManage.getInstance();

    /**
     * 得到好友列表
     */
    @RequestMapping(value="searchStranger.do")
    @ResponseBody
    public Map<String,Object> searchStranger(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<String, Object>();
        try {
            //0-获取参数
            Map<String, Object> m=MobileUtils.getDataFromRequest(request);
            if (m==null||m.size()==0) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获取需要的参数");
                return map;
            }
            MobileParam mp=MobileUtils.getMobileParam(m);
            SessionKey sk=(mp==null?null:mp.getSessionKey());

            //2-获取UserId
            String userId=(String)m.get("UserId");
            if (sk!=null) {
                map.put("SessionId", sk.getSessionId());
                MobileSession ms=smm.getSession(sk);
                if (ms==null) {
                    ms=new MobileSession(sk);
                    smm.addOneSession(ms);
                } else {
                    if (StringUtils.isNullOrEmptyOrSpace(userId)) {
                        userId=sk.getSessionId();
                        if (userId.length()!=12) {
                            userId=null;
                            User u = (User)ms.getAttribute("user");
                            if (u!=null) userId = u.getUserId();
                        }
                    }
                    ms.access();
                }
            }
            String SearchStr=(String)m.get("SearchStr");
            if (StringUtils.isNullOrEmptyOrSpace(SearchStr)) {
                map.put("ReturnType", "1003");
                map.put("Message", "搜索信息不能为空");
                return map;
            }
            if (StringUtils.isNullOrEmptyOrSpace(userId)) {
                map.put("ReturnType", "1002");
                map.put("Message", "无法获取用户Id");
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
                            um.put("PhoneNum", u.getMainPhoneNum());
                            um.put("Portrait", "images/person.png");//还要改变！！！
                            rul.add(um);
                        }
                    }
                    map.put("ReturnType", "1001");
                    map.put("UserList", rul);
                } else {
                    map.put("ReturnType", "1011");
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
     * 邀请陌生人为好友
     */
    @RequestMapping(value="invite.do")
    @ResponseBody
    public Map<String,Object> invite(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<String, Object>();
        try {
            //0-获取参数
            Map<String, Object> m=MobileUtils.getDataFromRequest(request);
            if (m==null||m.size()==0) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获取需要的参数");
                return map;
            }
            MobileParam mp=MobileUtils.getMobileParam(m);
            SessionKey sk=(mp==null?null:mp.getSessionKey());

            //2-获取UserId
            String userId=(String)m.get("UserId");
            if (sk!=null) {
                map.put("SessionId", sk.getSessionId());
                MobileSession ms=smm.getSession(sk);
                if (ms==null) {
                    ms=new MobileSession(sk);
                    smm.addOneSession(ms);
                } else {
                    if (StringUtils.isNullOrEmptyOrSpace(userId)) {
                        userId=sk.getSessionId();
                        if (userId.length()!=12) {
                            userId=null;
                            User u = (User)ms.getAttribute("user");
                            if (u!=null) userId = u.getUserId();
                        }
                    }
                    ms.access();
                }
            }
            String inviteUserId=(String)m.get("InviteUserId");
            if (StringUtils.isNullOrEmptyOrSpace(inviteUserId)) {
                map.put("ReturnType", "1003");
                map.put("Message", "被邀请人Id为空");
                return map;
            }
            String inviteMsg=(String)m.get("inviteMsg");
            if (StringUtils.isNullOrEmptyOrSpace(userId)) {
                map.put("ReturnType", "1002");
                map.put("Message", "无法获取邀请人Id");
            } else {
                map.put("ReturnType", "1001");
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
     * 得到邀请我列表
     */
    @RequestMapping(value="getInvitedMeList.do")
    @ResponseBody
    public Map<String,Object> getInvitedMeList(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<String, Object>();
        try {
            //0-获取参数
            Map<String, Object> m=MobileUtils.getDataFromRequest(request);
            if (m==null||m.size()==0) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获取需要的参数");
                return map;
            }
            MobileParam mp=MobileUtils.getMobileParam(m);
            SessionKey sk=(mp==null?null:mp.getSessionKey());

            //2-获取UserId
            String userId=(String)m.get("UserId");
            if (sk!=null) {
                map.put("SessionId", sk.getSessionId());
                MobileSession ms=smm.getSession(sk);
                if (ms==null) {
                    ms=new MobileSession(sk);
                    smm.addOneSession(ms);
                } else {
                    if (StringUtils.isNullOrEmptyOrSpace(userId)) {
                        userId=sk.getSessionId();
                        if (userId.length()!=12) {
                            userId=null;
                            User u = (User)ms.getAttribute("user");
                            if (u!=null) userId = u.getUserId();
                        }
                    }
                    ms.access();
                }
            }
            if (StringUtils.isNullOrEmptyOrSpace(userId)) {
                map.put("ReturnType", "1002");
                map.put("Message", "无法获取用户Id");
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
                            um.put("InviteMesage", "邀请你作为"+u.getLoginName()+"的好友");
                            um.put("Portrait", "images/person.png");//还要改变！！！
                            rul.add(um);
                        }
                    }
                    map.put("ReturnType", "1001");
                    map.put("UserList", rul);
                } else {
                    map.put("ReturnType", "1011");
                    map.put("Message", "邀请我列表已经都进行了处理");
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
     * 邀请陌生人为好友
     */
    @RequestMapping(value="inviteDeal.do")
    @ResponseBody
    public Map<String,Object> inviteDeal(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<String, Object>();
        try {
            //0-获取参数
            Map<String, Object> m=MobileUtils.getDataFromRequest(request);
            if (m==null||m.size()==0) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获取需要的参数");
                return map;
            }
            MobileParam mp=MobileUtils.getMobileParam(m);
            SessionKey sk=(mp==null?null:mp.getSessionKey());

            //2-获取UserId
            String userId=(String)m.get("UserId");
            if (sk!=null) {
                map.put("SessionId", sk.getSessionId());
                MobileSession ms=smm.getSession(sk);
                if (ms==null) {
                    ms=new MobileSession(sk);
                    smm.addOneSession(ms);
                } else {
                    if (StringUtils.isNullOrEmptyOrSpace(userId)) {
                        userId=sk.getSessionId();
                        if (userId.length()!=12) {
                            userId=null;
                            User u = (User)ms.getAttribute("user");
                            if (u!=null) userId = u.getUserId();
                        }
                    }
                    ms.access();
                }
            }
            String dealType=(String)m.get("DealType");
            if (StringUtils.isNullOrEmptyOrSpace(dealType)) {
                map.put("ReturnType", "1002");
                map.put("Message", "没有处理类型dealType，无法处理");
                return map;
            }
            String refuseMsg=(String)m.get("RefuseMsg");
            if (StringUtils.isNullOrEmptyOrSpace(userId)) {
                map.put("ReturnType", "1002");
                map.put("Message", "无法获取处理人Id");
            } else {
                map.put("ReturnType", "1001");
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
     * 得到好友列表
     */
    @RequestMapping(value="getList.do")
    @ResponseBody
    public Map<String,Object> getFriendList(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<String, Object>();
        try {
            //0-获取参数
            Map<String, Object> m=MobileUtils.getDataFromRequest(request);
            if (m==null||m.size()==0) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获取需要的参数");
                return map;
            }
            MobileParam mp=MobileUtils.getMobileParam(m);
            SessionKey sk=(mp==null?null:mp.getSessionKey());

            //2-获取UserId
            String userId=(String)m.get("UserId");
            if (sk!=null) {
                map.put("SessionId", sk.getSessionId());
                MobileSession ms=smm.getSession(sk);
                if (ms==null) {
                    ms=new MobileSession(sk);
                    smm.addOneSession(ms);
                } else {
                    if (StringUtils.isNullOrEmptyOrSpace(userId)) {
                        userId=sk.getSessionId();
                        if (userId.length()!=12) {
                            userId=null;
                            User u = (User)ms.getAttribute("user");
                            if (u!=null) userId = u.getUserId();
                        }
                    }
                    ms.access();
                }
            }
            if (StringUtils.isNullOrEmptyOrSpace(userId)) {
                map.put("ReturnType", "1002");
                map.put("Message", "无法获取用户Id");
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
                    map.put("ReturnType", "1011");
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
}