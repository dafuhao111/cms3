package cn.edu.guet.cms3.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserDTO {
    private String username;
    private String password;
    private List<Integer> roleIds = new ArrayList<>();
}