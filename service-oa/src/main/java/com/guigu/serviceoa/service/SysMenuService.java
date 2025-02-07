package com.guigu.serviceoa.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guigu.model.system.SysMenu;
import com.guigu.vo.system.AssginMenuVo;
import com.guigu.vo.system.RouterVo;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2024-11-23
 */
public interface SysMenuService extends IService<SysMenu> {


    /**
     * 获取菜单树形结构
     * @return
     */
    List<SysMenu> findNodes(List<SysMenu> sysMenuList);


    /**
     * 获取指定角色的菜单
     * @param roleId
     * @return
     */
    List<SysMenu> findSysMenuByRoleId(Long roleId);


    /**
     * 为指定角色设置菜单
     * @param assignMenuVo
     */
    void doAssign(AssginMenuVo assignMenuVo);


    /**
     * 获取指定用户菜单路由
     * @param userId
     * @return
     */
    List<RouterVo> getRoutesByUserId(Long userId);


    /**
     * 获取用户可用的按钮
     * @param userId
     * @return
     */
    List<String> getPermsBysByUserId(Long userId);
}
