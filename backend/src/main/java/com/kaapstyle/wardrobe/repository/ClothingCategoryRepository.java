package com.kaapstyle.wardrobe.repository;

import com.kaapstyle.wardrobe.model.ClothingCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClothingCategoryRepository extends JpaRepository<ClothingCategory, Long> {
    Optional<ClothingCategory> findByCategoryCode(String categoryCode);
    List<ClothingCategory> findAllByOrderBySortOrderAsc();
}
