create table cms.permission
(
    id          int auto_increment comment '权限ID'
        primary key,
    name        varchar(50)                          not null comment '权限名称，如：用户管理、新增用户',
    type        enum ('DIRECTORY', 'MENU', 'BUTTON') not null comment '权限类型：directory-目录, menu-菜单, button-按钮',
    code        varchar(100)                         null comment '权限标识符，如：sys:user:add',
    path        varchar(255)                         null comment '路由路径或接口路径',
    parent_id   int      default 0                   null comment '父权限ID，0表示顶级',
    sort        int      default 0                   null comment '排序值，数值越小越靠前',
    create_time datetime default CURRENT_TIMESTAMP   not null comment '创建时间',
    constraint uk_permission_code
        unique (code)
)
    comment '权限表（包含菜单和按钮）';

create table cms.role
(
    id   int auto_increment comment '角色ID'
        primary key,
    name varchar(50) not null comment '角色名称，如：管理员、职员、普通用户',
    constraint uk_role_name
        unique (name)
)
    comment '角色表';

create table cms.role_permission
(
    role_id       int not null comment '角色ID',
    permission_id int not null comment '权限ID',
    primary key (role_id, permission_id),
    constraint fk_rp_permission
        foreign key (permission_id) references cms.permission (id)
            on update cascade on delete cascade,
    constraint fk_rp_role
        foreign key (role_id) references cms.role (id)
            on update cascade on delete cascade
)
    comment '角色权限关联表（多对多）';

create table cms.user
(
    id          bigint auto_increment comment '用户ID'
        primary key,
    username    varchar(100)                           not null comment '用户名',
    password    varchar(100) default '123456'          not null comment '密码（实际应加密）',
    create_time datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '最后更新时间',
    constraint username
        unique (username)
)
    comment '用户表';

create table cms.user_role
(
    user_id bigint not null comment '用户ID',
    role_id int    not null comment '角色ID',
    primary key (user_id, role_id),
    constraint user_role_ibfk_1
        foreign key (user_id) references cms.user (id)
            on update cascade on delete cascade,
    constraint user_role_ibfk_2
        foreign key (role_id) references cms.role (id)
            on update cascade on delete cascade
)
    comment '用户角色关联表（多对多）';

create index role_id
    on cms.user_role (role_id);

