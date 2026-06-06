package com.example.basketball.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class DynamicRequest {

    @NotBlank(message = "内容不能为空")
    private String content;

    private List<String> imageUrls;

    private List<String> videoUrls;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public List<String> getVideoUrls() {
        return videoUrls;
    }

    public void setVideoUrls(List<String> videoUrls) {
        this.videoUrls = videoUrls;
    }
}
