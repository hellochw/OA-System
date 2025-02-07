package com.guigu.serviceoa.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guigu.model.process.ProcessType;
import com.guigu.serviceoa.mapper.ProcessTypeMapper;

import java.util.List;


public interface ProcessTypeSerivce extends IService<ProcessType> {
    /**
     * 查找所有审批类型以及归属的审批模板
     * @return
     */
    List<ProcessType> findProcessType();
}
