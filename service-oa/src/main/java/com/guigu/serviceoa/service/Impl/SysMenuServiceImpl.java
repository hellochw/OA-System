package com.guigu.serviceoa.service.Impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.guigu.model.system.SysMenu;
import com.guigu.model.system.SysRoleMenu;
import com.guigu.serviceoa.mapper.SysMenuMapper;
import com.guigu.serviceoa.mapper.SysRoleMenuMapper;
import com.guigu.serviceoa.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guigu.vo.system.AssginMenuVo;
import com.guigu.vo.system.MetaVo;
import com.guigu.vo.system.RouterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2024-11-23
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    /**
     * 将菜单转换成树形结构
     * @return
     */
    public List<SysMenu> findNodes(List<SysMenu> sysMenuList) {
        List<SysMenu> treeMenu = new ArrayList<SysMenu>();

        for (SysMenu menu : sysMenuList) {
            if (menu.getParentId() == 0) {
                treeMenu.add(findChildren(menu,sysMenuList));
            }
        }
        return treeMenu;
    }

    /**
     * 获取指定角色的菜单
     * @param roleId
     * @return
     */
    public List<SysMenu> findSysMenuByRoleId(Long roleId) {
        List<SysMenu> list = this.list();

        //组建条件，并查询此角色的菜单
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> sysRoleMenuList = sysRoleMenuMapper.selectList(queryWrapper);

        //过滤，只要对菜单id  并包装在集合中
        List<Long> collect = sysRoleMenuList.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());

        System.out.println(collect);

        //遍历所有菜单  只要在collect中的就标记选中
        list.forEach(sysMenu -> {
          if (collect.contains(sysMenu.getId())) {
              sysMenu.setSelect(true);
          }
          else sysMenu.setSelect(false);
        });

        //将其改造成树形结构
        List<SysMenu> nodes = this.findNodes(list);
        return nodes;

    }


    /**
     * 为指定角色设置菜单
     * @param assignMenuVo
     */
    public void doAssign(AssginMenuVo assignMenuVo) {
        //删除此角色的相关所有菜单
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SysRoleMenu::getRoleId, assignMenuVo.getRoleId());
        sysRoleMenuMapper.delete(queryWrapper);

        //重新添加 基于此角色id
        for (Long menuId : assignMenuVo.getMenuIdList()) {
            if (StringUtils.isEmpty(menuId))
                continue;
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(assignMenuVo.getRoleId());
            roleMenu.setMenuId(menuId);
            sysRoleMenuMapper.insert(roleMenu);
        }
    }

    //获取用户的菜单路由
    public List<RouterVo> getRoutesByUserId(Long userId) {
       List<SysMenu> menus=null;
        if (userId==1){
            //约定 如果id为1 则是管理员,
            LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.eq(SysMenu::getStatus, 1).orderByAsc(SysMenu::getSortValue);
             menus = this.list(queryWrapper);
        }
        else {
            //否则是普通用户
            menus=baseMapper.selectMenusByUserId(userId);
        }

        //将菜单列表转换成树形结构
        List<SysMenu> treeMenus = findNodes(menus);

        //将菜单列表中的每一项菜单转换成路由形式
        List<RouterVo> routers=BuildRouter(treeMenus);

        return routers;

    }

    //将菜单列表中的每一项菜单转换成路由形式
    private List<RouterVo> BuildRouter(List<SysMenu> treeMenus) {
        List<RouterVo> routers = new LinkedList<RouterVo>();
        for (SysMenu menu : treeMenus) {
            RouterVo router = new RouterVo();
            router.setHidden(false);
            router.setAlwaysShow(false);
            router.setPath(getRouterPath(menu));
            router.setComponent(menu.getComponent());
            router.setMeta(new MetaVo(menu.getName(), menu.getIcon()));
            List<SysMenu> children = menu.getChildren();
            //如果当前是菜单，需将按钮对应的路由加载出来，如：“角色授权”按钮对应的路由在“系统管理”下面
            if(menu.getType().intValue() == 1) {
                List<SysMenu> hiddenMenuList = children.stream().filter(item -> !StringUtils.isEmpty(item.getComponent())).collect(Collectors.toList());
                for (SysMenu hiddenMenu : hiddenMenuList) {
                    RouterVo hiddenRouter = new RouterVo();
                    hiddenRouter.setHidden(true);
                    hiddenRouter.setAlwaysShow(false);
                    hiddenRouter.setPath(getRouterPath(hiddenMenu));
                    hiddenRouter.setComponent(hiddenMenu.getComponent());
                    hiddenRouter.setMeta(new MetaVo (hiddenMenu.getName(), hiddenMenu.getIcon()));
                    routers.add(hiddenRouter);
                }
            } else {
                if (!CollectionUtils.isEmpty(children)) {
                    if(children.size() > 0) {
                        router.setAlwaysShow(true);
                    }
                    router.setChildren(BuildRouter( children));
                }
            }
            routers.add(router);
        }
        return routers;
    }


    //获取用户可用的按钮
    @Override
    public List<String> getPermsBysByUserId(Long userId) {
        List<SysMenu> list=null;
        //判断id是否为管理员
        if (userId==1){
            LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.eq(SysMenu::getStatus, 1);
            list = this.list(queryWrapper);
        }
        else {
            list=baseMapper.selectMenusByUserId(userId);
        }

        List<String> permsList = list.stream().filter(sysMenu -> sysMenu.getType() == 2).
                map(sysMenu -> sysMenu.getPerms()).collect(Collectors.toList());

        return permsList;
    }


    //获取父节点的所有子节点，以及子节点的子节点
    private SysMenu findChildren(SysMenu menu, List<SysMenu> childrenNodes) {
        menu.setChildren(new ArrayList<SysMenu>());
        for (SysMenu childrenNode : childrenNodes) {
            if (childrenNode.getParentId() == menu.getId()) {
                if(childrenNode.getChildren()==null){
                    childrenNode.setChildren(new ArrayList<SysMenu>());
                }
                menu.getChildren().add(findChildren(childrenNode,childrenNodes));
            }

        }
        return menu;
    }


    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenu menu) {
        String routerPath = "/" + menu.getPath();
        if(menu.getParentId().intValue() != 0) {
            routerPath = menu.getPath();
        }
        return routerPath;
    }


}
