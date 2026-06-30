package cn.edu.guet.cms3.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("news")
public class News {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String title;
    private String category;
    private String supplier;
    private String reviewer;
    private String content;
    private String status;
    @TableField("create_time")
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime publishTime;
}