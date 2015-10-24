package com.woting.mobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;

import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.framework.util.StringUtils;
import com.woting.mobile.model.MobileParam;

/**
 * 移动的公共处理
 * @author wh
 */
public abstract class MobileUtils {
    public static Map<String, Object> getDataFromRequest(ServletRequest req) {
        BufferedReader br = null;
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader((ServletInputStream)req.getInputStream(), "UTF-8");
            br = new BufferedReader(isr);
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line=br.readLine())!=null) sb.append(line);
            line = sb.toString();
            if (line!=null&&line.length()>0) {
                return (Map<String, Object>)JsonUtils.jsonToObj(sb.toString(), Map.class);
            }
            System.out.println(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (isr!=null) try {isr.close();} catch(Exception e) {}
            if (br!=null) try {br.close();} catch(Exception e) {}
        }
        return null;
    }

    public static MobileParam getMobileParam(Map<String, Object> m) {
        if (m==null||m.size()==0) return null;
        MobileParam mp = new MobileParam();
        mp.setImei(m.get("IMEI")+"");
        mp.setMType(m.get("MobileType")+"");
        mp.setGps(m.get("GPS")+"");
        mp.setScreenSize(m.get("ScreenSize")+"");
        mp.setSessionId(m.get("SessionId")+"");
        mp.setMachine(m.get("Machine")+"");

        if (StringUtils.isNullOrEmptyOrSpace(mp.getMType())&&
            StringUtils.isNullOrEmptyOrSpace(mp.getMType())&&
            StringUtils.isNullOrEmptyOrSpace(mp.getMType())&&
            StringUtils.isNullOrEmptyOrSpace(mp.getMType())&&
            StringUtils.isNullOrEmptyOrSpace(mp.getMType())&&
            StringUtils.isNullOrEmptyOrSpace(mp.getMType())&&
            StringUtils.isNullOrEmptyOrSpace(mp.getMachine())) return null;
        return mp;
    }
}