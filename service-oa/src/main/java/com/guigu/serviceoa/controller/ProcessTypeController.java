package com.guigu.serviceoa.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guigu.common.ServiceUtilApplication;
import com.guigu.common.result.Result;
import com.guigu.model.process.ProcessType;
import com.guigu.serviceoa.service.ProcessTypeSerivce;
import com.guigu.vo.process.ProcessQueryVo;
import io.netty.util.ResourceLeakDetector;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.ParameterResolutionDelegate;
import org.springframework.web.bind.annotation.*;


@RestController
@Api(tags = "审批类型")
@RequestMapping("/admin/process/processType")
public class ProcessTypeController {

    @Autowired
    private ProcessTypeSerivce processTypeService;


    @ApiOperation("分页查询")
    @GetMapping("{page}/{limit}")
    public Result page(@PathVariable int page, @PathVariable int limit){
        Page<ProcessType> Page = new Page<>(page, limit);
        processTypeService.page(Page);
        return Result.ok(Page);
    }

    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        ProcessType processType = processTypeService.getById(id);
        return Result.ok(processType);
    }

    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result save(@RequestBody ProcessType processType){
        processTypeService.save(processType);
        return Result.ok();
    }

    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result update(@RequestBody ProcessType processType){
        processTypeService.updateById(processType);
        return Result.ok();
    }

    @DeleteMapping("remove/{id}")
    @ApiOperation("删除")
    public Result removeByI(@PathVariable Long id){
        processTypeService.removeById(id);
        return Result.ok();
    }


    @ApiOperation("获取所有审批类型")
    @GetMapping("/findAll")
    public Result findAll(){
        return Result.ok(processTypeService.list());
    }


}
