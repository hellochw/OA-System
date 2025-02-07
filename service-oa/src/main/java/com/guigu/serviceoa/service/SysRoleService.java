package com.guigu.serviceoa.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guigu.model.system.SysRole;
import com.guigu.vo.system.AssginRoleVo;
import com.guigu.vo.system.SysRoleQueryVo;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;

public interface SysRoleService extends IService<SysRole> {


    /**
     * 获取角色列表
     * @return
     */
    List<SysRole> getSysRoleList();


    /**
     * 根据用户id查询所有角色
     * @param userId
     * @return
     */
    Map<String, Object> findRoleByAdminId(Long userId);


    /**
     * 设置用户的角色信息
     * @param assginRoleVo
     */
    void doAssign(AssginRoleVo assginRoleVo);
}
