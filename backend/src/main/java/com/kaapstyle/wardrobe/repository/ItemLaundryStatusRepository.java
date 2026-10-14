package com.kaapstyle.wardrobe.repository;

import com.kaapstyle.wardrobe.model.ItemLaundryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemLaundryStatusRepository extends JpaRepository<ItemLaundryStatus, Long> {
    List<ItemLaundryStatus> findByUserIdAndStatus(Long userId, String status);
}
