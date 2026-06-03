-- ============================================================
-- 系统管理模块建表脚本
-- 表名：system_role, system_menu, system_dept,
--       system_user_role, system_role_menu
-- 适用数据库：MySQL 8.0+
-- ============================================================

-- 角色表
CREATE TABLE IF NOT EXISTS `system_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(64) NOT NULL COMMENT '角色名称',
  `code` varchar(64) NOT NULL COMMENT '角色编码',
  `description` varchar(255) DEFAULT NULL COMMENT '角色描述',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态（1正常 0禁用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除（0未删除 1已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_code` (`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色信息表';

-- 菜单权限表
CREATE TABLE IF NOT EXISTS `system_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` bigint DEFAULT '0' COMMENT '父菜单id',
  `name` varchar(128) NOT NULL COMMENT '菜单名称',
  `type` tinyint NOT NULL DEFAULT '0' COMMENT '菜单类型（0目录 1菜单 2按钮）',
  `path` varchar(255) DEFAULT NULL COMMENT '路由路径',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `perms` varchar(255) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(128) DEFAULT NULL COMMENT '菜单图标',
  `sort_value` int DEFAULT '0' COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态（1正常 0禁用）',
  `is_hide` tinyint NOT NULL DEFAULT '0' COMMENT '是否隐藏（0否 1是）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除（0未删除 1已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_parent_id` (`parent_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单权限信息表';

-- 部门表
CREATE TABLE IF NOT EXISTS `system_dept` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(64) NOT NULL COMMENT '部门名称',
  `parent_id` bigint DEFAULT '0' COMMENT '父部门id',
  `tree_path` varchar(255) DEFAULT NULL COMMENT '树路径',
  `sort_value` int DEFAULT '0' COMMENT '排序',
  `leader` varchar(64) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态（1正常 0禁用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除（0未删除 1已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_parent_id` (`parent_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门信息表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `system_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `role_id` bigint NOT NULL COMMENT '角色id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除（0未删除 1已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_role_id` (`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关联表';

-- 角色菜单关联表
CREATE TABLE IF NOT EXISTS `system_role_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id` bigint NOT NULL COMMENT '角色id',
  `menu_id` bigint NOT NULL COMMENT '菜单id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除（0未删除 1已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_role_id` (`role_id`) USING BTREE,
  KEY `idx_menu_id` (`menu_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色菜单关联表';

-- ============================================================
-- 初始化数据
-- ============================================================

-- 管理员角色
INSERT IGNORE INTO `system_role` (`id`, `name`, `code`, `description`, `status`) VALUES
(1, '超级管理员', 'ROLE_ADMIN', '拥有系统全部权限', 1);

-- 系统管理菜单（根节点）
INSERT IGNORE INTO `system_menu` (`id`, `parent_id`, `name`, `type`, `path`, `component`, `perms`, `icon`, `sort_value`, `status`, `is_hide`) VALUES
(1, 0, '系统管理', 0, '/system', NULL, NULL, 'Setting', 100, 1, 0),
(2, 1, '用户管理', 1, '/system/user', 'system/user/user', 'system:user:list', 'User', 1, 1, 0),
(3, 1, '岗位管理', 1, '/system/post', 'system/post/post', 'system:post:list', 'Postcard', 2, 1, 0);

-- 超级管理员默认拥有系统管理菜单权限
INSERT IGNORE INTO `system_role_menu` (`role_id`, `menu_id`) VALUES
(1, 1),
(1, 2),
(1, 3);

-- 将 admin 用户关联到超级管理员角色（需 system_user 表中已存在 admin 用户）
INSERT IGNORE INTO `system_user_role` (`user_id`, `role_id`)
SELECT su.id, 1 FROM `system_user` su WHERE su.`username` = 'admin' AND NOT EXISTS (
  SELECT 1 FROM `system_user_role` sur WHERE sur.`user_id` = su.id AND sur.`role_id` = 1
);
