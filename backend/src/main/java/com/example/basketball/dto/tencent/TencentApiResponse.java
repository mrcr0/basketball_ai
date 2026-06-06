package com.example.basketball.dto.tencent;

import java.util.List;

/**
 * 腾讯体育API统一响应体
 */
public class TencentApiResponse<T> {

    /** 响应状态码：0-成功，非0-失败 */
    private int code;

    /** 响应消息 */
    private String message;

    /** 响应数据 */
    private T data;

    /** 请求ID（用于追踪） */
    private String requestId;

    /** 服务器时间戳 */
    private long timestamp;

    public boolean isSuccess() {
        return code == 0;
    }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
