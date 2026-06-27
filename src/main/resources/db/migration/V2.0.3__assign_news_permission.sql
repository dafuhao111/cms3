insert ignore into cms.role_permission (role_id, permission_id)
select r.id, p.id
from cms.role r
join cms.permission p
where r.name in ('超级管理员', '管理员')
  and p.code in ('content:manage', 'content:news:list');
