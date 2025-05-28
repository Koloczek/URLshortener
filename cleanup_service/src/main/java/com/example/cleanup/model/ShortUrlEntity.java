package com.example.cleanup.model;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Encja reprezentująca skrócony URL wraz z czasem utworzenia,
 * wygaśnięcia i ostatniego dostępu.
 */
@Table("short_url_entity")
public class ShortUrlEntity {

    @PrimaryKey("short_key")
    private String shortKey;

    @Column("original_url")
    private String originalUrl;

    @Column("creation_time")
    private long creationTime;

    @Column("expiration_time")
    private long expirationTime;

    @Column("last_access_time")
    private long lastAccessTime;

    public ShortUrlEntity() {
        // Default constructor
    }

    public ShortUrlEntity(String shortKey, String originalUrl, long creationTime, long expirationTime, long lastAccessTime) {
        this.shortKey = shortKey;
        this.originalUrl = originalUrl;
        this.creationTime = creationTime;
        this.expirationTime = expirationTime;
        this.lastAccessTime = lastAccessTime;
    }

    public String getShortKey() {
        return shortKey;
    }

    public void setShortKey(String shortKey) {
        this.shortKey = shortKey;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    @Override
    public String toString() {
        return "ShortUrlEntity{" +
                "shortKey='" + shortKey + '\'' +
                ", originalUrl='" + originalUrl + '\'' +
                ", creationTime=" + creationTime +
                ", expirationTime=" + expirationTime +
                ", lastAccessTime=" + lastAccessTime +
                '}';
    }
}
