package ru.training.qa.item;

import java.util.UUID;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import ru.training.qa.user.User;

@Entity
@Table(name = "items")
public class Item {
    @Id @UuidGenerator private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "owner_id")
    private User owner;
    @Column(nullable = false, length = 120) private String name;
    @Column(nullable = false) private long price;
    @Embedded private Statistics statistics;
    protected Item() { }
    public Item(User owner, String name, long price, Statistics statistics) { this.owner = owner; this.name = name; this.price = price; this.statistics = statistics; }
    public UUID getId() { return id; }
    public User getOwner() { return owner; }
    public String getName() { return name; }
    public long getPrice() { return price; }
    public Statistics getStatistics() { return statistics; }
    public void update(String name, long price, Statistics statistics) { this.name = name; this.price = price; this.statistics = statistics; }
}
