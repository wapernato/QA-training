package ru.training.qa.api;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.training.qa.item.ItemService;

@RestController
@RequestMapping("/api/v1/items")
public class ItemController {
    private final ItemService items;
    public ItemController(ItemService items) {
        this.items = items;
    }
    @PostMapping public ResponseEntity<ItemResponse> create(Authentication authentication, @Valid @RequestBody CreateItemRequest request) {
        ItemResponse item = items.create(userId(authentication), request);
        return ResponseEntity.created(URI.create("/api/v1/items/" + item.id())).body(item);
    }
    @GetMapping("/{id}")
    public ItemResponse get(@PathVariable UUID id) {
        return items.get(id);
    }
    @PutMapping("/{id}") public ItemResponse update(Authentication authentication, @PathVariable UUID id, @Valid @RequestBody CreateItemRequest request) {
        return items.update(id, userId(authentication), request);
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(Authentication authentication, @PathVariable UUID id) {
        items.delete(id, userId(authentication));
        return ResponseEntity.noContent().build();
    }
    private long userId(Authentication authentication) {
        return (Long) authentication.getPrincipal();
    }
}
