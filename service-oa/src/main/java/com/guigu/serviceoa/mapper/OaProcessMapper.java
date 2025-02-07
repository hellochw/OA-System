package com.guigu.serviceoa.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guigu.model.process.Process;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guigu.vo.process.ProcessQueryVo;
import com.guigu.vo.process.ProcessVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 审批类型 Mapper 接口
 * </p>
 *
 * @author atguigu
 * @since 2024-12-16
 */
@Mapper
public interface OaProcessMapper extends BaseMapper<Process> {

    /**
     * 分页条件查询
     * @param page
     * @param processQueryVo
     * @return
     */
   Page<ProcessVo> selectPage(Page page, @Param("vo") ProcessQueryVo processQueryVo);

}
