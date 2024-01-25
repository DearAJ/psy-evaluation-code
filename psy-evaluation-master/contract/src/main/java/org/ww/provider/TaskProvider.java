package org.ww.provider;

import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author 13096
 * @Date 2022/6/14 9:53
 * @Version 1.0
 */
public interface TaskProvider {
    /**
     * 查询是否有做过量表
     * @param userId userId
     * @return 返回是否做过 0 无  1 有  2 出错
     */
    Integer hasTask(@PathVariable("userId") Long userId);
}
