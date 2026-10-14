package com.kaapstyle.travel.repository;

import com.kaapstyle.travel.model.LuggageContainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LuggageContainerRepository extends JpaRepository<LuggageContainer, Long> {
    List<LuggageContainer> findByTripId(Long tripId);
}
