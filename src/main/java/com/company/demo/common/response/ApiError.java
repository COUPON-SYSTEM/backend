package com.company.demo.common.response;

import lombok.*;
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ApiError {
    private String code;
    private String message;
}
