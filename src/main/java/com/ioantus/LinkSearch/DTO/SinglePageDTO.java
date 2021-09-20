package com.ioantus.LinkSearch.DTO;

import lombok.*;

import java.net.URL;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@AllArgsConstructor
public class SinglePageDTO {
    final private URL innerUrl;
    @EqualsAndHashCode.Exclude private Long outerLinksCount;
    @EqualsAndHashCode.Exclude private Integer level;
}

