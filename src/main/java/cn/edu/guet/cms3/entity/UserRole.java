package cn.edu.guet.cms3.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_role")
public class UserRole {
    private Long userId;
    private Integer roleId;
}