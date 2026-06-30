package cn.edu.guet.cms3.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_role")
public class UserRole {
    @TableId
    private Long id; // 新增主键
    private Long userId;
    private Integer roleId;
}