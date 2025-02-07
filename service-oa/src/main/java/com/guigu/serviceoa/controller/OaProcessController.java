package com.guigu.serviceoa.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guigu.common.result.Result;
import com.guigu.model.process.Process;
import com.guigu.serviceoa.service.OaProcessService;
import com.guigu.vo.process.ProcessQueryVo;
import com.guigu.vo.process.ProcessVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 审批类型 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2024-12-16
 */
@RestController
@RequestMapping("/admin/process")
@Api(tags = "审批流程")
public class OaProcessController {

        @Autowired
        private OaProcessService oaProcessService;

        @ApiOperation("条件分页查询")
        @GetMapping("{page}/{limit}")
        public Result page(@PathVariable int page, @PathVariable int limit, ProcessQueryVo processQueryVo) {
            Page<ProcessVo> pages=new Page<>(page,limit);
            pages=oaProcessService.queryPage(pages,processQueryVo);
            return Result.ok(pages);
        }




}




