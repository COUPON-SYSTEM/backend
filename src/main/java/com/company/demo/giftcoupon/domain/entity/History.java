package com.company.demo.giftcoupon.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id", nullable = false)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "issuedAt", nullable = true)
    private LocalDateTime issuedAt;

    public History(Long eventId, Long userId, LocalDateTime issuedAt) {
        this.eventId = eventId;
        this.userId = userId;
        this.issuedAt = issuedAt;
    }
}
