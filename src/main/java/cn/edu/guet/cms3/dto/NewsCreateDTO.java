package cn.edu.guet.cms3.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NewsCreateDTO {
    private String title;
    private String category;
    private String supplier;
    private String reviewer;
    private String content;
    private List<NewsAttachmentDTO> attachments = new ArrayList<>();
}