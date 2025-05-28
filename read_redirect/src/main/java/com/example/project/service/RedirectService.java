package com.example.project.service;

import com.example.project.model.ShortUrlEntity;
import com.example.project.repository.ShortUrlRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RedirectService {

    private final ShortUrlRepository repo;

    public RedirectService(ShortUrlRepository repo) {
        this.repo = repo;
    }

    public ShortUrlEntity createMapping(String originalUrl) {
        String shortId = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8);
        ShortUrlEntity mapping = new ShortUrlEntity(shortId, originalUrl, Instant.now());
        return repo.save(mapping);
    }

    public Optional<ShortUrlEntity> lookup(String id) {
        return repo.findById(id);
    }
}