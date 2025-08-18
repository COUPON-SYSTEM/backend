package com.company.demo.common.response.exception;

import com.company.demo.common.response.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BusinessException 테스트")
class BusinessExceptionTest {

    @Test
    @DisplayName("ErrorCode로 BusinessException 생성 시 에러코드와 메시지가 올바르게 설정됨")
    void 비즈니스_예외_생성_테스트(){
        // given
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;

        // when
        BusinessException exception = new BusinessException(ErrorCode.INVALID_INPUT_VALUE);

        // then
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getMessage()).isEqualTo(errorCode.getMessage());
    }

    @Test
    @DisplayName("RuntimeException을 상속받아 UnCheck 예외로 동작함")
    void 런타임_예외_상속_확인(){
        // given
        BusinessException exception = new BusinessException(ErrorCode.UNEXPECTED);

        // then
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}