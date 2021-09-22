package com.ioantus.LinkSearch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@ComponentScan
public class AppContext {

    @Bean
    @Scope("singleton")
    public ExecutorService executorService(){
        int availableProcessors = Runtime.getRuntime().availableProcessors()*10;
        return Executors.newFixedThreadPool(availableProcessors);
    }

}
