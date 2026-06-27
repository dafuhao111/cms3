package cn.edu.guet.cms3.service;

import cn.edu.guet.cms3.dto.PermissionDTO;
import cn.edu.guet.cms3.entity.Permission;
import cn.edu.guet.cms3.util.PageRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface PermissionService {
    IPage<Permission> getPermissionPage(PageRequest pageRequest);

    List<Permission> getPermissionTree();

    Permission createPermission(PermissionDTO permissionDTO);

    Permission updatePermission(Integer id, PermissionDTO permissionDTO);

    void deletePermission(Integer id);
}