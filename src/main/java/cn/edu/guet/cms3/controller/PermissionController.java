package cn.edu.guet.cms3.controller;

import cn.edu.guet.cms3.dto.PermissionDTO;
import cn.edu.guet.cms3.entity.Permission;
import cn.edu.guet.cms3.service.PermissionService;
import cn.edu.guet.cms3.util.PageRequest;
import cn.edu.guet.cms3.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @PostMapping("/page")
    public Result<IPage<Permission>> getPermissionPage(@RequestBody PageRequest pageRequest) {
        return Result.success(permissionService.getPermissionPage(pageRequest));
    }

    @GetMapping("/tree")
    public Result<List<Permission>> getPermissionTree() {
        return Result.success(permissionService.getPermissionTree());
    }

    @PostMapping
    public Result<Permission> createPermission(@RequestBody PermissionDTO permissionDTO) {
        return Result.success("权限已新增", permissionService.createPermission(permissionDTO));
    }

    @PutMapping("/{id}")
    public Result<Permission> updatePermission(@PathVariable Integer id, @RequestBody PermissionDTO permissionDTO) {
        return Result.success("权限已更新", permissionService.updatePermission(id, permissionDTO));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deletePermission(@PathVariable Integer id) {
        permissionService.deletePermission(id);
        return Result.success("权限已删除");
    }
}
