package com.kaapstyle.wardrobe.repository;

import com.kaapstyle.wardrobe.model.WardrobeTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WardrobeTagRepository extends JpaRepository<WardrobeTag, Long> {
    Optional<WardrobeTag> findByTagName(String tagName);
    List<WardrobeTag> findByTagType(String tagType);
}
