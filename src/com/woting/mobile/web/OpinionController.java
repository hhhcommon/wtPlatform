package com.woting.mobile.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.woting.mobile.MobileUtils;
import com.woting.mobile.model.MobileParam;
import com.woting.mobile.session.mem.SessionMemoryManage;
import com.woting.mobile.session.model.MobileSession;
import com.woting.mobile.session.model.SessionKey;

@Controller
@RequestMapping(value="/opinion/app/")
public class OpinionController {

    private SessionMemoryManage smm=SessionMemoryManage.getInstance();

    @RequestMapping(value="commit.do")
    @ResponseBody
    public Map<String,Object> getVersion(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<String, Object>();
        try {
            //0-处理访问
            Map<String, Object> m=MobileUtils.getDataFromRequest(request);
            if (m!=null&&m.size()>0) {
                MobileParam mp=MobileUtils.getMobileParam(m);
                SessionKey sk=(mp==null?null:mp.getSessionKey());
                if (sk!=null){
                    map.put("SessionId", sk.getSessionId());
                    MobileSession ms=smm.getSession(sk);
                    if (ms!=null) ms.access();
                }
            }
            //1-获取意见信息
            map.put("ReturnType", "1001");
            return map;
        } catch(Exception e) {
            e.printStackTrace();
            map.put("ReturnType", "T");
            map.put("TClass", e.getClass().getName());
            map.put("Message", e.getMessage());
            return map;
        }
    }

    @RequestMapping(value="getList.do")
    @ResponseBody
    public Map<String,Object> getList(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<String, Object>();
        try {
            //0-处理访问
            Map<String, Object> m=MobileUtils.getDataFromRequest(request);
            if (m!=null&&m.size()>0) {
                MobileParam mp=MobileUtils.getMobileParam(m);
                SessionKey sk=(mp==null?null:mp.getSessionKey());
                if (sk!=null){
                    map.put("SessionId", sk.getSessionId());
                    MobileSession ms=smm.getSession(sk);
                    if (ms!=null) ms.access();
                }
                //获取用户信息
            }
            List<Map<String, Object>> ol = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> rel=null;
            Map<String, Object> selfOpinion=null, reOpinion=null;
            //------------------
            selfOpinion=new HashMap<String, Object>();//一个分类
            selfOpinion.put("OpinionId", "001");
            selfOpinion.put("Opinion", "建议001");
            rel = new ArrayList<Map<String, Object>>();
            reOpinion=new HashMap<String, Object>();
            reOpinion.put("OpinionReId", "1001"); //电台
            reOpinion.put("ReOpinion", "回复1");
            rel.add(reOpinion);
            reOpinion=new HashMap<String, Object>();
            reOpinion.put("OpinionReId", "1002"); //电台
            reOpinion.put("ReOpinion", "回复2");
            rel.add(reOpinion);
            selfOpinion.put("ReList", rel);
            ol.add(selfOpinion);

            //------------------
            selfOpinion=new HashMap<String, Object>();//一个分类
            selfOpinion.put("OpinionId", "002");
            selfOpinion.put("Opinion", "建议002");
            rel = new ArrayList<Map<String, Object>>();
            reOpinion=new HashMap<String, Object>();
            reOpinion.put("OpinionReId", "1001"); //电台
            reOpinion.put("ReOpinion", "回复1-建议002");
            rel.add(reOpinion);
            reOpinion=new HashMap<String, Object>();
            reOpinion.put("OpinionReId", "1002"); //电台
            reOpinion.put("ReOpinion", "回复2-建议002");
            rel.add(reOpinion);
            selfOpinion.put("ReList", rel);
            ol.add(selfOpinion);

            //------------------
            selfOpinion=new HashMap<String, Object>();//一个分类
            selfOpinion.put("OpinionId", "003");
            selfOpinion.put("Opinion", "建议003");
            rel = new ArrayList<Map<String, Object>>();
            reOpinion=new HashMap<String, Object>();
            reOpinion.put("OpinionReId", "1001"); //电台
            reOpinion.put("ReOpinion", "回复1-建议003");
            rel.add(reOpinion);
            reOpinion=new HashMap<String, Object>();
            reOpinion.put("OpinionReId", "1002"); //电台
            reOpinion.put("ReOpinion", "回复3-建议003");
            rel.add(reOpinion);
            reOpinion=new HashMap<String, Object>();
            reOpinion.put("OpinionReId", "1003"); //电台
            reOpinion.put("ReOpinion", "回复3-建议003");
            rel.add(reOpinion);
            selfOpinion.put("ReList", rel);
            ol.add(selfOpinion);

            map.put("ReturnType", "1001");
            map.put("OpinionList", ol);
            return map;
        } catch(Exception e) {
            e.printStackTrace();
            map.put("ReturnType", "T");
            map.put("TClass", e.getClass().getName());
            map.put("Message", e.getMessage());
            return map;
        }
    }
}