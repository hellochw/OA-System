package com.guigu.serviceoa.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guigu.model.process.ProcessTemplate;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guigu.model.process.ProcessType;

import java.util.List;

/**
 * <p>
 * 审批模板 服务类
 * </p>
 *
 * @author atguigu
 * @since 2024-12-15
 */
public interface OaProcessTemplateService extends IService<ProcessTemplate> {


    /**
     * 传入模板类型对应的名字
     * @param param
     * @return
     */
    Page<ProcessTemplate> selectByParam(Page<ProcessTemplate> param);


    /**
     * 根据id发布模板
     * @param id
     */
    void publish(int id);


    /**
     * 查找所有审批类型以及归属的审批模板
     * @return
     */
}
