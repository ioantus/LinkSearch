package com.ioantus.LinkSearch.context;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AppContext {

    @Bean
    @Scope("singleton")
    public ExecutorService executorService(){
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        return Executors.newFixedThreadPool(availableProcessors);
    }

}
