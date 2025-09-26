package com.company.demo.giftcoupon.domain.repository;

import com.company.demo.giftcoupon.domain.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {
}
