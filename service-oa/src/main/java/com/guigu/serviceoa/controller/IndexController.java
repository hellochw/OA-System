package com.guigu.serviceoa.controller;
import com.guigu.common.exception.GuiguException;
import com.guigu.common.jwt.JwtHelper;
import com.guigu.common.result.Result;
import com.guigu.model.system.SysUser;
import com.guigu.serviceoa.service.SysMenuService;
import com.guigu.serviceoa.service.SysUserService;
import com.guigu.vo.system.LoginVo;
import com.guigu.vo.system.RouterVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 后台登录登出
 * </p>
 */
@Api(tags = "后台登录管理")
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysMenuService sysMenuService;


    /**
     * 登录
     * @return
     */
    @ApiOperation(value = "登录")
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo) {

        //基于用户输入的用户名来查询用户实体
        SysUser sysUser = sysUserService.getByUserName(loginVo.getUsername());
        if(null == sysUser) {
            throw new GuiguException(201,"用户不存在");
        }
        //由于密码未加密  因此 直接对比密码就好了  后期需要加密再说
        if(!sysUser.getPassword().equals(loginVo.getPassword())) {
            throw new GuiguException(201,"密码错误");
        }
        if(sysUser.getStatus().intValue() == 0) {
            throw new GuiguException(201,"用户被禁用");
        }

        //登入成功 ，创建jwt令牌并返回
        Map<String, Object> map = new HashMap<>();
        map.put("token", JwtHelper.createToken(sysUser.getId(), sysUser.getUsername()));
        return Result.ok(map);
    }


    /**
     * 获取用户信息
     * @return
     */
    @ApiOperation("获取用户信息")
    @GetMapping("info")
    public Result info(HttpServletRequest request) {

        //从请求头中获取jwt信息
        String token = request.getHeader("token");
        //解析获取jwt 获取用户id
        Long userId = JwtHelper.getUserId(token);

        //获取该用户可使用的菜单列表
        List<RouterVo> routers=sysMenuService.getRoutesByUserId(userId);

        //获取该用户可用的按钮项列表
        List<String> perms=sysMenuService.getPermsBysByUserId(userId);

        Map<String, Object> map = new HashMap<>();
        map.put("roles","[admin]");
        map.put("name","admin");
        map.put("avatar","https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
        map.put("buttons", perms);
        map.put("routers", routers);
        return Result.ok(map);
    }
    /**
     * 退出
     * @return
     */
    @ApiOperation("退出登入")
    @PostMapping("logout")
    public Result logout(){
        return Result.ok();
    }

}