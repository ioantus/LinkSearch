package com.ioantus.LinkSearch.pageConsumer;

import com.ioantus.LinkSearch.config.AppConstants;
import com.ioantus.LinkSearch.DTO.SinglePageDTO;
import lombok.RequiredArgsConstructor;

import java.net.URL;
import java.util.Set;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class UrlConsumer implements Consumer<Set<String>> {
    private final SinglePageDTO singlePageDTO;

    @Override
    public void accept(Set<String> set) {
        URL url = singlePageDTO.getInnerUrl();
        long outerLinkCount = set.stream()
                .map(AppConstants.DOMAIN_CONVERTER::apply)
                .filter((u)-> u != null)
                .filter((u)-> url.getHost().equals(u.getHost()))
                .count();
        singlePageDTO.setOuterLinksCount(outerLinkCount);
    }
}
