package com.company.demo.common.response.error;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;


import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ErrorCode 열거형 테스트")
class ErrorCodeTest {

    @DisplayName("에러코드의 속성값이 올바른지 확인")
    @ParameterizedTest(name = "{0} - status: {1}, code: {2}")
    @MethodSource("errorCodeTestData")
    void 에러코드_속성값_검증(ErrorCode errorCode, int expectedStatus, String expectedCode, String expectedMessage){
        // then
        assertThat(errorCode.getStatus()).isEqualTo(expectedStatus);
        assertThat(errorCode.getCode()).isEqualTo(expectedCode);
        assertThat(errorCode.getMessage()).isEqualTo(expectedMessage);
    }

    // error 열거형을 추가할때마다 아래에 더하면 됨
    static Stream<Arguments> errorCodeTestData(){
        return Stream.of(
                Arguments.of(ErrorCode.UNEXPECTED, -1, "-1", "Unexpected exception occurred"),
                Arguments.of(ErrorCode.INVALID_INPUT_VALUE, 400, "C004", "Invalid Input Value"),
                Arguments.of(ErrorCode.METHOD_NOT_ALLOWED, 405, "C005", "Method not allowed")
        );
    }

    @Test
    @DisplayName("API 에러코드 스펙의 안전성 검증")
    void 에러코드_완전성_검증() {
        // given & when
        ErrorCode[] errorCodes = ErrorCode.values();

        // then
        assertThat(errorCodes).hasSize(3);
        assertThat(errorCodes).contains(
                ErrorCode.UNEXPECTED,
                ErrorCode.INVALID_INPUT_VALUE,
                ErrorCode.METHOD_NOT_ALLOWED
        );
    }
}