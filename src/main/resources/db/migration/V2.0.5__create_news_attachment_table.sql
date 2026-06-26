create table if not exists cms.news_attachment
(
    id          bigint auto_increment comment '附件ID'
        primary key,
    news_id     bigint                             not null comment '新闻ID',
    name        varchar(255)                       not null comment '附件原始文件名',
    url         varchar(500)                       not null comment '附件访问地址',
    file_size   bigint                             null comment '文件大小，单位字节',
    file_type   varchar(100)                       null comment '文件MIME类型',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    constraint fk_news_attachment_news
        foreign key (news_id) references cms.news (id)
            on update cascade on delete cascade
)
    comment '新闻附件表';

create index idx_news_attachment_news_id
    on cms.news_attachment (news_id);
