package com.kaapstyle.travel.repository;

import com.kaapstyle.travel.model.TravelTrip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TravelTripRepository extends JpaRepository<TravelTrip, Long> {
    List<TravelTrip> findByUserIdOrderByStartDateAsc(Long userId);
}
