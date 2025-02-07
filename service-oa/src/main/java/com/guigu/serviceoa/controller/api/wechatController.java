package com.guigu.serviceoa.controller.api;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.guigu.common.jwt.JwtHelper;
import com.guigu.common.result.Result;
import com.guigu.model.system.SysUser;
import com.guigu.serviceoa.service.SysUserService;
import com.guigu.vo.wechat.BindPhoneVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.activiti.api.runtime.shared.security.PrincipalGroupsProvider;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;

@Controller
@RequestMapping("/admin/wechat")
@Slf4j
@CrossOrigin
public class wechatController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private WxMpService wxMpService;

    @Value("${wechat.userInfoUrl}")
    private String userInfo;
    private PrincipalGroupsProvider principalGroupsProvider;


    @GetMapping("/authorize")
    public String authorize(String returnUrl){
        //方法的参数接收的是前端当前访问的url
        //设置微信回调接口
        //这里生成的url是最终返回给前端重定向的路径 在原访问的url的基础上又包含了token与当前用户微信用户的openId
        String redirectURL = wxMpService.getOAuth2Service().buildAuthorizationUrl(
                userInfo,
                WxConsts.OAuth2Scope.SNSAPI_USERINFO,
                URLEncoder.encode(returnUrl.replace("guiguoa", "#")));
        System.out.println(redirectURL);
        return "redirect:" + redirectURL;

    }

    //此接口是微信回调接口
    @GetMapping("/userInfo")
    public String userInfo(String code,@RequestParam("state")String returnUrl) throws WxErrorException {
        //通过code获取accessToekn
        WxOAuth2AccessToken accessToken = wxMpService.getOAuth2Service().getAccessToken(code);
        //再通过accessToken获取当前微信用户的OpenId
        String openId = accessToken.getOpenId();
        log.info("当前微信用户的openId：{}",openId);
        String token="";
        SysUser user=sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getOpenId,openId));
        if(user!=null){
            token = JwtHelper.createToken(user.getId(),user.getUsername());
        }
        if(returnUrl.indexOf("?") == -1) {
            return "redirect:" + returnUrl + "?token=" + token + "&openId=" + openId;
        } else {
            return "redirect:" + returnUrl + "&token=" + token + "&openId=" + openId;
        }
    }

    @ApiOperation("微信openId与手机号进行绑定")
    @PostMapping("/bindPhone")
    @ResponseBody
    public Result bindPhone(@RequestBody BindPhoneVo bindPhoneVo){
        SysUser user = sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, bindPhoneVo.getPhone()));

        //如果此手机号能查到用户 说明存在此用户 只是微信与手机号未进行绑定
        if(user!=null){
            user.setOpenId(bindPhoneVo.getOpenId());
            sysUserService.updateById(user);
            String token=JwtHelper.createToken(user.getId(),user.getUsername());
            return Result.ok(token);
        }
        //查不到说明管理系统不存在此此手机号绑定的用户  需要练习管理员进行新增
        else{
            return Result.fail("此手机号在后台系统中不存在，请联系管理员进行添加");
        }

    }
}
