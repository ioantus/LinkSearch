package com.ioantus.LinkSearch.service;

import com.ioantus.LinkSearch.context.AppConstants;
import com.ioantus.LinkSearch.context.AppContext;
import com.ioantus.LinkSearch.pageConsumer.TaskConsumer;
import com.ioantus.LinkSearch.pageConsumer.UrlConsumer;
import com.ioantus.LinkSearch.DTO.SinglePageDTO;
import com.ioantus.LinkSearch.pageSupplier.UrlSupplier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class MainService implements Runnable {
    private final String mainDomain;
    private final Integer maxLevel;
    private final Map<URL, SinglePageDTO> resultData;
    private final Map<URL, CompletableFuture> futureMap;
    private final ApplicationContext ctx = new AnnotationConfigApplicationContext(AppContext.class);

    public MainService(String mainDomain, Integer maxLevel) {
        this.mainDomain = mainDomain;
        this.maxLevel = maxLevel;
        resultData = new HashMap<>();
        futureMap = new HashMap<>();
    }

    @Override
    public void run() {
        AppConstants.LOGGER.debug(String.format("Start scanning domain: %s, thread: %s",
            mainDomain, Thread.currentThread().getName())
        );
        URL url = AppConstants.DOMAIN_CONVERTER.apply(mainDomain);
        SinglePageDTO mainPageDto = new SinglePageDTO(url);
        mainPageDto.setLevel(0);
        createNewTask(mainPageDto);
        while (futureMap.size() != 0) {
            // Check new task
            try {
                Thread.sleep(10L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            continue;
        }
        AppConstants.LOGGER.debug(String.format("Finish scanning domain: %s, thread: %s",
            mainDomain, Thread.currentThread().getName())
        );
    }

    private void createNewTask(SinglePageDTO taskPageDto){
        // Start processing
        try {
            if (isTaskExists(taskPageDto)) {
                return;
            }
            CompletableFuture future =
                    CompletableFuture.supplyAsync(new UrlSupplier(taskPageDto.getInnerUrl()), (ExecutorService)ctx.getBean("executorService"))
                            // Save result DTO
                            .thenApplyAsync((set) -> {
                                new UrlConsumer(taskPageDto).accept(set);
                                addNewResult(taskPageDto);
                                AppConstants.LOGGER.debug(String.format("New DTO %s, thread: %s",
                                        taskPageDto.getInnerUrl().toString(), Thread.currentThread().getName())
                                );
                                return set;
                            }, (ExecutorService)ctx.getBean("executorService"))
                            // Create new task records
                            .thenAcceptAsync((set) -> {
                                if (taskPageDto.getLevel() < maxLevel) {
                                    Queue<SinglePageDTO> newDtoQueue = new LinkedList<>();
                                    new TaskConsumer(taskPageDto, newDtoQueue).accept(set);
                                    newDtoQueue.forEach(this::createNewTask);
                                    AppConstants.LOGGER.debug(String.format("New task(s) added, thread: %s",
                                            Thread.currentThread().getName())
                                    );
                                }
                            }, (ExecutorService)ctx.getBean("executorService"))
                            .exceptionally(ex -> {
                                AppConstants.LOGGER.error(String.format("Thread: %s, exception: %s",
                                        Thread.currentThread().getName(), ex.getMessage())
                                );
                                futureMap.remove(taskPageDto.getInnerUrl());
                                return null;
                            })
                            // Remove future from map after it's finished
                            .thenRun(() -> {
                                futureMap.remove(taskPageDto.getInnerUrl());
                                AppConstants.LOGGER.debug(String.format("Future succeed, thread: %s",
                                        Thread.currentThread().getName())
                                );
                            });
            futureMap.put(taskPageDto.getInnerUrl(), future);
        } catch (Exception e){
            AppConstants.LOGGER.error(String.format("WorkProcess exception during processing DTO: %s, thread: %s, stack trace: %s",
                    taskPageDto.toString(), Thread.currentThread().getName(), e.getStackTrace().toString())
            );
            e.printStackTrace();
        };
    }

    private boolean isTaskExists(SinglePageDTO newDto){
        if (resultData.containsKey(newDto.getInnerUrl()) ||
                futureMap.containsKey(newDto.getInnerUrl())
        ) {
            return true;
        } else {
            return false;
        }
    }

    private void addNewResult(SinglePageDTO newPageDto){
        URL innerUrl = newPageDto.getInnerUrl();
        if (!resultData.containsKey(innerUrl)) {
            // Put new result DTO
            resultData.put(innerUrl, newPageDto);
        } else if (resultData.containsKey(innerUrl)) {
            // Check level in existing DTO
            SinglePageDTO singlePageDTO = resultData.get(innerUrl);
            singlePageDTO.setLevel(Integer.min(singlePageDTO.getLevel(), newPageDto.getLevel()));
        }
    }

}
