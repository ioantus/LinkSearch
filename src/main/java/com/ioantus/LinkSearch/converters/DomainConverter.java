package com.ioantus.LinkSearch.converters;


import com.ioantus.LinkSearch.context.AppConstants;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Function;

public class DomainConverter implements Function<String, URL> {

    @Override
    public URL apply(String s) {
        String urlStr;
        if (s == null) {
            throw new RuntimeException("Url address is empty!");
        } else {
            urlStr = convertUrlStringFromDomain(s);
        }
        try {
            URL url = new URL(urlStr);
            return url;
        } catch (MalformedURLException e) {
            AppConstants.LOGGER.error(String.format("UrlConverter exception during converting string: %s, thread: %s, stack trace: %s",
                    s, Thread.currentThread().getName(), e.getStackTrace().toString())
            );
            return null;
        }
    }

    private String convertUrlStringFromDomain(String url){
        StringBuilder stringBuilder = new StringBuilder(url);
        if (!stringBuilder.toString().startsWith("http://")){
            if (!stringBuilder.toString().startsWith("www.")){
                stringBuilder.insert(0,"www.");
            }
            stringBuilder.insert(0,"http://");
        }
        return stringBuilder.toString();
    }

}
