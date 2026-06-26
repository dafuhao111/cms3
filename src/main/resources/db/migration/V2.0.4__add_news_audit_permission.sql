insert ignore into cms.permission (name, type, code, path, parent_id, sort, create_time)
select '审核新闻', 'button', 'content:news:audit', null, p.id, 0, now()
from cms.permission p
where p.code = 'content:news:list';

insert ignore into cms.role_permission (role_id, permission_id)
select r.id, p.id
from cms.role r
join cms.permission p
where r.name in ('超级管理员', '管理员')
  and p.code = 'content:news:audit';
