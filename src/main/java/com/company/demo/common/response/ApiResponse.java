package com.company.demo.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;
import java.time.LocalDateTime;

@ToString(of = "success, data, error")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private ApiError error;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime serverDateTime = LocalDateTime.now();

    private ApiResponse(boolean success, T data, ApiError error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static ApiResponse<Object> success() {
        return new ApiResponse<>(true, null, null);
    }

    public static ApiResponse<Object> error(String code, String message) {
        return new ApiResponse<>(false, null, new ApiError(code, message));
    }

    public static ApiResponse<Object> error(ApiError error) {
        return new ApiResponse<>(false, null, error);
    }

}

