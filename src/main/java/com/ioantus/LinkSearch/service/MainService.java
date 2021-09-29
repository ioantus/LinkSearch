package com.ioantus.LinkSearch.service;

import com.ioantus.LinkSearch.DTO.ScanResultDTO;
import com.ioantus.LinkSearch.config.AppConstants;
import com.ioantus.LinkSearch.config.AppContext;
import com.ioantus.LinkSearch.pageConsumer.TaskConsumer;
import com.ioantus.LinkSearch.pageConsumer.UrlConsumer;
import com.ioantus.LinkSearch.DTO.SinglePageDTO;
import com.ioantus.LinkSearch.pageSupplier.UrlSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class MainService implements Runnable {
    private final ScanResultDTO resultDTO;
    private final Map<URL, CompletableFuture> futureMap;

    private final ApplicationContext ctx = new AnnotationConfigApplicationContext(AppContext.class);

    public MainService(ScanResultDTO resultDTO) {
        this.resultDTO = resultDTO;
        futureMap = new HashMap<>();
    }

    @Override
    public void run() {
        AppConstants.LOGGER.debug(String.format("Start scanning domain: %s, thread: %s",
                resultDTO.getDomain(), Thread.currentThread().getName())
        );
        URL url = AppConstants.DOMAIN_CONVERTER.apply(resultDTO.getDomain());
        SinglePageDTO mainPageDto = new SinglePageDTO(url);
        mainPageDto.setLevel(0);
        createNewTask(mainPageDto);
        AppConstants.LOGGER.debug(String.format("Finish scanning domain: %s, thread: %s",
                resultDTO.getDomain(), Thread.currentThread().getName())
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
                            })
                            // Create new task records
                            .thenAcceptAsync((set) -> {
                                if (resultDTO.getLevelScan() == 0 || taskPageDto.getLevel() < resultDTO.getLevelScan()) {
                                    Queue<SinglePageDTO> newDtoQueue = new LinkedList<>();
                                    new TaskConsumer(taskPageDto, newDtoQueue).accept(set);
                                    newDtoQueue.forEach(this::createNewTask);
                                    AppConstants.LOGGER.debug(String.format("New task(s) added, thread: %s",
                                            Thread.currentThread().getName())
                                    );
                                }
                            })
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
                                if (futureMap.size() == 0) {
                                    resultDTO.setEndTime(new Date());
                                    resultDTO.setJobDone(true);
                                }
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
        if (resultDTO.getResultSet().contains(newDto) ||
                futureMap.containsKey(newDto.getInnerUrl())
        ) {
            return true;
        } else {
            return false;
        }
    }

    private void addNewResult(SinglePageDTO newPageDto){
        URL innerUrl = newPageDto.getInnerUrl();
        if (!resultDTO.getResultSet().contains(newPageDto)) {
            // Put new result DTO
            resultDTO.getResultSet().add(newPageDto);
            resultDTO.increaseExternalPagesCount(newPageDto.getOuterLinksCount());
        }
}

}
