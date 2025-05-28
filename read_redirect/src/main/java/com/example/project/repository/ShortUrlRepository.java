package com.example.project.repository;

import com.example.project.model.ShortUrlEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface ShortUrlRepository extends CassandraRepository<ShortUrlEntity, String> {
    // domyślne metody save(), findById(), itp.
}
