package com.kaapstyle.outfit.controller;

import com.kaapstyle.outfit.model.ColorRule;
import com.kaapstyle.outfit.repository.ColorRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/outfits/color-rules")
public class ColorRuleController {

    private final ColorRuleRepository colorRuleRepository;

    @Autowired
    public ColorRuleController(ColorRuleRepository colorRuleRepository) {
        this.colorRuleRepository = colorRuleRepository;
    }

    @GetMapping
    public ResponseEntity<List<ColorRule>> getRules(@RequestParam(required = false) String ruleType) {
        if (ruleType != null) {
            return ResponseEntity.ok(colorRuleRepository.findByRuleType(ruleType));
        }
        return ResponseEntity.ok(colorRuleRepository.findAll());
    }
}
