package com.example.basketball.dto.response;

public class ResetTokenResponse {

    private Boolean valid;
    private String message;

    public ResetTokenResponse() {
    }

    public ResetTokenResponse(Boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static ResetTokenResponse valid() {
        return new ResetTokenResponse(true, "Token有效");
    }

    public static ResetTokenResponse invalid(String message) {
        return new ResetTokenResponse(false, message);
    }
}