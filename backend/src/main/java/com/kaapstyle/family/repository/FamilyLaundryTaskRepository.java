package com.kaapstyle.family.repository;

import com.kaapstyle.family.model.FamilyLaundryTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FamilyLaundryTaskRepository extends JpaRepository<FamilyLaundryTask, Long> {
    List<FamilyLaundryTask> findByFamilyGroupId(Long familyGroupId);
    List<FamilyLaundryTask> findByAssignedUserIdAndTaskStatus(Long assignedUserId, String taskStatus);
}
