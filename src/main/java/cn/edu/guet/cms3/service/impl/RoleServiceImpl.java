package cn.edu.guet.cms3.service.impl;

import cn.edu.guet.cms3.dto.RoleAuthorizeDTO;
import cn.edu.guet.cms3.dto.RoleDTO;
import cn.edu.guet.cms3.entity.Permission;
import cn.edu.guet.cms3.entity.Role;
import cn.edu.guet.cms3.mapper.RoleMapper;
import cn.edu.guet.cms3.service.RoleService;
import cn.edu.guet.cms3.util.PageRequest;
import cn.edu.guet.cms3.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public IPage<Role> getRoleInfoPage(PageRequest pageRequest) {
        Page<Role> page = new Page<>(pageRequest.getCurrentPage(), pageRequest.getPageSize());
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();

        String roleName = pageRequest.getParamValue("roleName");
        if (StringUtils.hasText(roleName)) {
            queryWrapper.like("name", roleName);
        }
        queryWrapper.orderByAsc("id");

        return roleMapper.selectPage(page, queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Role createRole(RoleDTO roleDTO) {
        validateRole(roleDTO);
        ensureRoleNameAvailable(roleDTO.getName(), null);

        Role role = new Role();
        role.setName(roleDTO.getName());
        roleMapper.insert(role);
        return roleMapper.selectById(role.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Role updateRole(Integer id, RoleDTO roleDTO) {
        if (id == null) {
            throw new IllegalArgumentException("角色ID不能为空");
        }
        validateRole(roleDTO);

        Role existingRole = roleMapper.selectById(id);
        if (existingRole == null) {
            throw new IllegalArgumentException("角色不存在");
        }
        ensureRoleNameAvailable(roleDTO.getName(), id);

        existingRole.setName(roleDTO.getName());
        roleMapper.updateById(existingRole);
        return roleMapper.selectById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result deleteRole(Integer id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new IllegalArgumentException("角色不存在");
        }
        roleMapper.deleteRolePermissions(id);
        roleMapper.deleteById(id);
        return null;
    }

    @Override
    public List<Permission> getPermissionTree() {
        List<Permission> permissions = roleMapper.selectPermissionList();
        return buildPermissionTree(permissions);
    }

    @Override
    public List<Integer> getRolePermissionIds(Integer roleId) {
        if (roleId == null) {
            throw new IllegalArgumentException("角色ID不能为空");
        }
        return roleMapper.selectPermissionIdsByRoleId(roleId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void authorizeRole(RoleAuthorizeDTO roleAuthorizeDTO) {
        if (roleAuthorizeDTO == null || roleAuthorizeDTO.getRoleId() == null) {
            throw new IllegalArgumentException("roleId不能为空");
        }

        Integer roleId = roleAuthorizeDTO.getRoleId();
        if (roleMapper.selectById(roleId) == null) {
            throw new IllegalArgumentException("角色不存在");
        }

        roleMapper.deleteRolePermissions(roleId);

        List<Integer> permissionIds = roleAuthorizeDTO.getPermissionIds();
        if (permissionIds == null || permissionIds.isEmpty()) {
            return;
        }

        List<Integer> distinctPermissionIds = permissionIds.stream().distinct().toList();
        roleMapper.insertRolePermissions(roleId, distinctPermissionIds);
    }

    private void validateRole(RoleDTO roleDTO) {
        if (roleDTO == null || !StringUtils.hasText(roleDTO.getName())) {
            throw new IllegalArgumentException("角色名称不能为空");
        }
    }

    private void ensureRoleNameAvailable(String name, Integer currentRoleId) {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getName, name);
        Role role = roleMapper.selectOne(queryWrapper);
        if (role != null && (currentRoleId == null || !role.getId().equals(currentRoleId))) {
            throw new IllegalArgumentException("角色名称已存在");
        }
    }

    private List<Permission> buildPermissionTree(List<Permission> permissions) {
        List<Permission> tree = new ArrayList<>();
        for (Permission permission : permissions) {
            if (permission.getParentId() == null || permission.getParentId() == 0) {
                tree.add(findChildren(permission, permissions));
            }
        }
        tree.sort(Comparator.comparing(Permission::getSort, Comparator.nullsLast(Integer::compareTo)));
        return tree;
    }

    private Permission findChildren(Permission parent, List<Permission> permissions) {
        parent.setChildren(permissions.stream()
                .filter(permission -> parent.getId().equals(permission.getParentId()))
                .map(permission -> findChildren(permission, permissions))
                .sorted(Comparator.comparing(Permission::getSort, Comparator.nullsLast(Integer::compareTo)))
                .collect(Collectors.toList()));
        return parent;
    }
}