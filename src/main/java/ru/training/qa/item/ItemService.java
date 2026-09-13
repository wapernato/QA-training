package ru.training.qa.item;

import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.training.qa.api.CreateItemRequest;
import ru.training.qa.api.ItemResponse;
import ru.training.qa.api.StatisticsRequest;
import ru.training.qa.exception.NotFoundException;
import ru.training.qa.user.User;
import ru.training.qa.user.UserRepository;

@Service
public class ItemService {
    private final ItemRepository items; private final UserRepository users;
    public ItemService(ItemRepository items, UserRepository users) { this.items = items; this.users = users; }
    @Transactional public ItemResponse create(long userId, CreateItemRequest request) {
        User owner = users.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        return toResponse(items.save(new Item(owner, request.name(), request.price(), toStatistics(request.statistics()))));
    }
    @Transactional(readOnly = true) public ItemResponse get(UUID id) { return toResponse(find(id)); }
    @Transactional public ItemResponse update(UUID id, long userId, CreateItemRequest request) {
        Item item = ownedItem(id, userId); item.update(request.name(), request.price(), toStatistics(request.statistics())); return toResponse(item);
    }
    @Transactional public void delete(UUID id, long userId) { items.delete(ownedItem(id, userId)); }
    private Item ownedItem(UUID id, long userId) {
        Item item = find(id);
        if (!item.getOwner().getId().equals(userId)) throw new NotFoundException("Item not found");
        return item;
    }
    private Item find(UUID id) { return items.findById(id).orElseThrow(() -> new NotFoundException("Item not found")); }
    private Statistics toStatistics(StatisticsRequest stats) { return new Statistics(stats.contacts(), stats.likes(), stats.viewCount()); }
    private ItemResponse toResponse(Item item) {
        Statistics s = item.getStatistics();
        return new ItemResponse(item.getId(), item.getOwner().getId(), item.getName(), item.getPrice(), new StatisticsRequest(s.getContacts(), s.getLikes(), s.getViewCount()));
    }
}
