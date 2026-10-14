package com.kaapstyle.wardrobe.repository;

import com.kaapstyle.wardrobe.model.DryCleaningOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DryCleaningOrderRepository extends JpaRepository<DryCleaningOrder, Long> {
    List<DryCleaningOrder> findByUserIdOrderByDropoffDateDesc(Long userId);
    List<DryCleaningOrder> findByUserIdAndOrderStatus(Long userId, String orderStatus);
}
