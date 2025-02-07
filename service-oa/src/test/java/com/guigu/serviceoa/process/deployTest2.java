package com.guigu.serviceoa.process;


import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class deployTest2 {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;


    //创建流程定义
    @Test
    public void deployProcess(){
        Deployment deploy = repositoryService.createDeployment().addClasspathResource("process/jiaban.bpmn20.xml").deploy();
        System.out.println(deploy.getId());
    }


    //创建流程实例
    @Test
    public void createProcessInstance(){
        //为对应的变量赋值
        HashMap<String,Object> map=new HashMap<>();
        map.put("A","Jack");
        //map.put("B","Tom");
        ProcessInstance jiaban = runtimeService.startProcessInstanceByKey("jiaban",map);
        System.out.println(jiaban.getProcessDefinitionId());
        System.out.println(jiaban.getProcessInstanceId());
        System.out.println(jiaban.getId());
    }


    //查询任务
    @Test
    public void findTask(){
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
    public void taskTest(){
        List<Task> list = taskService.createTaskQuery().taskAssignee("Jack").list();
        Map<String,Object> map=new HashMap<>();
        map.put("B","LUCY");
        taskService.complete(list.get(0).getId(),map);
    }



    @Test
    public void deleteDeployment() {
        //部署id
        String deploymentId = "f77913fa-bd08-11ef-bb36-8cc84bf13b8f";
        //删除流程定义，如果该流程定义已有流程实例启动则删除时出错
        //repositoryService.deleteDeployment(deploymentId);
        //设置true 级联删除流程定义，即使该流程有流程实例启动也可以删除，设置为false非级别删除方式
        repositoryService.deleteDeployment(deploymentId, true);
    }
}
