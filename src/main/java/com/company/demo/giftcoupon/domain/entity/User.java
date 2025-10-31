package com.company.demo.giftcoupon.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(name = "user_name", length = 30, nullable = false)
    private String userName;

    @Column(name = "user_fcm_token", length = 30, nullable = false)
    private String fcmToken;

    @Column(name = "user_email", length = 30, nullable = true)
    private String email;
}
