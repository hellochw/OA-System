package com.guigu.serviceoa.service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guigu.model.process.Process;
import com.guigu.vo.process.ApprovalVo;
import com.guigu.vo.process.ProcessFormVo;
import com.guigu.vo.process.ProcessQueryVo;
import com.guigu.vo.process.ProcessVo;
import org.springframework.security.core.parameters.P;

import java.util.Map;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author atguigu
 * @since 2024-12-16
 */
public interface OaProcessService extends IService<Process> {


    /**
     * 分页条件查询
     * @param pages
     * @param processQueryVo
     * @return
     */
    Page<ProcessVo> queryPage(Page pages, ProcessQueryVo processQueryVo);


    /**
     * 部署流程定义
     * @param path
     */
    void processDefinition(String path);


    /**
     * 启动一个审批实例
     * @param processFormVo
     */
    void startUp(ProcessFormVo processFormVo);


    /**
     *
     * 查询代办任务
     * @param pages
     * @return
     */
    Page<ProcessVo> findPending(Page<ProcessVo> pages);


    /**
     * 查看任务详细信息
     * @param id
     * @return
     */
    Map<String, Object> pendingDetail(Long id);

    /**
     * 审批任务
     */
    void approve(ApprovalVo approvalVo);

    /**
     * 查已处理的任务
     * @param pages
     * @return
     */
    Page<ProcessVo> findProcessed(Page<Process> pages);


    /**
     * 查已发起的
     * @param pages
     * @return
     */
    Page<ProcessVo> findStarted(Page<ProcessVo> pages);
}
