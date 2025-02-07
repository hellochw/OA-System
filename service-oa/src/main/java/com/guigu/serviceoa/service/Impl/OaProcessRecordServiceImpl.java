package com.guigu.serviceoa.service.Impl;

import com.guigu.model.process.ProcessRecord;
import com.guigu.serviceoa.mapper.OaProcessRecordMapper;
import com.guigu.serviceoa.service.OaProcessRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guigu.springsecurity.custom.LoginUserInfoHelper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 审批记录 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2024-12-17
 */
@Service
public class OaProcessRecordServiceImpl extends ServiceImpl<OaProcessRecordMapper, ProcessRecord> implements OaProcessRecordService {

    /**
     * 新增审批记录
     * @param processId
     * @param status
     * @param description
     */
    public void record(Long processId, Integer status, String description) {
        ProcessRecord processRecord = new ProcessRecord();
        processRecord.setProcessId(processId);
        processRecord.setStatus(status);
        processRecord.setDescription(description);
        processRecord.setOperateUserId(LoginUserInfoHelper.getUserId());
        processRecord.setOperateUser(LoginUserInfoHelper.getUsername());
        baseMapper.insert(processRecord);
    }
}
