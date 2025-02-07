package com.guigu.serviceoa.service.Impl;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guigu.model.process.Process;
import com.guigu.model.process.ProcessRecord;
import com.guigu.model.process.ProcessTemplate;
import com.guigu.model.system.SysUser;
import com.guigu.serviceoa.mapper.OaProcessMapper;
import com.guigu.serviceoa.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guigu.springsecurity.custom.LoginUserInfoHelper;
import com.guigu.vo.process.ApprovalVo;
import com.guigu.vo.process.ProcessFormVo;
import com.guigu.vo.process.ProcessQueryVo;
import com.guigu.vo.process.ProcessVo;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2024-12-16
 */
@Service
public class OaProcessServiceImpl extends ServiceImpl<OaProcessMapper, Process> implements OaProcessService {

    @Autowired
    private OaProcessMapper oaProcessMapper;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private OaProcessTemplateService oaProcessTemplateService;

    @Autowired
    private OaProcessRecordService oaProcessRecordService;

    @Autowired
    private OaProcessService oaProcessService;

    @Autowired
    private ProcessTypeSerivce processTypeService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private MessageService messageService;


    /**
     * 分页条件查询
     * @param pages
     * @param processQueryVo
     * @return
     */
    public Page<ProcessVo> queryPage(Page pages, ProcessQueryVo processQueryVo) {
        Page<ProcessVo> pagess=baseMapper.selectPage(pages,processQueryVo);
        return pagess;
    }


    /**
     * 部署流程定义
     * @param path
     */
    public void processDefinition(String path) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(path);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        repositoryService.createDeployment().addZipInputStream(zipInputStream).deploy();
    }

    /**
     * 启动一个审批实例
     * @param processFormVo
     */
    public void startUp(ProcessFormVo processFormVo) {
        //1、获取用户信息
        Long userId = LoginUserInfoHelper.getUserId();
        SysUser user = sysUserService.getById(userId);
        //2、获取模板信息
        ProcessTemplate template = oaProcessTemplateService.getById(processFormVo.getProcessTemplateId());
        //3、创建业务表实体，并塞进一些能设置的属性并更向业务表(oa_process)插入
        Process process = new Process();
        BeanUtils.copyProperties(processFormVo, process);
        //设置其他其他参数
        String workNo = System.currentTimeMillis() + "";
        process.setProcessCode(workNo);
        process.setUserId(userId);
        process.setFormValues(processFormVo.getFormValues());
        process.setTitle(user.getName() + "发起" + template.getName() + "申请");
        process.setStatus(1);
        //插入 同时获取到业务id(businessKey)
        baseMapper.insert(process);

        //4、启动流程实例
        //流程定义key
        String key = template.getProcessDefinitionKey();
        //businessKey
        String businessKey = String.valueOf(process.getId());
        //流程参数
        String formValues = processFormVo.getFormValues();
        JSONObject jsonObject = JSONObject.parseObject(formValues);
        JSONObject formData = jsonObject.getJSONObject("formData");
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, Object> entry : formData.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        //到这里其实就结束了 但是老师又在外层套了一个map 将这个数据map放进去 不知用意 但也先打上了
        Map<String, Object> variables = new HashMap<>();
        variables.put("data", map);
        //参数都获取的差不多就启动啦 并获取流程实例id
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key, businessKey, variables);

        //5、获取审批人 注意一个阶段的审批可能会有多个审批人(也就对应着多个task)
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        //用来装审批人姓名的集合
        List<String> nameList = new ArrayList<>();
        for (Task task : taskList) {
            //获取审批人username
            String assignee = task.getAssignee();
            //基于username查询审批人实体
            SysUser sysUser = sysUserService.getByUserName(assignee);
            //要审批人真实姓名
            String name = sysUser.getName();
            //添加到审批姓名集合中
            nameList.add(name);
            //TODO 消息推送给审批人
            messageService.sendMessage(process.getId(),sysUser.getId(),task.getId());
        }

        //6、最后补充业务表相关信息并更新
        process.setProcessInstanceId(processInstance.getId());
        process.setDescription("等待"+nameList.toString()+"审批");
        baseMapper.updateById(process);
        //新增审批记录 要processId,状态，描述
        oaProcessRecordService.record(Long.valueOf(businessKey),1,"发起申请");

    }

    /**
     *
     * 查询代办任务
     * @param pages
     * @return
     */
    public Page<ProcessVo> findPending(Page<ProcessVo> pages) {
       //创建查询条件 审批人为当前登入的用户 并按照创建时间降序排序
        TaskQuery query = taskService.createTaskQuery().taskAssignee(LoginUserInfoHelper.getUsername())
                .orderByTaskCreateTime().desc();

        //创建分页参数
        //起始索引
        int start = (int)((pages.getCurrent() - 1) * pages.getSize());
        //每页显示记录数
        int size = (int)pages.getSize();
        //使用查询条件进行分页查询
        List<Task> tasks = query.listPage(start, size);

        //准备将将task中的Process->ProcessVo
        List<ProcessVo> processVoList = new ArrayList<>();
        for (Task task : tasks) {
            ProcessVo processVo = new ProcessVo();
            //通过task获取businessKey 然后获取process  老师是通过获取流程实例 通过流程实例获取businessKey
            Process process = this.getById(task.getBusinessKey());

            if (process==null)
                continue;

            //获取其他参数
            String templateName = oaProcessTemplateService.getById(process.getProcessTemplateId()).getName();
            String typeName = processTypeService.getById(process.getProcessTypeId()).getName();
            String userName = sysUserService.getById(process.getUserId()).getName();
            //完整赋值
            BeanUtils.copyProperties(process, processVo);
            processVo.setProcessTemplateName(templateName);
            processVo.setProcessTypeName(typeName);
            processVo.setName(userName);
            processVo.setTaskId(task.getId());
            //加入到集合中
            processVoList.add(processVo);

        }
        //最后 手动封装Page  包括 总记录数 当前页码 当前页的记录数 以及record
        Page<ProcessVo> processVoPage= new Page<>();
        //这里设置的该用户的总任务数（也就是基于query这个条件的总记录数） 而不是分页后的记录数
        processVoPage.setTotal(query.count());
        processVoPage.setCurrent(pages.getCurrent());
        processVoPage.setSize(pages.getSize());
        processVoPage.setRecords(processVoList);
        return processVoPage;
    }


    /**
     * 查看任务详细信息
     * @param id
     * @return
     */
    public Map<String, Object> pendingDetail(Long id) {
       //注意 这里的id是processId
        //首先根据id获取process
        Process process = this.getById(id);

        //然后获取此process的历史记录信息
        List<ProcessRecord> recordList = oaProcessRecordService.list(
                new LambdaQueryWrapper<ProcessRecord>()
                        .eq(ProcessRecord::getProcessId, process.getId()));
        //获取此process的模板信息
        ProcessTemplate template = oaProcessTemplateService.getById(process.getProcessTemplateId());

        //准备验证用户是否可以审批  这里有一点抽象
        boolean isOk=false; //默认不可以
        //获取当前流程实例的所有task 同一阶段，每一个task对应不同的审批人
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(process.getProcessInstanceId()).list();
        //遍历taskList 获取每一个task的审批人 并与当前登入用户做对比
        for (Task task : taskList) {
            String assignee = task.getAssignee();
            if (assignee.equals(LoginUserInfoHelper.getUsername())){
                isOk=true;
                break;
            }
        }

        //封装所需要的数据
        Map<String, Object> map = new HashMap<>();
        map.put("process", process);
        map.put("processRecordList", recordList);
        map.put("processTemplate", template);
        map.put("isApprove", isOk);

        return map;

    }

    /**
     * 审批任务
     */
    public void approve(ApprovalVo approvalVo) {
       //获取流程实体
        Process process = this.getById(approvalVo.getProcessId());
        //获取流程参数 根据任务id获取
        Map<String, Object> variables = taskService.getVariables(approvalVo.getTaskId());

        //获取一下任务id
        String taskId = approvalVo.getTaskId();
        //判断状态 -1驳回 1通过
        if (approvalVo.getStatus()==1){
            //通过
            taskService.complete(taskId, variables);
        }
        else {
            //驳回
            this.endTask(taskId);
        }

        //记录一下流程状态
        String description=approvalVo.getStatus()==1 ? "通过":"驳回";
        oaProcessRecordService.record(approvalVo.getProcessId(),approvalVo.getStatus(),description);

        //设置process表状态
        //先查查接下来是否还有任务 若有 说明一定当前任务一定通过了
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(process.getProcessInstanceId()).list();
        if (!CollectionUtils.isEmpty(taskList)) {
            List<String> nameList = new ArrayList<>();
            for (Task task : taskList) {
                String assignee = task.getAssignee();
                //基于username查询审批人实体
                SysUser sysUser = sysUserService.getByUserName(assignee);
                //要审批人真实姓名
                String name = sysUser.getName();
                //添加到审批姓名集合中
                nameList.add(name);
                //TODO 消息推送给审批人
                messageService.sendMessage(process.getId(),sysUser.getId(),task.getId());
            }
            process.setDescription("等待" + nameList.toString() + "审批");
            process.setStatus(1);
        }
        //没有 则可能是因为驳回 使任务结束后没有任务  也有可能是因为通过了 而且当前任务后面就没有其他任务了
        else{
            //当前任务通过了 但当前任务后面确实没有任务了 则此流程顺利通过
            if (approvalVo.getStatus()==1){
                process.setDescription("审批完整通过");
                process.setStatus(2);
            }
            else{
                //否则 当前任务被驳回 流程结束无任务了
                process.setDescription("审批被驳回");
                process.setStatus(-1);
            }

        }
        //更新流程信息
        baseMapper.updateById(process);

    }
    /**
     * 查询处理过的任务
     * @param pages
     * @return
     */
    public Page<ProcessVo> findProcessed(Page<Process> pages) {
        //创建查询历史task的条件
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
                        .taskAssignee(LoginUserInfoHelper.getUsername())
                        .finished().orderByTaskCreateTime().desc();//这里的finish表示已经处理的任务

        //创建分页参数
        int start = (int)((pages.getCurrent() - 1) * pages.getSize());
        int size = (int)pages.getSize();
        //基于条件进行分页查询
        List<HistoricTaskInstance> hisList = query.listPage(start, size);
        //将task对应的process->processVo
        List<ProcessVo> processVoList = new ArrayList<>();
        for (HistoricTaskInstance task : hisList) {
            ProcessVo processVo = new ProcessVo();

            String processInstanceId = task.getProcessInstanceId();
            Process process = this.getOne(new LambdaQueryWrapper<Process>().eq(Process::getProcessInstanceId, processInstanceId));

            if (process==null)
                continue;

            String templeName = oaProcessTemplateService.getById(process.getProcessTemplateId()).getName();
            String typeName = processTypeService.getById(process.getProcessTypeId()).getName();
            String userName = sysUserService.getById(process.getUserId()).getName();
            BeanUtils.copyProperties(process, processVo);
            processVo.setProcessTemplateName(templeName);
            processVo.setProcessTypeName(typeName);
            processVo.setName(userName);
            processVo.setTaskId(task.getId());
            processVoList.add(processVo);
        }

        Page<ProcessVo> processVoPage= new Page<>();
        processVoPage.setTotal(query.count());
        processVoPage.setCurrent(pages.getCurrent());
        processVoPage.setSize(pages.getSize());
        processVoPage.setRecords(processVoList);
        return processVoPage;
    }

    /**
     * 查已发起的
     * @param pages
     * @return
     */
    public Page<ProcessVo> findStarted(Page<ProcessVo> pages) {

        ProcessQueryVo processQuerytVo = new ProcessQueryVo();
        processQuerytVo.setUserId(LoginUserInfoHelper.getUserId());
        Page<ProcessVo> page = oaProcessMapper.selectPage(pages, processQuerytVo);
        for (ProcessVo processVo : page.getRecords()) {
            processVo.setTaskId("0");
        }
        return page;
    }

    //驳回 结束流程
    private void endTask(String taskId) {
        //  当前任务
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        List endEventList = bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class);
        // 并行任务可能为null
        if(CollectionUtils.isEmpty(endEventList)) {
            return;
        }
        FlowNode endFlowNode = (FlowNode) endEventList.get(0);
        FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(task.getTaskDefinitionKey());

        //  临时保存当前活动的原始方向
        List originalSequenceFlowList = new ArrayList<>();
        originalSequenceFlowList.addAll(currentFlowNode.getOutgoingFlows());
        //  清理活动方向
        currentFlowNode.getOutgoingFlows().clear();

        //  建立新方向
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlowId");
        newSequenceFlow.setSourceFlowElement(currentFlowNode);
        newSequenceFlow.setTargetFlowElement(endFlowNode);
        List newSequenceFlowList = new ArrayList<>();
        newSequenceFlowList.add(newSequenceFlow);
        //  当前节点指向新的方向
        currentFlowNode.setOutgoingFlows(newSequenceFlowList);

        //  完成当前任务
        taskService.complete(task.getId());
    }
}




























