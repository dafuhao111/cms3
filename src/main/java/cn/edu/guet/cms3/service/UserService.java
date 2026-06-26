package cn.edu.guet.cms3.service;

import cn.edu.guet.cms3.dto.UserDTO;
import cn.edu.guet.cms3.util.PageRequest;
import cn.edu.guet.cms3.vo.UserVO;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface UserService {
    IPage<UserVO> getUserPage(PageRequest pageRequest);

    UserVO createUser(UserDTO userDTO);

    UserVO updateUser(Long id, UserDTO userDTO);

    void deleteUser(Long id);
}