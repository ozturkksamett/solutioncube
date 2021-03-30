package com.example.solutioncube.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
public class MongoConfig {

    public MongoClient mongoClient() {

        //return MongoClients.create("mongodb+srv://ethemduzgun:scfm2021@solutioncube.azjcz.mongodb.net/solutioncube");
        return MongoClients.create(System.getenv("MONGODB_URI")); 
    }

    public @Bean MongoTemplate mongoTemplate() {
    	
        return new MongoTemplate(mongoClient(), "erisyem"); 
    }

} 