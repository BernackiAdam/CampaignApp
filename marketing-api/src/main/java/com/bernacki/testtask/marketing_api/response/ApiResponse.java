package com.bernacki.testtask.marketing_api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private ApiError error;

    public ApiResponse(boolean success, ApiError error) {
        this.success = success;
        this.error = error;
    }

    public ApiResponse(boolean success) {
        this.success = success;
    }
}
