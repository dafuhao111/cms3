package cn.edu.guet.cms3.controller;

import cn.edu.guet.cms3.dto.LoginDTO;
import cn.edu.guet.cms3.service.LoginService;
import cn.edu.guet.cms3.util.Result;
import cn.edu.guet.cms3.vo.UserLoginVO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class LoginController {

    @PostConstruct
    public void init() {
        log.info("这是postConstruct111");
        log.info("loginService {}", loginService);
    }

    @Resource(name="test")
    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

    private LoginService loginService;

    @PostMapping("/login")
    public Result<Object> login(@RequestBody LoginDTO loginDTO) {
        log.info("用户名：{}，密码： {}", loginDTO.getUsername(), loginDTO.getPassword());
        UserLoginVO loginVO = loginService.login(loginDTO);
        // 既然前端页面需要动态显示菜单，所以我们之前登录，没有返回任何数据，只返回了“登录成功”四个字！
        // loginVO中包含了我们前端需要的数据
        if (loginVO != null) {
            return Result.success("登录成功",loginVO);
        }
        return Result.fail(401, "登录失败");
    }
}
