package com.kaapstyle.wardrobe.repository;

import com.kaapstyle.wardrobe.model.CapsuleWardrobe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CapsuleWardrobeRepository extends JpaRepository<CapsuleWardrobe, Long> {
    List<CapsuleWardrobe> findByUserId(Long userId);
    List<CapsuleWardrobe> findByUserIdAndSeason(Long userId, String season);
}
