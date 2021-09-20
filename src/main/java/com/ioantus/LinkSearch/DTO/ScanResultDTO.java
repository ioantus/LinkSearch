package com.ioantus.LinkSearch.DTO;

import com.ioantus.LinkSearch.service.MainService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ScanResultDTO {
    final private String domain;
    final private Integer levelScan;
    final private MainService service;
    private int resultCount;
}
