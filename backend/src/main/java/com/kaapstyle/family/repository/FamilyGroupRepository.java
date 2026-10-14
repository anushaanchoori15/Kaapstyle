package com.kaapstyle.family.repository;

import com.kaapstyle.family.model.FamilyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FamilyGroupRepository extends JpaRepository<FamilyGroup, Long> {
    List<FamilyGroup> findByOwnerUserId(Long ownerUserId);
}
