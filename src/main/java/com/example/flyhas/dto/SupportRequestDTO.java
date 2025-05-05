
package com.example.flyhas.dto;

import java.time.LocalDateTime;

public class SupportRequestDTO {
    private Long id;
    private String errorCode;
    private String message;
    private String screenshot;
    private String managerResponse;
    private String status;
    private String customerEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SupportRequestDTO(
            Long id,
            String errorCode,
            String message,
            String screenshot,
            String managerResponse,
            String status,
            String customerEmail,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.errorCode = errorCode;
        this.message = message;
        this.screenshot = screenshot;
        this.managerResponse = managerResponse;
        this.status = status;
        this.customerEmail = customerEmail;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public String getScreenshot() {
        return screenshot;
    }

    public String getManagerResponse() {
        return managerResponse;
    }

    public String getStatus() {
        return status;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
