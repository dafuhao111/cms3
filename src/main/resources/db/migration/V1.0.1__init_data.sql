
-- 顶级目录：工作台
INSERT INTO cms.permission (name, type, code, path, parent_id, sort)
VALUES ('工作台', 'directory', 'dashboard:view', '/dashboard', 0, 1);

-- 顶级目录：账号管理
INSERT INTO cms.permission (name, type, code, path, parent_id, sort)
VALUES ('账号管理', 'directory', 'account:manage', '/account', 0, 2);

-- 菜单：用户信息（隶属于账号管理）
INSERT INTO cms.permission (name, type, code, path, parent_id, sort)
VALUES ('用户信息', 'menu', 'account:user:list', '/account/user',
        (SELECT id FROM (SELECT id FROM cms.permission WHERE name = '账号管理' AND type = 'directory') AS tmp), 1);

-- 顶级目录：角色管理
INSERT INTO cms.permission (name, type, code, path, parent_id, sort)
VALUES ('角色管理', 'directory', 'role:manage', '/role', 0, 3);

-- 菜单：角色信息（隶属于角色管理）
INSERT INTO cms.permission (name, type, code, path, parent_id, sort)
VALUES ('角色信息', 'menu', 'role:list', '/role/list',
        (SELECT id FROM (SELECT id FROM cms.permission WHERE name = '角色管理' AND type = 'directory') AS tmp), 1);

-- 顶级目录：权限管理
INSERT INTO cms.permission (name, type, code, path, parent_id, sort)
VALUES ('权限管理', 'directory', 'permission:manage', '/permission', 0, 4);

-- 菜单：权限信息（隶属于权限管理）
INSERT INTO cms.permission (name, type, code, path, parent_id, sort)
VALUES ('权限信息', 'menu', 'permission:list', '/permission/list',
        (SELECT id FROM (SELECT id FROM cms.permission WHERE name = '权限管理' AND type = 'directory') AS tmp), 1);