package com.kaapstyle.weather.repository;

import com.kaapstyle.weather.model.UserWeatherCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserWeatherCacheRepository extends JpaRepository<UserWeatherCache, Long> {
    Optional<UserWeatherCache> findTopByUserIdOrderByFetchedAtDesc(Long userId);
}
