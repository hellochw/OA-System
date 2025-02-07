package com.guigu.serviceoa.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guigu.common.result.Result;
import com.guigu.model.system.SysRole;
import com.guigu.serviceoa.service.SysRoleService;
import com.guigu.vo.system.AssginRoleVo;
import com.guigu.vo.system.SysRoleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Api(tags = "角色管理")
@RestController
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {

@Autowired
private SysRoleService sysRoleService;

    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation(value = "根据id获取指定角色")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        SysRole role = sysRoleService.getById(id);
        return Result.ok(role);
    }

    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation("获取角色列表")
    @GetMapping("/findAll")
    public Result<List<SysRole>> getSysRoleList(){
        List<SysRole> list=sysRoleService.getSysRoleList();
        return Result.ok(list);
    }

    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation("分页查询角色列表")
    @GetMapping("/{page}/{limit}")
    public Result<IPage<SysRole>> pageSysRoleList(@PathVariable int page, @PathVariable int limit,
                                                  SysRoleQueryVo sysRoleQueryVo){
        //创建分页条件
        IPage<SysRole> ipage=new Page<>(page,limit);
        //获取名字
        String roleName = sysRoleQueryVo.getRoleName();
        //构造查询条件
        LambdaQueryWrapper<SysRole> queryWrapper=new LambdaQueryWrapper<>();

        //不为空与null则增加模糊查询
        if(!StringUtils.isEmpty(roleName)) {
            //封装 like模糊查询
            queryWrapper.like(SysRole::getRoleName,roleName);
        }
        //查询
        sysRoleService.page(ipage,queryWrapper);
        //返回结果
        return Result.ok(ipage);
    }

    @PreAuthorize("hasAuthority('bnt.sysRole.add')")
    @ApiOperation(value = "新增角色")
    @PostMapping("save")
    public Result save(@RequestBody @Validated SysRole role) {
        sysRoleService.save(role);
        return Result.ok();
    }

    @PreAuthorize("hasAuthority('bnt.sysRole.update')")
    @ApiOperation(value = "修改角色")
    @PutMapping("update")
    public Result updateById(@RequestBody SysRole role) {
        sysRoleService.updateById(role);
        return Result.ok();
    }


    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @ApiOperation("根据id删除角色")
    @DeleteMapping("/remove/{id}")
    public Result removeSysRole(@PathVariable int id){
        sysRoleService.removeById(id);
        return Result.ok();
    }

    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @ApiOperation("根据id批量删除角色")
    @DeleteMapping("/batchRemove")
    public Result removeSysRoles(@RequestBody List<Integer> idList){
        sysRoleService.removeByIds(idList);
        return Result.ok();
    }

    @ApiOperation(value = "根据用户获取角色数据")
    @GetMapping("/toAssign/{userId}")
    public Result toAssign(@PathVariable Long userId) {
        Map<String, Object> roleMap = sysRoleService.findRoleByAdminId(userId);
        return Result.ok(roleMap);
    }


    @ApiOperation(value = "根据用户分配角色")
    @PostMapping("/doAssign")
    public Result doAssign(@RequestBody AssginRoleVo assginRoleVo) {
        sysRoleService.doAssign(assginRoleVo);
        return Result.ok();
    }
}
