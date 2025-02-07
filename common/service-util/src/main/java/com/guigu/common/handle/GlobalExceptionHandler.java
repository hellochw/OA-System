package com.guigu.common.handle;

import com.guigu.common.exception.GuiguException;
import com.guigu.common.result.Result;
import com.guigu.common.result.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;



/**
 * 全局异常处理类
 *
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //处理异常Exception的方法
    @ExceptionHandler(Exception.class)
    public Result error(Exception e){
        log.info("出错啦");
        e.printStackTrace();
        return Result.fail();
    }

    public Result error(GuiguException e){
        log.info("出错啦！");
        e.printStackTrace();
        return Result.fail().message(e.getMessage()).code(e.getCode());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public Result error(AccessDeniedException e) throws AccessDeniedException {
        return Result.build(null, ResultCodeEnum.PERMISSION);
    }
}