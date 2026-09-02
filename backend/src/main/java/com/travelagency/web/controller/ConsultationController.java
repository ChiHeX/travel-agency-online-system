package com.travelagency.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.travelagency.common.api.ApiResponse;
import com.travelagency.common.exception.BusinessException;
import com.travelagency.common.security.CurrentUser;
import com.travelagency.domain.dto.ConsultationReplyRequest;
import com.travelagency.domain.dto.ConsultationRequest;
import com.travelagency.domain.entity.Consultation;
import com.travelagency.domain.entity.ConsultationReply;
import com.travelagency.domain.mapper.ConsultationMapper;
import com.travelagency.domain.mapper.ConsultationReplyMapper;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    private final ConsultationMapper consultationMapper;
    private final ConsultationReplyMapper replyMapper;

    public ConsultationController(ConsultationMapper consultationMapper, ConsultationReplyMapper replyMapper) {
        this.consultationMapper = consultationMapper;
        this.replyMapper = replyMapper;
    }

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> mine() {
        Long userId = CurrentUser.required().userId();
        return ApiResponse.ok(consultationMapper.selectList(new QueryWrapper<Consultation>()
                        .eq("user_id", userId).orderByDesc("created_at"))
                .stream().map(this::withReply).toList());
    }

    @PostMapping
    public ApiResponse<Consultation> create(@Valid @RequestBody ConsultationRequest request) {
        Consultation consultation = new Consultation();
        consultation.userId = CurrentUser.required().userId();
        consultation.title = request.title();
        consultation.content = request.content();
        consultation.status = "WAIT_REPLY";
        consultationMapper.insert(consultation);
        return ApiResponse.ok(consultation);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<Consultation>> all() {
        return ApiResponse.ok(consultationMapper.selectList(new QueryWrapper<Consultation>().orderByDesc("created_at")));
    }

    @PostMapping("/{id}/reply")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<ConsultationReply> reply(
            @PathVariable Long id, @Valid @RequestBody ConsultationReplyRequest request) {
        Consultation consultation = consultationMapper.selectById(id);
        if (consultation == null) {
            throw new BusinessException(404, "咨询不存在");
        }
        ConsultationReply reply = new ConsultationReply();
        reply.consultationId = id;
        reply.staffId = CurrentUser.required().userId();
        reply.content = request.content();
        replyMapper.insert(reply);
        consultation.status = "REPLIED";
        consultationMapper.updateById(consultation);
        return ApiResponse.ok(reply);
    }

    private Map<String, Object> withReply(Consultation consultation) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("consultation", consultation);
        result.put("replies", replyMapper.selectList(new QueryWrapper<ConsultationReply>()
                .eq("consultation_id", consultation.id).orderByAsc("created_at")));
        return result;
    }
}
