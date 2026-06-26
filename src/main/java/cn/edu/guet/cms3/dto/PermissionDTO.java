package cn.edu.guet.cms3.dto;

import cn.edu.guet.cms3.entity.MenuType;
import lombok.Data;

@Data
public class PermissionDTO {
    private String name;
    private MenuType menuType;
    private String code;
    private String path;
    private Integer parentId;
    private Integer sort;
}