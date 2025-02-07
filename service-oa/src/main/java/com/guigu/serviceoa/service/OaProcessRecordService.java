package com.guigu.serviceoa.service;

import com.guigu.model.process.ProcessRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 审批记录 服务类
 * </p>
 *
 * @author atguigu
 * @since 2024-12-17
 */
public interface OaProcessRecordService extends IService<ProcessRecord> {
    /**
     * 新增审批记录
     * @param processId
     * @param status
     * @param description
     */
        void record(Long processId, Integer status, String description);
}
