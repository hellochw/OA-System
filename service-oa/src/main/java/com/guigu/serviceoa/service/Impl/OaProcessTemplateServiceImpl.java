package com.guigu.serviceoa.service.Impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.guigu.model.process.ProcessTemplate;
import com.guigu.model.process.ProcessType;
import com.guigu.serviceoa.mapper.OaProcessTemplateMapper;
import com.guigu.serviceoa.service.OaProcessService;
import com.guigu.serviceoa.service.OaProcessTemplateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guigu.serviceoa.service.ProcessTypeSerivce;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * <p>
 * 审批模板 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2024-12-15
 */
@Service
public class OaProcessTemplateServiceImpl extends ServiceImpl<OaProcessTemplateMapper, ProcessTemplate> implements OaProcessTemplateService {

    @Autowired
    private ProcessTypeSerivce processTypeService;

    @Autowired
    private OaProcessService oaProcessService;

    /**
     * 传入模板类型对应的名字
     * @param param
     * @return
     */
    public Page<ProcessTemplate> selectByParam(Page<ProcessTemplate> param) {
        //分页获取审批模版
        Page<ProcessTemplate> page = this.page(param);
        //提取集合
        List<ProcessTemplate> records = page.getRecords();
        //遍历
        for (ProcessTemplate processTemplate : records) {
            //获取对应的审批类型实体
            ProcessType process = processTypeService.getById(processTemplate.getProcessTypeId());
            if (process == null) {
                continue;
            }
            //设置审批模版中类型名字属性
            processTemplate.setProcessTypeName(process.getName());
        }

        return page;

    }

    /**
     * 根据id发布模板
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    public void publish(int id) {
        //修改模板发布状态 0为未发布  1为已发布
        ProcessTemplate processTemplate = baseMapper.selectById(id);
        processTemplate.setStatus(1);
        baseMapper.updateById(processTemplate);

        //如果模板文件路径不为空  进行流程定义部署
        if (!StringUtils.isEmpty(processTemplate.getProcessDefinitionPath()))
        oaProcessService.processDefinition(processTemplate.getProcessDefinitionPath());
    }

}
