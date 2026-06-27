package cn.edu.guet.cms3.mapper;

import cn.edu.guet.cms3.dto.LoginDTO;
import cn.edu.guet.cms3.vo.UserLoginVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginMapper {
    UserLoginVO login(LoginDTO loginDTO);
}
