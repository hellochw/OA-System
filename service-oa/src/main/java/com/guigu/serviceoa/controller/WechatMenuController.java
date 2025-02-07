package com.guigu.serviceoa.controller;


import com.guigu.common.result.Result;
import com.guigu.model.wechat.Menu;
import com.guigu.serviceoa.service.WechatMenuService;
import com.guigu.vo.wechat.MenuVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜单 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2024-12-20
 */
@RestController
@RequestMapping("/admin/wechat/menu")
@Api(tags = "微信菜单")
public class WechatMenuController {

    @Autowired
    private WechatMenuService wechatMenuService;


    @ApiOperation("查询树形菜单")
    @GetMapping("findMenuInfo")
    public Result findMenuList(){
        List<MenuVo> list=wechatMenuService.findMenuList();
        return Result.ok(list);
    }

    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        Menu menu = wechatMenuService.getById(id);
        return Result.ok(menu);
    }

    @ApiOperation("增加菜单")
    @PostMapping("")
    public Result add(@RequestBody Menu menu){
        wechatMenuService.save(menu);
        return Result.ok();
    }

    @ApiOperation("删除菜单")
    @DeleteMapping("remove/{id}")
    public Result delete(@PathVariable int id){
        wechatMenuService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("更新菜单")
    @PutMapping("update")
    public Result update(@RequestBody Menu menu){
        wechatMenuService.updateById(menu);
        return Result.ok();
    }

    @ApiOperation("推送菜单")
    @GetMapping("syncMenu")
    public Result createMenu(){
        wechatMenuService.createMenu();
        return Result.ok();
    }

    @ApiOperation("删除菜单")
    @DeleteMapping("removeMenu")
    public Result removeMenu() throws WxErrorException {
        wechatMenuService.removeMenu();
        return Result.ok();
    }
}

