package com.kaapstyle.security.repository;

import com.kaapstyle.security.model.IpRateLimitRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IpRateLimitRecordRepository extends JpaRepository<IpRateLimitRecord, Long> {

    Optional<IpRateLimitRecord> findByIpAddressAndEndpointGroup(String ipAddress, String endpointGroup);
}