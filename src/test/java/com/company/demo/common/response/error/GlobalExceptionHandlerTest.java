package com.company.demo.common.response.error;

import com.company.demo.common.response.ApiResponse;
import com.company.demo.common.response.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler 테스트")
class GlobalExceptionHandlerTest {
    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    @DisplayName("BusinessException 처리 시 올바른 응답을 반환")
    void 비즈니스_예외_처리_테스트() {
        // given
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        BusinessException exception = new BusinessException(errorCode);

        // when
        ResponseEntity<ApiResponse<?>> response = globalExceptionHandler.handleBusinessException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getData()).isNull();
        assertThat(response.getBody().getError()).isNotNull();
        assertThat(response.getBody().getError().getCode()).isEqualTo(errorCode.getCode());
        assertThat(response.getBody().getError().getMessage()).isEqualTo(errorCode.getMessage());
    }

    // TODO: 여러 종류의 BusinessException을 처리할지 고민

}