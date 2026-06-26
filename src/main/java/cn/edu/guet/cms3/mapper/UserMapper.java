package cn.edu.guet.cms3.mapper;

import cn.edu.guet.cms3.entity.User;
import cn.edu.guet.cms3.vo.UserPageVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    IPage<UserPageVO> selectUserPage(Page<UserPageVO> page, @Param("userName") String userName);
}
