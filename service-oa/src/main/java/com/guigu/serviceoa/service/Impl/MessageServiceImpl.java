package com.guigu.serviceoa.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.guigu.model.process.Process;
import com.guigu.model.process.ProcessTemplate;
import com.guigu.model.system.SysUser;
import com.guigu.serviceoa.service.MessageService;
import com.guigu.serviceoa.service.OaProcessService;
import com.guigu.serviceoa.service.SysUserService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private OaProcessService oaProcessService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private OaProcessTemplateServiceImpl oaProcessTemplateService;

    @Autowired
    private WxMpService wxMpService;

    @Override
    public void sendMessage(Long processId, Long userId, String taskId) {
        //获取被推送人实体
        SysUser user = sysUserService.getById(userId);
        //获取流程实体
        Process process = oaProcessService.getById(processId);
        //获取流程对应的模板实体
        ProcessTemplate template = oaProcessTemplateService.getById(process.getProcessTemplateId());
        //获取流程发起人
        SysUser submitUser = sysUserService.getById(process.getUserId());
        //获取被推送人的openId
        String openId = user.getOpenId();
        if (StringUtils.isEmpty(openId)){
            openId="oV_Ll67vM9kshS0hgGhZxGcH7kIw";
        }
        //创建消息模板对象
        //设置相关参数
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(openId)//要推送的用户openid
                .templateId("ZJ-LpXbAFd8kze0fkBTZM8mJwHZbYKfayUChO5lMi0A")//模板id
                .url("http://192.168.31.122:9090/#/show/"+processId+"/"+taskId)//点击模板消息要访问的网址
                .build();
        //从process中获取具体提交的表单数据
        JSONObject jsonObject = JSON.parseObject(process.getFormValues());
        JSONObject formShowData = jsonObject.getJSONObject("formShowData");
        StringBuffer content = new StringBuffer();
        for (Map.Entry entry : formShowData.entrySet()) {
            content.append(entry.getKey()).append("：").append(entry.getValue()).append("\n");
        }

        //设置消息模板中变量的值
        templateMessage.addData(new WxMpTemplateData("first", submitUser.getName()+"提交了"+template.getName()+"审批申请，请注意查看"));
        templateMessage.addData(new WxMpTemplateData("keyword1", process.getProcessCode(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword2", new DateTime(process.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss"), "#272727"));
        templateMessage.addData(new WxMpTemplateData("content", content.toString(), "#272727"));

        //发送
        try {
            String msg = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }

    }
}
