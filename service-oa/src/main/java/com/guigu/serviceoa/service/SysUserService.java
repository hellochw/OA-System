package com.guigu.serviceoa.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.guigu.model.system.SysUser;

import java.util.Map;

public interface SysUserService extends IService<SysUser> {
    /**
     * 修改用户状态
     * @param id
     * @param status
     */
    void updateStatus(Long id, Integer status);


    /**
     * 根据用户名获取用户实体
     * @param username
     * @return
     */
    SysUser getByUserName(String username);

    /**
     *获取当前登入用户的信息
     */
    Map<String, Object> getCurrentUser();
}
