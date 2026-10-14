package com.kaapstyle.wardrobe.repository;

import com.kaapstyle.wardrobe.model.WardrobeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WardrobeItemRepository extends JpaRepository<WardrobeItem, Long> {
    List<WardrobeItem> findByUserId(Long userId);
    List<WardrobeItem> findByUserIdAndCategoryId(Long userId, Long categoryId);
    List<WardrobeItem> findByUserIdAndIsFavoriteTrue(Long userId);
}
