package com.example.project.config;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Configuration
public class CassandraSettings {

    @Value("${cassandra.nodes:127.0.0.1}")
    private String contactPoint;

    @Value("${cassandra.port:9042}")
    private int port;

    @Value("${cassandra.keyspace:redirect_keyspace}")
    private String keyspace;

    @Bean
    public CqlSession cqlSession() {
        return CqlSession.builder()
                .addContactPoint(new InetSocketAddress(contactPoint, port))
                .withLocalDatacenter("datacenter1")
                .withKeyspace(keyspace)
                .build();
    }
}
