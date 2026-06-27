package cn.edu.guet.cms3.interceptor;

import cn.edu.guet.cms3.context.UserContext;
import cn.edu.guet.cms3.vo.UserLoginVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper; // 注入 Jackson 对象映射器

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        log.info("----------------拦截器------------------");
        // 1. 获取 Header 中的原始 Token 字符串

        String authHeader = request.getHeader("Authorization");
        String agent = request.getHeader("User-Agent");
        log.info("客户使用的浏览器是：{}",agent);

        // 2. 校验格式：必须不为空且以 "Bearer " 开头
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("未检测到 Bearer Token，请求路径: {}", request.getRequestURI());
            throw new IllegalAccessException("请先登录");
        }

        // 3. 剥离前缀，获取真实的 Token (UUID 部分)
        // "Bearer " 长度为 7
        String token = authHeader.substring(7);

        // 4. 从 Redis 查询数据
        String redisKey = "login:token:" + token;
        String jsonUser = stringRedisTemplate.opsForValue().get(redisKey);

        if (jsonUser == null || jsonUser.isEmpty()) {
            throw new RuntimeException("登录已过期或凭证无效");
        }

        // 5. 反序列化为对象并存入上下文
        UserLoginVO loginUser = objectMapper.readValue(jsonUser, UserLoginVO.class);
        log.info("用户信息：{}", loginUser);
        UserContext.set(loginUser);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        // 请求结束后务必清理，防止内存泄漏
        UserContext.clear();
    }
}
