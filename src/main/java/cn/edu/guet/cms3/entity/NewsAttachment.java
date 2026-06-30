package cn.edu.guet.cms3.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("news_attachment")
public class NewsAttachment {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("news_id")
    private Long newsId;
    private String name;
    private String url;
    @TableField(" file_size")
    private Long fileSize;
    @TableField("file_type")
    private String fileType;
    @TableField("create_time")
    private LocalDateTime createTime;
}