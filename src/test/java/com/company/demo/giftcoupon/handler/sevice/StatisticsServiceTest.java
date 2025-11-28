package com.company.demo.giftcoupon.handler.sevice;

import static org.junit.jupiter.api.Assertions.*;
import com.company.demo.common.response.error.ErrorCode;
import com.company.demo.common.response.exception.BusinessException;
import com.company.demo.giftcoupon.domain.entity.CouponMetadata;
import com.company.demo.giftcoupon.domain.entity.User;
import com.company.demo.giftcoupon.domain.repository.CouponMetadataRepository;
import com.company.demo.giftcoupon.domain.repository.StatisticsRepository;
import com.company.demo.giftcoupon.mapper.dto.StatisticsDto;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StatisticsService 단위 테스트")
class StatisticsServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private CouponMetadataRepository couponMetadataRepository;
    @Mock
    private StatisticsRepository statisticsRepository;

    @InjectMocks
    private StatisticsService statisticsService;

    private static final Long TEST_PROMOTION_ID = 1L;
    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_PUBLISHER_ID = 900L;
    private static final Long TOTAL_CAPACITY = 200L;
    private static final Long COUPON_VALUE = 5000L;
    private static final Long INITIAL_ISSUED_COUNT = 50L;
    private static final Long INITIAL_ESTIMATED_REVENUE = 250000L; // 50 * 5000

    private void setupMocks() {
        // CouponMetadata 설정
        CouponMetadata metadata = mock(CouponMetadata.class);
        when(couponMetadataRepository.findByPromotionId(TEST_PROMOTION_ID))
                .thenReturn(Optional.of(metadata));
        when(metadata.getPublisherId()).thenReturn(TEST_PUBLISHER_ID);
        when(metadata.getValue()).thenReturn(COUPON_VALUE);
        when(metadata.getTotalCapacity()).thenReturn(TOTAL_CAPACITY);

        // User 설정
        User user = mock(User.class);
        when(userService.findById(TEST_USER_ID)).thenReturn(user);
        when(user.getGender()).thenReturn("MALE");
        when(user.getAgeGroup()).thenReturn("30s");

        // StatisticsRepository 조회 설정 (업데이트 후의 값)
        when(statisticsRepository.getIssuedCount(TEST_PROMOTION_ID)).thenReturn(INITIAL_ISSUED_COUNT + 1);
        when(statisticsRepository.getEstimatedRevenue(TEST_PROMOTION_ID)).thenReturn(INITIAL_ESTIMATED_REVENUE + COUPON_VALUE);

        // 통계 분포 설정 (단순화)
        Map<String, Long> genderCounts = Map.of("MALE", 30L, "FEMALE", 21L); // 총 51명 가정
        Map<String, Long> ageCounts = Map.of("20s", 20L, "30s", 31L); // 총 51명 가정
        when(statisticsRepository.getGenderCounts(TEST_PROMOTION_ID)).thenReturn(genderCounts);
        when(statisticsRepository.getAgeGroupCounts(TEST_PROMOTION_ID)).thenReturn(ageCounts);

        // 방문 추이 설정
        when(statisticsRepository.getVisitTrendChange(TEST_PUBLISHER_ID)).thenReturn(0.15); // 15% 증가 가정
    }

    @Test
    @DisplayName("정상 발급 이벤트 처리 시, 모든 통계 지표가 정확히 계산되어야 한다")
    void testProcessIssuedEvent_Success() {
        // GIVEN
        setupMocks();

        // WHEN
        StatisticsDto result = statisticsService.processIssuedEvent(TEST_USER_ID, TEST_PROMOTION_ID);

        // THEN
        // Repository 업데이트 메서드 호출 검증 (핵심 로직 검증)
        verify(statisticsRepository, times(1)).incrementIssuedCount(TEST_PROMOTION_ID);
        verify(statisticsRepository, times(1)).incrementEstimatedRevenue(TEST_PROMOTION_ID, COUPON_VALUE);
        verify(statisticsRepository, times(1)).incrementGenderCount(TEST_PROMOTION_ID, "MALE");
        verify(statisticsRepository, times(1)).incrementAgeGroupCount(TEST_PROMOTION_ID, "30s");

        // 반환된 DTO 값 검증
        assertThat(result.publisherId()).isEqualTo(TEST_PUBLISHER_ID);
        assertThat(result.promotionId()).isEqualTo(TEST_PROMOTION_ID);

        // 발급 개수, 매출, 남은 개수 검증
        assertThat(result.issuedCount()).isEqualTo(INITIAL_ISSUED_COUNT + 1); // 51
        assertThat(result.estimatedRevenue()).isEqualTo(INITIAL_ESTIMATED_REVENUE + COUPON_VALUE); // 255000
        assertThat(result.remainingCount()).isEqualTo(TOTAL_CAPACITY - (INITIAL_ISSUED_COUNT + 1)); // 200 - 51 = 149

        // 분포 비율 검증 (총 51명 기준)
        assertThat(result.genderDistribution().get("MALE")).isCloseTo(30.0 / 51.0, org.assertj.core.data.Percentage.withPercentage(0.01));
        assertThat(result.ageDistribution().get("30s")).isCloseTo(31.0 / 51.0, org.assertj.core.data.Percentage.withPercentage(0.01));

        // 방문 추이 검증
        assertThat(result.visitTrendChange()).isEqualTo(0.15);
    }

    @Test
    @DisplayName("CouponMetadata가 존재하지 않으면 BusinessException을 던진다")
    void testProcessIssuedEvent_MetadataNotFound() {
        // GIVEN
        // CouponMetadataRepository가 Optional.empty()를 반환하도록 설정
        when(couponMetadataRepository.findByPromotionId(TEST_PROMOTION_ID)).thenReturn(Optional.empty());

        // WHEN & THEN
        BusinessException exception = assertThrows(BusinessException.class,
                () -> statisticsService.processIssuedEvent(TEST_USER_ID, TEST_PROMOTION_ID));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_COUPON_ID);

        // 업데이트 관련 로직은 호출되지 않았는지 검증
        verify(statisticsRepository, never()).incrementIssuedCount(any());
    }
}
