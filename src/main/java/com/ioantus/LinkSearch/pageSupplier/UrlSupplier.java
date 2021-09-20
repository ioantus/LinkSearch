package com.ioantus.LinkSearch.pageSupplier;

import com.ioantus.LinkSearch.context.AppConstants;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

@AllArgsConstructor
public class UrlSupplier implements Supplier<Set<String>> {
    private URL url;

    @Override
    public Set<String> get() {
        Set<String> resultSet = new HashSet();
        Document document = null;
        String extUrl = url.toExternalForm();
        try {
            document = Jsoup.connect(extUrl).get();
            for (Element element : document.getElementsByTag("a")) {
                String link = element.attr("href");
                if (!link.equals("/") && !resultSet.contains(link)) {
                    URL newUrl = AppConstants.URL_CONVERTER.apply(url.getProtocol()+"://"+url.getHost(), link);
                    if (newUrl != null) {
                        String newUrlString = newUrl.toExternalForm();
                        if (newUrl != null && !newUrl.equals(extUrl) && !resultSet.contains(newUrlString))
                            resultSet.add(newUrlString);
                    }
                }
            }
        } catch (IOException e) {
            AppConstants.LOGGER.error(String.format("UrlSupplier exception during scanning url: %s, thread: %s, stack trace: %s",
                    url.toString(), Thread.currentThread().getName(), System.err));
        }
        return resultSet;
    }
}
