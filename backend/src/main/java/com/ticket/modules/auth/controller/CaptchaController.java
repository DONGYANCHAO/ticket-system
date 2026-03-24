package com.ticket.modules.auth.controller;

import com.ticket.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 验证码控制器
 *
 * @author Ticket System
 */
@Tag(name = "验证码")
@RestController
@RequestMapping("/api/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    /**
     * 获取图片验证码
     */
    @Operation(summary = "获取图片验证码")
    @GetMapping("/image")
    public Result<Map<String, String>> getImageCaptcha() {
        // 简单的验证码实现，实际生产环境应使用更复杂的方案
        String code = String.format("%04d", new Random().nextInt(10000));
        String uuid = java.util.UUID.randomUUID().toString();

        // 实际生产环境应生成图片并存储到Redis
        Map<String, String> result = new HashMap<>();
        result.put("uuid", uuid);
        result.put("code", code); // 开发环境返回code，生产环境应删除

        return Result.success(result);
    }

    /**
     * 验证验证码
     */
    @Operation(summary = "验证验证码")
    @GetMapping("/verify")
    public Result<Boolean> verifyCaptcha(String uuid, String code) {
        // TODO: 从Redis获取并验证
        return Result.success(true);
    }
}
