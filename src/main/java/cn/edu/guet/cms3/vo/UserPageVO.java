package cn.edu.guet.cms3.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户列表分页行（含角色名，供前端表格展示）
 */
@Data
public class UserPageVO {
    private Long id;
    private String username;
    /** 多个角色用中文顿号拼接 */
    private String roleName;
    private LocalDateTime createTime;
}
