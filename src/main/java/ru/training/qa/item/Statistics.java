package ru.training.qa.item;

import jakarta.persistence.Embeddable;

@Embeddable
public class Statistics {
    private long contacts;
    private long likes;
    private long viewCount;
    protected Statistics() { }
    public Statistics(long contacts, long likes, long viewCount) { this.contacts = contacts; this.likes = likes; this.viewCount = viewCount; }
    public long getContacts() { return contacts; }
    public long getLikes() { return likes; }
    public long getViewCount() { return viewCount; }
}
