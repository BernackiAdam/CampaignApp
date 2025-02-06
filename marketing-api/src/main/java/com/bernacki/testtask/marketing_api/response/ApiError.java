package com.bernacki.testtask.marketing_api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ApiError {
    private int status;
    private String message;
    private long timestamp;
}
