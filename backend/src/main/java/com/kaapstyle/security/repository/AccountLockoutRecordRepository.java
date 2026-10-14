package com.kaapstyle.security.repository;

import com.kaapstyle.security.model.AccountLockoutRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountLockoutRecordRepository extends JpaRepository<AccountLockoutRecord, Long> {
}