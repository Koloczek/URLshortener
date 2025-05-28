package com.example.project.controller;

import com.example.project.model.ShortUrlEntity;
import com.example.project.service.RedirectService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/links")
public class RedirectController {

    private final RedirectService service;

    public RedirectController(RedirectService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<LinkResponse> create(@RequestBody LinkRequest request) {
        ShortUrlEntity mapping = service.createMapping(request.getUrl());
        String shortPath = "/l/" + mapping.getId();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new LinkResponse(shortPath));
    }

    @GetMapping("/l/{id}")
    public ResponseEntity<Void> redirect(@PathVariable String id) {
        Optional<ShortUrlEntity> maybe = service.lookup(id);
        if (maybe.isPresent()) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", maybe.get().getOriginalUrl());
            return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
        }
        return ResponseEntity.notFound().build();
    }

    public static class LinkRequest {
        private String url;
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
    }

    public static class LinkResponse {
        private String shortUrl;
        public LinkResponse(String shortUrl) { this.shortUrl = shortUrl; }
        public String getShortUrl() { return shortUrl; }
        public void setShortUrl(String shortUrl) { this.shortUrl = shortUrl; }
    }
}