package com.kaapstyle.wardrobe.controller;

import com.kaapstyle.wardrobe.model.WardrobeTag;
import com.kaapstyle.wardrobe.repository.WardrobeTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wardrobe/tags")
public class WardrobeTagController {

    private final WardrobeTagRepository tagRepository;

    @Autowired
    public WardrobeTagController(WardrobeTagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @GetMapping
    public ResponseEntity<List<WardrobeTag>> getAllTags(@RequestParam(required = false) String tagType) {
        if (tagType != null) {
            return ResponseEntity.ok(tagRepository.findByTagType(tagType));
        }
        return ResponseEntity.ok(tagRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<WardrobeTag> createTag(@RequestBody WardrobeTag tag) {
        return ResponseEntity.ok(tagRepository.save(tag));
    }
}
