package com.woting.passport.login.service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.util.SequenceUUID;
import com.woting.passport.login.persistence.pojo.MobileUsed;

/**
 * 移动设备用户使用情况
 * @author wh
 */
public class MobileUsedService {
    @Resource(name="defaultDAO")
    private MybatisDAO<MobileUsed> muDao;

    @PostConstruct
    public void initParam() {
        muDao.setNamespace("MOBILE_USED");
    }

    /**
     * 保存用户使用情况
     * @param mu
     */
    public void saveMobileUsed(MobileUsed mu) {
        try {
            mu.setMuId(SequenceUUID.getUUIDSubSegment(4));
            muDao.insert(mu);
        } catch(Exception e) {
            e.printStackTrace();
            muDao.update("updateByIMEI", mu);
        }
    }

    /**
     * 根据手机串号，获取最后使用情况
     * @param imei 手机串号
     */
    public MobileUsed getUsedInfo(String imei) {
        return muDao.getInfoObject("getUsedInfo", imei);
    }
}