package com.ioantus.LinkSearch.DTO;

import com.ioantus.LinkSearch.service.MainService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
public class ScanResultDTO {
    final private String domain;
    final private Integer levelScan;
    final private Set<SinglePageDTO> resultSet;
    private volatile Long externalPagesCount = 0L;
    private Date startTime = new Date();
    private Date endTime = new Date();
    private boolean jobDone = false;

    public void increaseExternalPagesCount(Long count){
        externalPagesCount += count;
    }

}
