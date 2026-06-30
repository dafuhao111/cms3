package cn.edu.guet.cms3.controller;

import cn.edu.guet.cms3.annotation.RequiresPermission;
import cn.edu.guet.cms3.dto.RoleAuthorizeDTO;
import cn.edu.guet.cms3.dto.RoleDTO;
import cn.edu.guet.cms3.entity.Permission;
import cn.edu.guet.cms3.entity.Role;
import cn.edu.guet.cms3.service.RoleService;
import cn.edu.guet.cms3.util.PageRequest;
import cn.edu.guet.cms3.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/getRoleInfoPage")
    public Result<IPage<Role>> getRoleInfoPage(@RequestBody PageRequest pageRequest) {
        return Result.success(roleService.getRoleInfoPage(pageRequest));
    }
    @PostMapping
    public Result<Role> createRole(@RequestBody RoleDTO roleDTO) {
        return Result.success("角色已新增", roleService.createRole(roleDTO));
    }

    @PutMapping("/{id}")
    public Result<Role> updateRole(@PathVariable Integer id, @RequestBody RoleDTO roleDTO) {
        return Result.success("角色已更新fg", roleService.updateRole(id, roleDTO));
    }

    @RequiresPermission("role:delete")
    @DeleteMapping("/{id}")
    public Result<Void> deleteRole(@PathVariable Integer id) {
        roleService.deleteRole(id);
        return Result.success("角色已删除");
    }

    @GetMapping("/permissionTree")
    public Result<List<Permission>> getPermissionTree() {
        return Result.success(roleService.getPermissionTree());
    }

    @GetMapping("/{roleId}")
    public Result<List<Integer>> getRolePermissionIds(@PathVariable Integer roleId) {
        return Result.success(roleService.getRolePermissionIds(roleId));
    }

    @PostMapping("/authorize")
    public Result<String> authorizeRole(@RequestBody RoleAuthorizeDTO roleAuthorizeDTO) {
        roleService.authorizeRole(roleAuthorizeDTO);
        return Result.success("授权保存成功");
    }
}