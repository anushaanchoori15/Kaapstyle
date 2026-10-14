package com.kaapstyle.outfit.repository;

import com.kaapstyle.outfit.model.ColorRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColorRuleRepository extends JpaRepository<ColorRule, Long> {
    List<ColorRule> findByRuleType(String ruleType);
}
