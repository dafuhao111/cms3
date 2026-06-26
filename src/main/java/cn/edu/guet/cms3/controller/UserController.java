package cn.edu.guet.cms3.controller;

import cn.edu.guet.cms3.dto.UserDTO;
import cn.edu.guet.cms3.service.UserService;
import cn.edu.guet.cms3.util.PageRequest;
import cn.edu.guet.cms3.util.Result;
import cn.edu.guet.cms3.vo.UserVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/page")
    public Result<IPage<UserVO>> getUserPage(@RequestBody PageRequest pageRequest) {
        return Result.success(userService.getUserPage(pageRequest));
    }

    @PostMapping
    public Result<UserVO> createUser(@RequestBody UserDTO userDTO) {
        return Result.success("用户已新增", userService.createUser(userDTO));
    }

    @PutMapping("/{id}")
    public Result<UserVO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return Result.success("用户已更新", userService.updateUser(id, userDTO));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success("用户已删除");
    }
}
