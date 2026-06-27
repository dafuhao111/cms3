package cn.edu.guet.cms3.mapper;

import cn.edu.guet.cms3.entity.Permission;
import cn.edu.guet.cms3.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    List<Role> getRoleInfoPage();

    List<Permission> selectPermissionList();

    List<Integer> selectPermissionIdsByRoleId(Integer roleId);

    void deleteRolePermissions(Integer roleId);

    void insertRolePermissions(@Param("roleId") Integer roleId, @Param("permissionIds") List<Integer> permissionIds);
}