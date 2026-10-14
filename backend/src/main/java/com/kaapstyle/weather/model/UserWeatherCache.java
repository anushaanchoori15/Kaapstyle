package com.kaapstyle.weather.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_weather_caches")
public class UserWeatherCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "city_name", nullable = false, length = 100)
    private String cityName;

    @Column(name = "country_code", length = 10)
    private String countryCode;

    @Column(name = "temperature_celsius", nullable = false, precision = 4, scale = 1)
    private BigDecimal temperatureCelsius;

    @Column(name = "weather_condition", nullable = false, length = 50)
    private String weatherCondition;

    @Column(name = "precipitation_chance", nullable = false)
    private Integer precipitationChance = 0;

    @Column(name = "humidity_percent", nullable = false)
    private Integer humidityPercent = 50;

    @CreationTimestamp
    @Column(name = "fetched_at", nullable = false, updatable = false)
    private LocalDateTime fetchedAt;

    public UserWeatherCache() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public BigDecimal getTemperatureCelsius() { return temperatureCelsius; }
    public void setTemperatureCelsius(BigDecimal temperatureCelsius) { this.temperatureCelsius = temperatureCelsius; }

    public String getWeatherCondition() { return weatherCondition; }
    public void setWeatherCondition(String weatherCondition) { this.weatherCondition = weatherCondition; }

    public Integer getPrecipitationChance() { return precipitationChance; }
    public void setPrecipitationChance(Integer precipitationChance) { this.precipitationChance = precipitationChance; }

    public Integer getHumidityPercent() { return humidityPercent; }
    public void setHumidityPercent(Integer humidityPercent) { this.humidityPercent = humidityPercent; }

    public LocalDateTime getFetchedAt() { return fetchedAt; }
    public void setFetchedAt(LocalDateTime fetchedAt) { this.fetchedAt = fetchedAt; }
}
