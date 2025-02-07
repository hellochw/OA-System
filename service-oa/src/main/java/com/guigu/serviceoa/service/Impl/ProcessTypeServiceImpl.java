package com.guigu.serviceoa.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guigu.model.process.ProcessTemplate;
import com.guigu.model.process.ProcessType;
import com.guigu.serviceoa.mapper.ProcessTypeMapper;
import com.guigu.serviceoa.service.OaProcessTemplateService;
import com.guigu.serviceoa.service.ProcessTypeSerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProcessTypeServiceImpl extends ServiceImpl<ProcessTypeMapper, ProcessType> implements ProcessTypeSerivce{

    @Autowired
    private OaProcessTemplateService oaProcessTemplateService;

    /**
     * 查找所有审批类型以及归属的审批模板
     * @return
     */
    public List<ProcessType> findProcessType() {
        List<ProcessType> typeList = this.list();

        for (ProcessType processType : typeList) {
            LambdaQueryWrapper<ProcessTemplate> queryWrapper = new LambdaQueryWrapper<>();
            //TODO 这里查询模板应该得加上Status=1的条件 即查询已经发布的模板 但是老师并未提及
            queryWrapper.eq(ProcessTemplate::getProcessTypeId,processType.getId());
            List<ProcessTemplate> templeList = oaProcessTemplateService.list(queryWrapper);
            processType.setProcessTemplateList(templeList);

        }

        return typeList;
    }
}
