package com.woting.passport.UGA.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.woting.passport.UGA.persistence.pojo.Group;
import com.woting.passport.UGA.persistence.pojo.GroupUser;
import com.woting.passport.UGA.persistence.pojo.User;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.util.SequenceUUID;

public class GroupService {
    @Resource(name="defaultDAO")
    private MybatisDAO<Group> groupDao;

    @PostConstruct
    public void initParam() {
        groupDao.setNamespace("WT_GROUP");
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
    public int insertGroup(Group group) {
        int i=0;
        try {
            group.setGroupId(SequenceUUID.getUUIDSubSegment(4));
            groupDao.insert(group);
            //插入用户
            List<User> ul = group.getGroupUsers();
            if (ul!=null&&ul.size()>0) {
                for (User u: ul) this.insertGroupUser(group, u);
            }
            i=1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * 创建用户用户组关系
     * @param group 用户组
     * @param u 用户
     */
    public int insertGroupUser(Group g, User u) {
        GroupUser gu=new GroupUser();
        gu.setId(SequenceUUID.getUUIDSubSegment(4));
        gu.setGroupId(g.getGroupId());
        gu.setUserId(u.getUserId());
        gu.setInviter(g.getCreateUserId());
        int i=0;
        try {
            groupDao.insert("insertGroupUser", gu);
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

    /**
     * 
     * @param userId
     * @return
     */
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