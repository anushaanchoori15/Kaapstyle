package com.kaapstyle.security.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Tracks request counts per IP address and endpoint group within a rate limit window.
 */
@Entity
@Table(name = "ip_rate_limit_records")
public class IpRateLimitRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @Column(name = "endpoint_group", nullable = false)
    private String endpointGroup;

    @Column(name = "request_count", nullable = false)
    private int requestCount;

    @Column(name = "window_start", nullable = false)
    private Instant windowStart;

    @Column(name = "blocked_until")
    private Instant blockedUntil;

    public IpRateLimitRecord() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getEndpointGroup() {
        return endpointGroup;
    }

    public void setEndpointGroup(String endpointGroup) {
        this.endpointGroup = endpointGroup;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }

    public Instant getWindowStart() {
        return windowStart;
    }

    public void setWindowStart(Instant windowStart) {
        this.windowStart = windowStart;
    }

    public Instant getBlockedUntil() {
        return blockedUntil;
    }

    public void setBlockedUntil(Instant blockedUntil) {
        this.blockedUntil = blockedUntil;
    }
}