package cn.edu.guet.cms3.service.impl;

import cn.edu.guet.cms3.dto.LoginDTO;
import cn.edu.guet.cms3.entity.MenuType;
import cn.edu.guet.cms3.entity.Permission;
import cn.edu.guet.cms3.mapper.LoginMapper;
import cn.edu.guet.cms3.service.LoginService;
import cn.edu.guet.cms3.vo.UserLoginVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service("test")
public class LoginServiceImpl implements LoginService {

    @Autowired
    private LoginMapper loginMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    // token的生存周期（有效期：168小时（24*7））
    @Value("${token.ttl.hours}")
    private Long tokenTtlHours;

    @Override
    public UserLoginVO login(LoginDTO loginDTO) {
        // 1. 基础登录验证
        UserLoginVO loginVO = loginMapper.login(loginDTO);

        if (loginVO == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        // 2. 优化：只在这里构建一次菜单树
        if (loginVO.getPermissionList() != null) {
            List<Permission> menuTree = buildMenuTree(loginVO.getPermissionList());
            loginVO.setMenuTree(menuTree);
        }
        log.info("loginVO的信息：{}", loginVO);
        issueToken(loginVO);
        return loginVO;
    }
    /**
     * 颁发 Token 并处理 Redis 存储（方案2：单点登录）
     */
    private void issueToken(UserLoginVO loginVO) {
        Integer userId = loginVO.getId();
        String userUidKey = "login:uid:" + userId;

        // === 1. 单点登录逻辑：检查并踢出旧 Token ===
        String oldToken = stringRedisTemplate.opsForValue().get(userUidKey);
        if (oldToken != null) {
            log.info("用户 {} 在其他地方登录，作废旧 Token: {}", userId, oldToken);
            stringRedisTemplate.delete("login:token:" + oldToken);
            // 同时删除关联的权限缓存
            stringRedisTemplate.delete("login:permissions:" + oldToken);
        }

        // === 2. 生成新 Token ===
        String newToken = UUID.randomUUID().toString().replace("-", "");
        loginVO.setToken(newToken);
        loginVO.setTokenExpireAt(LocalDateTime.now().plusHours(tokenTtlHours));

        // === 3. 存储到 Redis ===
        String tokenKey = "login:token:" + newToken;
        try {
            // 存储 VO 详情
            String jsonVO = objectMapper.writeValueAsString(loginVO);
            stringRedisTemplate.opsForValue().set(tokenKey, jsonVO, tokenTtlHours, TimeUnit.HOURS);

            // 存储 UID -> Token 映射，用于下次登录时踢人
            stringRedisTemplate.opsForValue().set(userUidKey, newToken, tokenTtlHours, TimeUnit.HOURS);

            // 存储权限码映射（优化：Key 使用 Token 绑定，确保随 Token 一起失效）
            if (loginVO.getPermissionList() != null) {
                String permKey = "login:permissions:" + newToken;
                String perms = loginVO.getPermissionList().stream()
                        .map(Permission::getCode)
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining(","));
                stringRedisTemplate.opsForValue().set(permKey, perms, tokenTtlHours, TimeUnit.HOURS);
            }

        } catch (JsonProcessingException e) {
            log.error("LoginVO 序列化失败", e);
            throw new RuntimeException("登录系统异常");
        }
    }

    /**
     * 构建菜单树（过滤按钮，进行排序）
     */
    private List<Permission> buildMenuTree(List<Permission> permissions) {
        // 1. 过滤掉“按钮”类型的权限
        List<Permission> menus = permissions.stream()
                .filter(p -> p.getMenuType() != null && !p.getMenuType().equals(MenuType.BUTTON))
                .collect(Collectors.toList());

        List<Permission> tree = new ArrayList<>();
        for (Permission menu : menus) {
            // 2. 找到顶级节点
            if (menu.getParentId() == 0) {
                tree.add(findChildren(menu, menus));
            }
        }
        // 3. 顶级节点排序
        tree.sort(Comparator.comparing(Permission::getSort, Comparator.nullsLast(Integer::compareTo)));
        return tree;
    }

    /**
     * 递归寻找子节点
     */
    private Permission findChildren(Permission parent, List<Permission> allMenus) {
        parent.setChildren(new ArrayList<>());
        for (Permission it : allMenus) {
            if (parent.getId().equals(it.getParentId())) {
                parent.getChildren().add(findChildren(it, allMenus));
            }
        }
        // 子节点排序
        parent.getChildren().sort(Comparator.comparing(Permission::getSort, Comparator.nullsLast(Integer::compareTo)));
        return parent;
    }
}
