package com.company.demo.common.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ApiResponse 테스트")
class ApiResponseTest {

    @Test
    @DisplayName("데이터가 있는 성공 응답 생성")
    void 성공_응답_데이터_포함_생성(){
        // given
        String testData = "test data";

        // when
        ApiResponse<String> response = ApiResponse.success(testData);

        // then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isEqualTo(testData);
        assertThat(response.getError()).isNull();
        assertThat(response.getServerDateTime()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("데이터가 없는 성공 응답 생성")
    void 성공_응답_데이터_없음_생성(){
        // when
        ApiResponse<Object> response = ApiResponse.success();

        // then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isNull();
        assertThat(response.getError()).isNull();
        assertThat(response.getServerDateTime()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("에러 코드와 메시지로 실패 응답 생성")
    void 실패_응답_코드_메시지_생성() {
        // given
        String errorCode = "C004";
        String errorMessage = "Invalid Input";

        // when
        ApiResponse<Object> response = ApiResponse.error(errorCode, errorMessage);

        // then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getData()).isNull();
        assertThat(response.getError()).isNotNull();
        assertThat(response.getError().getCode()).isEqualTo(errorCode);
        assertThat(response.getError().getMessage()).isEqualTo(errorMessage);
        assertThat(response.getServerDateTime()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("ApiError 객체로 실패 응답 생성")
    void 실패_응답_에러객체_생성() {
        // given
        ApiError apiError = new ApiError("C005", "Method not allowed");

        // when
        ApiResponse<Object> response = ApiResponse.error(apiError);

        // then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getData()).isNull();
        assertThat(response.getError()).isEqualTo(apiError);
        assertThat(response.getServerDateTime()).isBeforeOrEqualTo(LocalDateTime.now());
    }

}