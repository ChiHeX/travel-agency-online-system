package com.travelagency.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.travelagency.common.api.ApiResponse;
import com.travelagency.common.exception.BusinessException;
import com.travelagency.common.security.CurrentUser;
import com.travelagency.domain.entity.Message;
import com.travelagency.domain.mapper.MessageMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageMapper messageMapper;

    public MessageController(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    @GetMapping
    public ApiResponse<List<Message>> list() {
        return ApiResponse.ok(messageMapper.selectList(new QueryWrapper<Message>()
                .eq("user_id", CurrentUser.required().userId()).orderByDesc("created_at")));
    }

    @GetMapping("/unread-count")
    public ApiResponse<Long> unreadCount() {
        return ApiResponse.ok(messageMapper.selectCount(new QueryWrapper<Message>()
                .eq("user_id", CurrentUser.required().userId()).eq("read_flag", 0)));
    }

    @PostMapping("/{id}/read")
    public ApiResponse<Void> read(@PathVariable Long id) {
        Message message = messageMapper.selectOne(new QueryWrapper<Message>()
                .eq("id", id).eq("user_id", CurrentUser.required().userId()));
        if (message == null) {
            throw new BusinessException(404, "消息不存在");
        }
        message.readFlag = 1;
        message.readAt = LocalDateTime.now();
        messageMapper.updateById(message);
        return ApiResponse.ok();
    }
}
