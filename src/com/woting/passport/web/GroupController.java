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

import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.woting.mobile.MobileUtils;
import com.woting.mobile.model.MobileParam;
import com.woting.mobile.session.mem.SessionMemoryManage;
import com.woting.mobile.session.model.MobileSession;
import com.woting.mobile.session.model.SessionKey;
import com.woting.passport.UGA.persistence.pojo.User;
import com.woting.passport.UGA.service.GroupService;
import com.woting.passport.UGA.service.UserService;

@Controller
@RequestMapping(value="/group/")
public class GroupController {
    @Resource
    private GroupService groupService;
    @Resource
    private UserService userService;

    private SessionMemoryManage smm=SessionMemoryManage.getInstance();
    /**
     * 创建用户组
     */
    @RequestMapping(value="buildGroup.do")
    @ResponseBody
    public Map<String,Object> buildGroup(HttpServletRequest request) {
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
            //1-得到创建者
            String creator=(String)m.get("Creator");
            if (sk!=null) {
                sk.setSessionId(sessionId);
                MobileSession ms=smm.getSession(sk);
                if (ms==null) {
                    ms=new MobileSession(sk);
                    smm.addOneSession(ms);
                } else {
                    ms.access();
                    if (StringUtils.isNullOrEmptyOrSpace(creator)) {
                        User u = (User)ms.getAttribute("user");
                        if (u!=null) creator = u.getUserId();
                    }
                }
            }
            if (StringUtils.isNullOrEmptyOrSpace(creator)) {
                map.put("ReturnType", "1002");
                map.put("Message", "无法得到创建者");
            } else {
                //创建用户组
                String members = (String)m.get("Menbers");
                if (StringUtils.isNullOrEmptyOrSpace(creator)) {
                    map.put("ReturnType", "1002");
                    map.put("Message", "无法得到组员信息");
                } else {
                    members=creator+","+members;
                    
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
     * 获得用户组
     */
    @RequestMapping(value="getGroupList.do")
    @ResponseBody
    public Map<String,Object> getGroupList(HttpServletRequest request) {
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

            List<Map<String, Object>> gl = new ArrayList<Map<String, Object>>();
            Map<String, Object> g = new HashMap<String, Object>();
            g.put("GroupId", "123456");
            g.put("GroupName", "用户组1");
            g.put("GroupCount", "3");
            g.put("GroupImg", "images/group.png");
            gl.add(g);
            g = new HashMap<String, Object>();
            g.put("GroupId", "334455");
            g.put("GroupName", "用户组2");
            g.put("GroupCount", "4");
            g.put("GroupImg", "images/group.png");
            gl.add(g);
            g = new HashMap<String, Object>();
            g.put("GroupId", "334466");
            g.put("GroupName", "用户组3");
            g.put("GroupCount", "7");
            g.put("GroupImg", "images/group.png");
            gl.add(g);
            map.put("ReturnType", "1001");
            map.put("SessionId", SequenceUUID.getUUIDSubSegment(4));
            map.put("GroupList", gl);
            return map;
        } catch(Exception e) {
            map.put("ReturnType", "T");
            map.put("SessionId", e.getMessage());
            return map;
        }
    }

    /**
     * 获得用户组
     */
    @RequestMapping(value="getGroupMembers.do")
    @ResponseBody
    public Map<String,Object> getGroupMembers(HttpServletRequest request) {
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

            List<Map<String, Object>> ul = new ArrayList<Map<String, Object>>();
            Map<String, Object> u = new HashMap<String, Object>();
            u.put("UserId", "123456");
            u.put("UserName", "张先生1");
            u.put("Portrait", "images/person.png");
            ul.add(u);
            u = new HashMap<String, Object>();
            u.put("UserId", "334455");
            u.put("UserName", "张先生2");
            u.put("Portrait", "images/person.png");
            ul.add(u);
            u = new HashMap<String, Object>();
            u.put("UserId", "336655");
            u.put("UserName", "张先生3");
            u.put("Portrait", "images/person.png");
            ul.add(u);
            u = new HashMap<String, Object>();
            u.put("UserId", "333sd5");
            u.put("UserName", "张先生4");
            u.put("Portrait", "images/person.png");
            ul.add(u);
            map.put("ReturnType", "1001");
            map.put("SessionId", SequenceUUID.getUUIDSubSegment(4));
            map.put("UserList", ul);
            return map;
        } catch(Exception e) {
            map.put("ReturnType", "T");
            map.put("SessionId", e.getMessage());
            return map;
        }
    }
}