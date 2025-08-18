package com.company.demo.common.response;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ApiError {
    private String code;
    private String message;
}
