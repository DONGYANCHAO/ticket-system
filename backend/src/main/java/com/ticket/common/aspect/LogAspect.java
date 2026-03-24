package com.ticket.common.aspect;

import com.ticket.common.annotation.Log;
import com.ticket.common.util.SecurityUtils;
import com.ticket.modules.system.entity.OperationLog;
import com.ticket.modules.system.mapper.OperationLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * 操作日志切面
 *
 * @author Ticket System
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final OperationLogMapper operationLogMapper;

    @Around("@annotation(com.ticket.common.annotation.Log)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = null;
        Throwable error = null;

        try {
            result = point.proceed();
            return result;
        } catch (Throwable e) {
            error = e;
            throw e;
        } finally {
            try {
                saveLog(point, result, error, System.currentTimeMillis() - startTime);
            } catch (Exception e) {
                log.error("保存操作日志失败", e);
            }
        }
    }

    private void saveLog(ProceedingJoinPoint point, Object result, Throwable error, long duration) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Log logAnnotation = method.getAnnotation(Log.class);

        OperationLog operationLog = new OperationLog();
        operationLog.setModule(logAnnotation.module());
        operationLog.setContent(logAnnotation.content());

        // 获取当前用户信息
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId != null) {
            operationLog.setOperatorId(userId);
            operationLog.setOperatorName(SecurityUtils.getCurrentUsername());
        }

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            operationLog.setIp(request.getRemoteAddr());
            operationLog.setMethod(request.getMethod());
            operationLog.setUrl(request.getRequestURI());
        }

        // 获取类名和方法名
        String className = point.getTarget().getClass().getName();
        String methodName = method.getName();
        operationLog.setOperation(className + "." + methodName);

        // 设置执行结果
        operationLog.setDuration((int) duration);
        if (error != null) {
            operationLog.setResult("FAIL: " + error.getMessage());
        } else {
            operationLog.setResult("SUCCESS");
        }

        operationLogMapper.insert(operationLog);
    }
}
