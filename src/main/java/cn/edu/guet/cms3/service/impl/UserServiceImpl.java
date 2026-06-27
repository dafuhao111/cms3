package cn.edu.guet.cms3.service.impl;

import cn.edu.guet.cms3.dto.UserDTO;
import cn.edu.guet.cms3.entity.Role;
import cn.edu.guet.cms3.entity.User;
import cn.edu.guet.cms3.entity.UserRole;
import cn.edu.guet.cms3.mapper.RoleMapper;
import cn.edu.guet.cms3.mapper.UserMapper;
import cn.edu.guet.cms3.mapper.UserRoleMapper;
import cn.edu.guet.cms3.service.UserService;
import cn.edu.guet.cms3.util.PageRequest;
import cn.edu.guet.cms3.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public IPage<UserVO> getUserPage(PageRequest pageRequest) {
        Page<User> page = new Page<>(pageRequest.getCurrentPage(), pageRequest.getPageSize());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        String username = pageRequest.getParamValue("username");
        if (StringUtils.hasText(username)) {
            queryWrapper.like("username", username);
        }
        queryWrapper.orderByDesc("update_time", "id");

        return userMapper.selectPage(page, queryWrapper).convert(this::toVO);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserVO createUser(UserDTO userDTO) {
        validateUser(userDTO, true);
        ensureUsernameAvailable(userDTO.getUsername(), null);

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(StringUtils.hasText(userDTO.getPassword()) ? userDTO.getPassword() : "123456");
        userMapper.insert(user);

        rebuildUserRoles(user.getId(), userDTO.getRoleIds());
        return toVO(userMapper.selectById(user.getId()));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserVO updateUser(Long id, UserDTO userDTO) {
        if (id == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        validateUser(userDTO, false);

        User existingUser = userMapper.selectById(id);
        if (existingUser == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        ensureUsernameAvailable(userDTO.getUsername(), id);

        existingUser.setUsername(userDTO.getUsername());
        if (StringUtils.hasText(userDTO.getPassword())) {
            existingUser.setPassword(userDTO.getPassword());
        }
        userMapper.updateById(existingUser);

        rebuildUserRoles(id, userDTO.getRoleIds());
        return toVO(userMapper.selectById(id));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        QueryWrapper<UserRole> roleWrapper = new QueryWrapper<>();
        roleWrapper.eq("user_id", id);
        userRoleMapper.delete(roleWrapper);
        userMapper.deleteById(id);
    }

    private void validateUser(UserDTO userDTO, boolean requirePassword) {
        if (userDTO == null || !StringUtils.hasText(userDTO.getUsername())) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (requirePassword && !StringUtils.hasText(userDTO.getPassword())) {
            throw new IllegalArgumentException("密码不能为空");
        }
    }

    private void ensureUsernameAvailable(String username, Long currentUserId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        if (user != null && (currentUserId == null || !user.getId().equals(currentUserId))) {
            throw new IllegalArgumentException("用户名已存在");
        }
    }

    private void rebuildUserRoles(Long userId, List<Integer> roleIds) {
        QueryWrapper<UserRole> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("user_id", userId);
        userRoleMapper.delete(deleteWrapper);

        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }

        roleIds.stream().distinct().forEach(roleId -> {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleMapper.insert(userRole);
        });
    }

    private UserVO toVO(User user) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        QueryWrapper<UserRole> userRoleWrapper = new QueryWrapper<>();
        userRoleWrapper.eq("user_id", user.getId());
        List<Integer> roleIds = userRoleMapper.selectList(userRoleWrapper)
                .stream()
                .map(UserRole::getRoleId)
                .toList();
        userVO.setRoleIds(roleIds);

        if (roleIds.isEmpty()) {
            userVO.setRoleNames(Collections.emptyList());
            return userVO;
        }

        Map<Integer, String> roleNameMap = roleMapper.selectBatchIds(roleIds)
                .stream()
                .collect(Collectors.toMap(Role::getId, Role::getName));
        userVO.setRoleNames(roleIds.stream()
                .map(roleNameMap::get)
                .filter(StringUtils::hasText)
                .toList());
        return userVO;
    }
}