package com.guigu.serviceoa.service;


public interface MessageService {

    /**
     * 推送消息
     * @param processId
     * @param userId
     * @param taskId
     */
    void sendMessage(Long processId, Long userId,String taskId);
}
