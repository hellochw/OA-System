package com.guigu.serviceoa.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.core.io.ResourceUtil;
import com.guigu.common.result.Result;
import com.guigu.model.process.ProcessTemplate;
import com.guigu.serviceoa.service.OaProcessTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 审批模板 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2024-12-15
 */
@RestController
@Api(tags = "审批模板")
@RequestMapping("/admin/process/processTemplate")
public class OaProcessTemplateController {

    @Autowired
    private OaProcessTemplateService oaProcessTemplateService;

    @ApiOperation("分页查询审批模版")
    @GetMapping("{page}/{limit}")
    public Result page(@PathVariable int page, @PathVariable int limit){
        Page<ProcessTemplate> param = new Page<>(page, limit);
        //虽然可以直接调用page分页方法，但由于前后端属性不一致问题 需手动调整一下
        Page<ProcessTemplate> pages=oaProcessTemplateService.selectByParam(param);
        return Result.ok(pages);
    }


    @ApiOperation("增加审批模板")
    @PostMapping("/save")
    public Result insert(@RequestBody ProcessTemplate processTemplate){
        oaProcessTemplateService.save(processTemplate);
        return Result.ok();
    }

    @DeleteMapping("/remove/{id}")
    @ApiOperation("删除审批模板")
    public Result remove(@PathVariable int id){
        oaProcessTemplateService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("修改审批模板")
    @PutMapping("/update")
    public Result updateById(@RequestBody ProcessTemplate processTemplate){
        oaProcessTemplateService.updateById(processTemplate);
        return Result.ok();
    }

    @ApiOperation("根据id查审批模板")
    @GetMapping("get/{id}")
    public Result<ProcessTemplate> getById(@PathVariable int id){
        return Result.ok(oaProcessTemplateService.getById(id));
    }


    @ApiOperation("上传流程定义文件")
    @PostMapping("/uploadProcessDefinition")
    public Result uploadProcessDefinition(MultipartFile file) throws IOException {
        String path = new File(ResourceUtils.getURL("classpath:").getPath()).getAbsolutePath();
        File processPath = new File(path+"/processes/");
        if(!processPath.exists()){
            processPath.mkdirs();
        }
        String originalFilename = file.getOriginalFilename();
        File tempFile = new File(processPath,originalFilename);

        file.transferTo(tempFile);

        Map<String, Object> map = new HashMap<>();
        //根据上传地址后续部署流程定义，文件名称为流程定义的默认key
        map.put("processDefinitionPath", "processes/" + originalFilename);
        map.put("processDefinitionKey", originalFilename.substring(0, originalFilename.lastIndexOf(".")));
        return Result.ok(map);

    }

    @ApiOperation("发布模板")
    @GetMapping("publish/{id}")
    public Result publish(@PathVariable int id){
        oaProcessTemplateService.publish(id);
        return Result.ok();
    }
}

