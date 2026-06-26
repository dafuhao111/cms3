package cn.edu.guet.cms3.service;

import cn.edu.guet.cms3.dto.RoleAuthorizeDTO;
import cn.edu.guet.cms3.dto.RoleDTO;
import cn.edu.guet.cms3.entity.Permission;
import cn.edu.guet.cms3.entity.Role;
import cn.edu.guet.cms3.util.PageRequest;
import cn.edu.guet.cms3.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface RoleService {
    IPage<Role> getRoleInfoPage(PageRequest pageRequest);

    Role createRole(RoleDTO roleDTO);

    Role updateRole(Integer id, RoleDTO roleDTO);

    Result deleteRole(Integer id);

    List<Permission> getPermissionTree();

    List<Integer> getRolePermissionIds(Integer roleId);

    void authorizeRole(RoleAuthorizeDTO roleAuthorizeDTO);
}