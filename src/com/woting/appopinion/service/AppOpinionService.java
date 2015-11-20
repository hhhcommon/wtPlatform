package com.woting.appopinion.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.util.SequenceUUID;
import com.woting.appopinion.model.AppOpinion;
import com.woting.appopinion.persistence.pojo.AppOpinionPo;
import com.woting.appopinion.persistence.pojo.AppReOpinionPo;

public class AppOpinionService {
    @Resource(name="defaultDAO")
    private MybatisDAO<AppOpinionPo> opinionDao;
    @Resource(name="defaultDAO")
    private MybatisDAO<AppReOpinionPo> reOpinionDao;

    @PostConstruct
    public void initParam() {
        opinionDao.setNamespace("WT_APPOPINION");
        reOpinionDao.setNamespace("WT_APPREOPINION");
    }

    /**
     * 保存用户所提意见
     * @param user 用户信息
     * @return 创建用户成功返回1，否则返回0
     */
    public int insertOpinion(AppOpinionPo opinion) {
        int i=0;
        try {
            opinion.setId(SequenceUUID.getUUIDSubSegment(4));
            opinionDao.insert(opinion);
            i=1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * 根据用户指标（userId或Imei）得到意见及反馈列表
     * @param userId
     * @param imei
     * @return 意见及反馈列表
     */
    public List<AppOpinion> getOpinionsByOnwerId(String userId, String imei) {
        try {
            Map<String, String> param = new HashMap<String, String>();
            param.put("userId", userId);
            param.put("imei", imei);
            this.opinionDao.queryForList("", param);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}