/**001 PLAT_USER（用户）*/
DROP TABLE IF EXISTS plat_User;
CREATE TABLE plat_User (
  id            varchar(32)      NOT NULL                COMMENT 'uuid（用户id）',
  userName      varchar(100)               DEFAULT NULL  COMMENT '用户名称——实名',
  loginName     varchar(15)      NOT NULL                COMMENT '登录账号',
  password      varchar(30)                DEFAULT NULL  COMMENT '密码',
  mainPhoneNum  varchar(100)               DEFAULT NULL  COMMENT '用户主手机号码',
  mailAdress    varchar(100)               DEFAULT NULL  COMMENT '邮箱(非空为一索引)',
  userType      int(1) unsigned  NOT NULL                COMMENT '用户分类：1自然人用户，2机构用户',
  userState     int(1)           NOT NULL  DEFAULT '0'   COMMENT '用户状态，0-2,0代表未激活的用户，1代表已激用户，2代表失效用户,3根据邮箱找密码的用户',
  protraitBig   varchar(300)                             COMMENT '用户头像大',
  protraitMini  varchar(300)                             COMMENT '用户头像小',
  descn         varchar(2000)              DEFAULT NULL  COMMENT '备注',
  cTime         timestamp        NOT NULL  DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间:创建时的系统时间',
  lmTime        timestamp        NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT '最后修改：每次更新的时间',
  PRIMARY KEY(id),
  UNIQUE KEY loginName(loginName) USING BTREE
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

/**002 手机用户使用[PLAT_USER_MOBILEUSED]*/
/**
 * 记录手机最近一次用某账号登录的情况。
 * 若一个月没有登录，是否就删除掉呢，或者是1年??
 */
DROP TABLE IF EXISTS plat_U_MobileUsed;
CREATE TABLE plat_U_MobileUsed (
  id      varchar(32)   NOT NULL  COMMENT 'uuid',
  imei    varchar(100)  NOT NULL  COMMENT '手机串号，手机身份码',
  userId  varchar(32)   NOT NULL  COMMENT '用户Id',
  status  varchar(1)    NOT NULL  COMMENT '状态：1-登录；2-注销；',
  lmTime  timestamp     NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT '最后修改：每次更新的时间',
  PRIMARY KEY(id),
  UNIQUE KEY imei(imei) USING BTREE

)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='手机用户使用';

/**003 PLAT_GROUP（用户组）*/
DROP TABLE IF EXISTS plat_Group;
CREATE TABLE plat_Group (
  id            varchar(32)      NOT NULL                COMMENT 'uuid（用户组id）',
  groupName     varchar(100)     NOT NULL                COMMENT '组名称',
  groupImg      varchar(200)                             COMMENT '用户组头像，是指向头像的URL',
  pId           varchar(32)      NOT NULL  DEFAULT 0     COMMENT '上级用户组名称，默认0，为根',
  sort          int(5) unsigned  NOT NULL  DEFAULT 0     COMMENT '排序,只在本级排序有意义,从大到小排序，越大越靠前',
  createUserId  varchar(32)      NOT NULL                COMMENT '用户组创建者',
  adminUserIds  varchar(32)                              COMMENT '用户组管理者，非一人',
  descn         varchar(2000)              DEFAULT NULL  COMMENT '备注',
  cTime         timestamp        NOT NULL  DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间:创建时的系统时间',
  lmTime        timestamp        NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT '最后修改：每次更新的时间',
  PRIMARY KEY(id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户组表';
/** 目前和树型组相关的字段pId, sort没有用 */

/**004 PLAT_GROUPUSER（用户组成员）*/
DROP TABLE IF EXISTS plat_GroupUser;
CREATE TABLE plat_GroupUser (
  id       varchar(32)  NOT NULL  COMMENT 'uuid（主键）',
  groupId  varchar(32)  NOT NULL  COMMENT 'uuid（用户组Id）',
  userId   varchar(32)  NOT NULL  COMMENT 'uuid（用户Id）',
  inviter  varchar(32)  NOT NULL  COMMENT 'uuid（邀请者Id）',
  cTime    timestamp    NOT NULL  DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间:创建时的系统时间',
  PRIMARY KEY(id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户组成员表';
/** 目前和树型组相关的字段pId, sort没有用 */