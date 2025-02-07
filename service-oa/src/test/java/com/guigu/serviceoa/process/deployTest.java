package com.guigu.serviceoa.process;



import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class deployTest {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Test
    public void test(){
        Deployment deploy = repositoryService.createDeployment().
                addClasspathResource("process/qingjia.bpmn20.xml").
                addClasspathResource("process/myfilename-20241129120641700.png").deploy();
        System.out.println(deploy.getId());
    }

    @Test
    public void deleteDeployment() {
        //部署id
        String deploymentId = "ef912f0b-ae0a-11ef-9d6f-8cc84bf13b8f";
        //删除流程定义，如果该流程定义已有流程实例启动则删除时出错
        //repositoryService.deleteDeployment(deploymentId);
        //设置true 级联删除流程定义，即使该流程有流程实例启动也可以删除，设置为false非级别删除方式
        repositoryService.deleteDeployment(deploymentId, true);
    }

    //创建流程实例
    @Test
    public void createProcessInstance(){
        ProcessInstance process = runtimeService.startProcessInstanceByKey("qingjia");
        System.out.println(process.getId());
    }


    @Test
    public void findPendingTaskList() {
        //任务负责人
        String assignee = "A";
        List<Task> list = taskService.createTaskQuery()
                .taskAssignee(assignee)//只查询该任务负责人的任务
                .list();
        for (Task task : list) {
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }
    }


    /**
     * 完成任务
     */
    @Test
    public void completTask(){
        Task task = taskService.createTaskQuery()
                .taskAssignee("A")  //要查询的负责人
                .singleResult();//返回一条

        //完成任务,参数：任务id
        taskService.complete(task.getId());
    }

    /**
     * 查询已处理历史任务
     */
    @Test
    public void findProcessedTaskList() {
        //A已处理过的历史任务
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().taskAssignee("A").finished().list();
        for (HistoricTaskInstance historicTaskInstance : list) {
            System.out.println("流程实例id：" + historicTaskInstance.getProcessInstanceId());
            System.out.println("任务id：" + historicTaskInstance.getId());
            System.out.println("任务负责人：" + historicTaskInstance.getAssignee());
            System.out.println("任务名称：" + historicTaskInstance.getName());
        }
    }


    //挂起所有流程实例
    @Test
    public void suspendProcessInstance(){
        ProcessDefinition qingjia = repositoryService.createProcessDefinitionQuery().processDefinitionKey("qingjia").singleResult();

        if (qingjia.isSuspended()){
            //如果挂起  则激活
            repositoryService.activateProcessDefinitionById(qingjia.getId(),true,null);
        }
        else{
            //否则是激活  则挂起
            repositoryService.suspendProcessDefinitionById(qingjia.getId(),true,null);
        }
    }
}
