package com.guigu.serviceoa.service.Impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.guigu.model.wechat.Menu;
import com.guigu.serviceoa.mapper.WechatMenuMapper;
import com.guigu.serviceoa.service.WechatMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guigu.vo.wechat.MenuVo;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2024-12-20
 */
@Service
public class WechatMenuServiceImpl extends ServiceImpl<WechatMenuMapper, Menu> implements WechatMenuService {

    @Autowired
    private WxMpService wxMpService;

    /**
     * 查询微信菜单的树形结构
     * @return
     */
    public List<MenuVo> findMenuList() {
        List<MenuVo> menuVoList=new ArrayList<>();
        //获取所有menu
        List<Menu> list = this.list();
        //收集所有根菜单
        List<Menu> rootMenu = list.stream().filter(menu -> menu.getParentId() == 0).collect(Collectors.toList());

        //遍历所有根菜单 并添加子菜单
        for (Menu menu : rootMenu) {
            MenuVo menuVo=new MenuVo();
            BeanUtils.copyProperties(menu,menuVo);
            for (Menu childMenu : list) {
                if(childMenu.getParentId()==menuVo.getId()){
                    if(menuVo.getChildren()==null){
                        menuVo.setChildren(new ArrayList<>());
                    }
                    MenuVo childMenuVo=new MenuVo();
                    BeanUtils.copyProperties(childMenu,childMenuVo);
                    menuVo.getChildren().add(childMenuVo);
                }
            }
            menuVoList.add(menuVo);
        }
        return menuVoList;
    }

    /**
     * 推送菜单
     */
    public void createMenu() {
        List<MenuVo> menuVoList = this.findMenuList();
        //菜单
        JSONArray buttonList = new JSONArray();
        for(MenuVo oneMenuVo : menuVoList) {
            JSONObject one = new JSONObject();
            one.put("name", oneMenuVo.getName());
            if(CollectionUtils.isEmpty(oneMenuVo.getChildren())) {
                one.put("type", oneMenuVo.getType());
                one.put("url", "http://192.168.31.122:9090/#"+oneMenuVo.getUrl());
            } else {
                JSONArray subButton = new JSONArray();
                for(MenuVo twoMenuVo : oneMenuVo.getChildren()) {
                    JSONObject view = new JSONObject();
                    view.put("type", twoMenuVo.getType());
                    if(twoMenuVo.getType().equals("view")) {
                        view.put("name", twoMenuVo.getName());
                        //H5页面地址
                        view.put("url", "http://192.168.31.122:9090#"+twoMenuVo.getUrl());
                    } else {
                        view.put("name", twoMenuVo.getName());
                        view.put("key", twoMenuVo.getMeunKey());
                    }
                    subButton.add(view);
                }
                one.put("sub_button", subButton);
            }
            buttonList.add(one);
        }

        //菜单
        JSONObject button = new JSONObject();
        button.put("button", buttonList);
        try {
            wxMpService.getMenuService().menuCreate(button.toJSONString());
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除菜单
     */
    public void removeMenu() {
        try {
            wxMpService.getMenuService().menuDelete();
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }
}
