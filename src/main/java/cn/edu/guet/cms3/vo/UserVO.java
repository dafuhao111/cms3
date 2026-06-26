package cn.edu.guet.cms3.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class UserVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String username;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<Integer> roleIds = new ArrayList<>();
    private List<String> roleNames = new ArrayList<>();
}