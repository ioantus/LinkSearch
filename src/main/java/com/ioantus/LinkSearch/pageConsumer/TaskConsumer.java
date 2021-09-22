package com.ioantus.LinkSearch.pageConsumer;

import com.ioantus.LinkSearch.config.AppConstants;
import com.ioantus.LinkSearch.DTO.SinglePageDTO;
import lombok.RequiredArgsConstructor;

import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class TaskConsumer implements Consumer<Set<String>> {
    final SinglePageDTO parentDto;
    final private Queue<SinglePageDTO> resultQueue;

    @Override
    public void accept(Set<String> set) {
        set.stream()
                .map(AppConstants.DOMAIN_CONVERTER::apply)
                .filter((url) -> parentDto.getInnerUrl().getHost().equals(url.getHost()))
                .forEach((url) -> {
                    SinglePageDTO newPageDto = new SinglePageDTO(url);
                    newPageDto.setLevel(parentDto.getLevel()+1);
                    resultQueue.add(newPageDto);
                });
    }
}
