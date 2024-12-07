package com.certi;

import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(MongoClients.create("mongodb+srv://bejawadasaimahendra:tHJW1BzKOSw1BwXQ@meec.7kphnfb.mongodb.net"), "certificate-db");
    }
}
