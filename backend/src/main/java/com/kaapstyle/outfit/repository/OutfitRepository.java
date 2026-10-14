package com.kaapstyle.outfit.repository;

import com.kaapstyle.outfit.model.Outfit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutfitRepository extends JpaRepository<Outfit, Long> {
    List<Outfit> findByUserId(Long userId);
    List<Outfit> findByUserIdAndOccasion(Long userId, String occasion);
    List<Outfit> findByUserIdAndIsFavoriteTrue(Long userId);
}
