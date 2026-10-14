package com.kaapstyle.weather.controller;

import com.kaapstyle.weather.model.UserWeatherCache;
import com.kaapstyle.weather.repository.UserWeatherCacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/weather")
public class WeatherSyncController {

    private final UserWeatherCacheRepository weatherRepo;

    @Autowired
    public WeatherSyncController(UserWeatherCacheRepository weatherRepo) {
        this.weatherRepo = weatherRepo;
    }

    @PostMapping("/cache")
    public ResponseEntity<UserWeatherCache> cacheWeather(@RequestBody UserWeatherCache cache) {
        return ResponseEntity.ok(weatherRepo.save(cache));
    }

    @GetMapping("/user/{userId}/latest")
    public ResponseEntity<UserWeatherCache> getLatestWeather(@PathVariable Long userId) {
        return weatherRepo.findTopByUserIdOrderByFetchedAtDesc(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
