package com.kaapstyle.wardrobe.controller;

import com.kaapstyle.wardrobe.model.ClothingCategory;
import com.kaapstyle.wardrobe.repository.ClothingCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wardrobe/categories")
public class ClothingCategoryController {

    private final ClothingCategoryRepository categoryRepository;

    @Autowired
    public ClothingCategoryController(ClothingCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public ResponseEntity<List<ClothingCategory>> getCategories() {
        return ResponseEntity.ok(categoryRepository.findAllByOrderBySortOrderAsc());
    }

    @GetMapping("/{code}")
    public ResponseEntity<ClothingCategory> getCategoryByCode(@PathVariable String code) {
        return categoryRepository.findByCategoryCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
