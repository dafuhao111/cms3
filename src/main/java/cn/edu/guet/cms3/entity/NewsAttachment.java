package cn.edu.guet.cms3.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("news_attachment")
public class NewsAttachment {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long newsId;
    private String name;
    private String url;
    private Long fileSize;
    private String fileType;
    private LocalDateTime createTime;
}