package com.guigu.serviceoa.service.Impl;


import com.guigu.model.system.SysUser;
import com.guigu.serviceoa.service.SysMenuService;
import com.guigu.serviceoa.service.SysUserService;
import com.guigu.springsecurity.custom.CustomUser;
import com.guigu.springsecurity.custom.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

@Autowired
private SysUserService sysUserService;

@Autowired
private SysMenuService sysMenuService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserService.getByUserName(username);

        if(null == sysUser) {
            throw new UsernameNotFoundException("用户名不存在！");
        }

        if(sysUser.getStatus().intValue() == 0) {
            throw new RuntimeException("账号已停用");
        }

        //通过验证 获取用户可操作的按钮(也就是获取权限)
        List<String> perms = sysMenuService.getPermsBysByUserId(sysUser.getId());

        //创建Security的权限列表
        List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();

        //转换
        for (String perm : perms) {
            authorities.add(new SimpleGrantedAuthority(perm.trim()));
        }

        //返回用户对象
        return new CustomUser(sysUser, authorities);
    }
}
