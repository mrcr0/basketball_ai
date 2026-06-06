package com.example.basketball.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.basketball.service.LogService;
import com.example.basketball.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OperationLogInterceptor implements HandlerInterceptor {

    @Autowired
    private LogService logService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (handler instanceof HandlerMethod) {
            try {
                Long userId = null;
                String username = "anonymous";
                
                String token = request.getHeader("Authorization");
                if (token != null && token.startsWith("Bearer ")) {
                    token = token.substring(7);
                    try {
                        userId = jwtUtil.getUserIdFromToken(token);
                        username = jwtUtil.getUsernameFromToken(token);
                    } catch (Exception e) {
                        // Token解析失败，保持默认值
                    }
                }

                String requestUrl = request.getRequestURI();
                String requestMethod = request.getMethod();
                String operationType = getOperationType(requestMethod);
                String module = getModule(requestUrl);
                String desc = generateDescription(requestMethod, requestUrl);
                String ip = getClientIp(request);

                logService.recordOperationLog(
                    userId,
                    username,
                    operationType,
                    module,
                    desc,
                    requestUrl,
                    requestMethod,
                    null,
                    response.getStatus(),
                    null,
                    ip
                );
            } catch (Exception e) {
                // 日志记录异常不影响业务
            }
        }
    }

    private String getOperationType(String method) {
        return switch (method.toUpperCase()) {
            case "POST" -> "新增";
            case "PUT" -> "修改";
            case "DELETE" -> "删除";
            case "GET" -> "查询";
            default -> "其他";
        };
    }

    private String getModule(String url) {
        if (url.contains("/user")) return "用户管理";
        if (url.contains("/plan")) return "训练计划";
        if (url.contains("/action")) return "动作库";
        if (url.contains("/record")) return "训练记录";
        if (url.contains("/tactic")) return "战术管理";
        if (url.contains("/dynamic")) return "社区动态";
        if (url.contains("/match")) return "比赛管理";
        if (url.contains("/admin")) return "系统管理";
        if (url.contains("/ai")) return "AI服务";
        return "其他";
    }

    private String generateDescription(String method, String url) {
        return method + " " + url;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}