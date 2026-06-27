package cn.edu.guet.cms3.dto;

import lombok.Data;

@Data
public class NewsAttachmentDTO {
    private String name;
    private String url;
    private Long size;
    private String type;
}