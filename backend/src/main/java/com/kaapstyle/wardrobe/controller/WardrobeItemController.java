package com.kaapstyle.wardrobe.controller;

import com.kaapstyle.wardrobe.model.WardrobeItem;
import com.kaapstyle.wardrobe.repository.WardrobeItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wardrobe/items")
public class WardrobeItemController {

    private final WardrobeItemRepository itemRepository;

    @Autowired
    public WardrobeItemController(WardrobeItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @PostMapping
    public ResponseEntity<WardrobeItem> createItem(@RequestBody WardrobeItem item) {
        return ResponseEntity.ok(itemRepository.save(item));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WardrobeItem>> getItemsByUser(
            @PathVariable Long userId,
            @RequestParam(required = false) Long categoryId) {
        if (categoryId != null) {
            return ResponseEntity.ok(itemRepository.findByUserIdAndCategoryId(userId, categoryId));
        }
        return ResponseEntity.ok(itemRepository.findByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WardrobeItem> getItemById(@PathVariable Long id) {
        return itemRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/favorite")
    public ResponseEntity<WardrobeItem> toggleFavorite(@PathVariable Long id) {
        return itemRepository.findById(id).map(item -> {
            item.setIsFavorite(!Boolean.TRUE.equals(item.getIsFavorite()));
            return ResponseEntity.ok(itemRepository.save(item));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        if (itemRepository.existsById(id)) {
            itemRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
