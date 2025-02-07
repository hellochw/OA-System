package com.guigu.serviceoa.process;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class deployTest3 {


    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;


    //部署流程实例
    @Test
    public void deployProcess(){
        repositoryService.createDeployment().addClasspathResource("process/jiaban03.bpmn20.xml").deploy();
    }

    //创建流程实例
    @Test
    public void createProcessInstance(){
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("jiaban03");
        System.out.println(processInstance.getId());
    }


    //查询用户所在任务组中的任务
    @Test
    public void findProcessInstanceTaskByCandidateUser(){
        List<Task> list = taskService.createTaskQuery().taskCandidateUser("rose").list();
        for (Task task : list) {
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }
    }


    //让指定用户拾取任务
    @Test
    public void claimTask(){
        Task task = taskService.createTaskQuery().taskCandidateUser("rose").singleResult();
        if (task!=null)
            taskService.claim(task.getId(), "rose");
    }


    //查询个人任务
    @Test
    public void findTaskListByUser(){
        List<Task> list = taskService.createTaskQuery().taskAssignee("lucy").list();
        for (Task task : list) {
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }
    }

    //完成任务
    @Test
    public void completeTask(){
        Task task = taskService.createTaskQuery().taskAssignee("rose").singleResult();
        taskService.complete(task.getId());
    }


    //归还任务组
    @Test
    public void assigneeToGroupTask() {
        String taskId = "cf80d869-aec6-11ef-ae41-8cc84bf13b8f";
        // 任务负责人
        String userId = "lucy";
        // 校验userId是否是taskId的负责人，如果是负责人才可以归还组任务
        Task task = taskService
                .createTaskQuery()
                .taskId(taskId)
                .taskAssignee(userId)
                .singleResult();
        if (task != null) {
            // 如果设置为null，归还组任务,该 任务没有负责人
            taskService.setAssignee(taskId, null);
        }
    }
}
