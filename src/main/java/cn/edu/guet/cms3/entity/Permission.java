package cn.edu.guet.cms3.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("permission")
public class Permission {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    @TableField("type")
    private MenuType menuType;
    private String code;
    private String path;
    @TableField("parent_id")
    private Integer parentId;
    @TableField(exist = false)
    private List<Permission> children;
    private Integer sort;
    @TableField("create_time")
    private LocalDateTime createTime;
}