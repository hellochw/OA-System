package com.guigu.serviceoa.service;

import com.guigu.model.wechat.Menu;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guigu.vo.wechat.MenuVo;

import java.util.List;

/**
 * <p>
 * 菜单 服务类
 * </p>
 *
 * @author atguigu
 * @since 2024-12-20
 */
public interface WechatMenuService extends IService<Menu> {

    /**
     * 查询微信菜单的树形结构
     * @return
     */
    List<MenuVo> findMenuList();


    /**
     * 推送菜单
     */
    void createMenu();

    /**
     * 删除菜单
     */
    void removeMenu();
}
