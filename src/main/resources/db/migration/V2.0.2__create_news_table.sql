create table if not exists cms.news
(
    id           bigint auto_increment comment '新闻ID'
        primary key,
    title        varchar(120)                         not null comment '新闻标题',
    category     varchar(50)                          not null comment '栏目',
    supplier     varchar(50)                          null comment '供稿人',
    reviewer     varchar(50)                          null comment '审稿人',
    content      longtext                             not null comment '新闻正文HTML',
    status       varchar(30) default 'PENDING_REVIEW' not null comment '状态',
    create_time  datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    publish_time datetime                             null comment '发布时间'
)
    comment '新闻表';
