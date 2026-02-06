package com.company.demo.giftcoupon.domain.repository;

import com.company.demo.giftcoupon.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u.fcmToken from User u where u.id = :userId")
    Optional<String> findFcmTokenByUserId(@Param("userId") Long userId);

    @Query("select u.email from User u where u.id = :userId")
    Optional<String> findEmailByUserId(Long userId);
}
