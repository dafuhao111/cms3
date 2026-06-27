package cn.edu.guet.cms3.util;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页请求
 *
 * @Author Liwei
 * @Date 2021-08-10 09:32
 */
@Data
public class PageRequest {
    /**
     * 当前页码
     */
    private int currentPage = 1;
    /**
     * 每页数量
     */
    private int pageSize = 10;
    /**
     * 查询参数
     */
    private List<Param> params = new ArrayList<>();

    /**
     * 查询参数对象
     *
     * @param name 参数名称
     * @return
     */
    public Param getParam(String name) {
        for (Param param : this.params) {
            if (name != null && name.equals(param.getName())) {
                return param;
            }
        }
        return null;
    }

    /**
     * 查询参数值
     *
     * @param name 参数名称
     * @return
     */
    public String getParamValue(String name) {
        Param param = getParam(name);
        if (param != null) {
            return param.getValue();
        }
        return null;
    }
}

