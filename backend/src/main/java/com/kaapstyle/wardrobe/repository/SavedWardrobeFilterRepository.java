package com.kaapstyle.wardrobe.repository;

import com.kaapstyle.wardrobe.model.SavedWardrobeFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedWardrobeFilterRepository extends JpaRepository<SavedWardrobeFilter, Long> {
    List<SavedWardrobeFilter> findByUserId(Long userId);
}
