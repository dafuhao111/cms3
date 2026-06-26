package cn.edu.guet.cms3.service;


import cn.edu.guet.cms3.dto.LoginDTO;
import cn.edu.guet.cms3.vo.UserLoginVO;

public interface LoginService {
    UserLoginVO login(LoginDTO loginDTO);
}
