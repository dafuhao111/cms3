package cn.edu.guet.cms3.context;

import cn.edu.guet.cms3.vo.UserLoginVO;

public class UserContext {
    private static final ThreadLocal<UserLoginVO> USER_HOLDER = new ThreadLocal<>();

    public static void set(UserLoginVO user) {
        USER_HOLDER.set(user);
    }

    public static UserLoginVO get() {
        return USER_HOLDER.get();
    }

    public static void clear() {
        USER_HOLDER.remove();
    }
}
