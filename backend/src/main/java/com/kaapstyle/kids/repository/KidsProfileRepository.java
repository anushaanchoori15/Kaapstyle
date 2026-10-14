package com.kaapstyle.kids.repository;

import com.kaapstyle.kids.model.KidsProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KidsProfileRepository extends JpaRepository<KidsProfile, Long> {
    List<KidsProfile> findByGuardianId(Long guardianId);
}
