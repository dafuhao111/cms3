INSERT INTO cms.permission (name, type, code, path, parent_id, sort)
VALUES
    ('新增角色', 'button', 'role:add', NULL, 5, 0),
    ('编辑角色', 'button', 'role:edit', NULL, 5, 0),
    ('删除角色', 'button', 'role:delete', NULL, 5, 0),
    ('分配权限', 'button', 'role:assign-permission', NULL, 5, 0);