package com.guigu.serviceoa.service.Impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guigu.model.system.SysUser;
import com.guigu.serviceoa.mapper.SysUserMapper;
import com.guigu.serviceoa.service.SysUserService;
import com.guigu.springsecurity.custom.LoginUserInfoHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        SysUser user = this.getById(id);
        user.setStatus(status);
        this.updateById(user);
    }

    /**
     * 根据用户名获取用户实体
     * @param username
     * @return
     */
    public SysUser getByUserName(String username) {
        return this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
    }


    /**
     *获取当前登入用户的信息
     */
    public Map<String, Object> getCurrentUser() {
        SysUser sysUser = this.getById(LoginUserInfoHelper.getUserId());
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", sysUser.getName());
        map.put("phone", sysUser.getPhone());
        return map;
    }
}
