package com.woting.passport.UGA.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.woting.passport.UGA.persistence.pojo.Group;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.util.SequenceUUID;

public class GroupService {
    @Resource(name="defaultDAO")
    private MybatisDAO<Group> groupDao;

    @PostConstruct
    public void initParam() {
        groupDao.setNamespace("WT_USER");
    }

    public Group getUserByLoginName(String loginName) {
        try {
            return groupDao.getInfoObject("getUserByLoginName", loginName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Group getUserById(String userId) {
        try {
            return groupDao.getInfoObject("getUserById", userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建用户
     * @param user 用户信息
     * @return 创建用户成功返回1，否则返回0
     */
    public int insertUser(Group group) {
        int i=0;
        try {
            group.setGroupId(SequenceUUID.getUUIDSubSegment(4));
            groupDao.insert(group);
            i=1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * 更新用户
     * @param user 用户信息
     * @return 更新用户成功返回1，否则返回0
     */
    public int updateUser(Group group) {
        int i=0;
        try {
            groupDao.update(group);
            i=1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    public List<Group> getFriendList(String userId) {
        try {
            List<Group> ul=groupDao.queryForList();
            return ul;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}