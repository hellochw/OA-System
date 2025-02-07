package com.guigu.serviceoa.controller.api;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.guigu.common.result.Result;
import com.guigu.model.process.Process;
import com.guigu.model.process.ProcessTemplate;
import com.guigu.model.process.ProcessType;
import com.guigu.serviceoa.service.OaProcessService;
import com.guigu.serviceoa.service.OaProcessTemplateService;
import com.guigu.serviceoa.service.ProcessTypeSerivce;
import com.guigu.serviceoa.service.SysUserService;
import com.guigu.vo.process.ApprovalVo;
import com.guigu.vo.process.ProcessFormVo;
import com.guigu.vo.process.ProcessVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/process")
@Api(tags = "审批实例管理")
@CrossOrigin //解决微信公众号访问接口的跨域问题
public class ProcessController {

    @Autowired
    private ProcessTypeSerivce processTypeService;

    @Autowired
    private OaProcessTemplateService processTemplateService;

    @Autowired
    private OaProcessService processService;

    @Autowired
    private SysUserService sysUserService;

    @ApiOperation("查询审批类型与归属的审批模板")
    @GetMapping("/findProcessType")
    public Result findProcessType(){
        List<ProcessType> list=processTypeService.findProcessType();
        return Result.ok(list);
    }

    @ApiOperation("根据审批模板id获取模板")
    @GetMapping("/getProcessTemplate/{processTemplateId}")
    public Result getProcessTemplate(@PathVariable Long processTemplateId){
        ProcessTemplate template = processTemplateService.getById(processTemplateId);
        return Result.ok(template);
    }

    @PostMapping("/startUp")
    @ApiOperation("启动一个审批实例")
    public Result startUp(@RequestBody ProcessFormVo processFormVo){
        processService.startUp(processFormVo);
        return Result.ok();
    }

    @ApiOperation("查代办任务")
    //注意 在实际业务中 查询代办任务其实是不需要分页的 这里只是为了练习分页
    @GetMapping("/findPending/{page}/{limit}")
    public Result findPending(@PathVariable int page, @PathVariable int limit){
        //这里用ProcessVo封装是因为相对于Process Vo中有许多的name相关属性 这是前端所需要的
        Page<ProcessVo> pages=new Page<>(page,limit);
        Page<ProcessVo> result=processService.findPending(pages);
        return Result.ok(result);
    }

    @ApiOperation("任务详情")
    @GetMapping("show/{id}")
    public Result pendingDetail(@PathVariable Long id){
        //这里返回map类型数据是因为未来方便起见
        Map<String,Object> map=processService.pendingDetail(id);
        return  Result.ok(map);
    }

    @ApiOperation("审批任务")
    @PostMapping("/approve")
    public Result approve(@RequestBody ApprovalVo approvalVo){
        processService.approve(approvalVo);
        return Result.ok();
    }

    @ApiOperation("查看已处理")
    @GetMapping("/findProcessed/{page}/{limit}")
    public Result findProcessed(@PathVariable int page, @PathVariable int limit){
        Page<Process> pages=new Page<>(page,limit);
        Page<ProcessVo> result=processService.findProcessed(pages);
        return Result.ok(result);
    }

    @ApiOperation("查看已发起的任务")
    @GetMapping("/findStarted/{page}/{limit}")
    public Result findStarted(@PathVariable int page, @PathVariable int limit){
        Page<ProcessVo> pages=new Page<>(page,limit);
        Page<ProcessVo> result=processService.findStarted(pages);
        return Result.ok(result);
    }

}
