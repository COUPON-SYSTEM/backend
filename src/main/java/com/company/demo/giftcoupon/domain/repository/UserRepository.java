package com.company.demo.giftcoupon.domain.repository;

import com.company.demo.giftcoupon.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
