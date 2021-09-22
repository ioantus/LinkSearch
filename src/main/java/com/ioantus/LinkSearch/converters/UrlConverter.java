package com.ioantus.LinkSearch.converters;

import com.ioantus.LinkSearch.config.AppConstants;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.function.BiFunction;

import static com.ioantus.LinkSearch.config.AppConstants.CORRECT_EXTENSIONS;

public class UrlConverter implements BiFunction<String, String, URL> {

    @Override
    public URL apply(String mainUrl, String innerUrl) {
        String urlStr;
        try {
            if (isEmpty(mainUrl) || isEmpty(innerUrl)) {
                return null;
            } else if (innerUrl.startsWith("/")) {
                if (mainUrl.endsWith("/")) {
                    urlStr = mainUrl.substring(0, mainUrl.length() - 1) + innerUrl;
                } else {
                    urlStr = mainUrl + innerUrl;
                }
            } else if (innerUrl.startsWith("http://") || innerUrl.startsWith("https://")) {
                urlStr = innerUrl;
            } else {
                if (mainUrl.endsWith("/")) {
                    urlStr = mainUrl + innerUrl;
                } else {
                    urlStr = mainUrl + "/" + innerUrl;
                }
            }
            URL url = removeForbiddenFiles(removeQuery(new URL(removeBadEnding(urlStr))));

            return url;
        } catch (Exception e) {
            AppConstants.LOGGER.error(String.format("InnerUrlConverter exception during converting inner url: %s, main url: %s, thread: %s, stack trace: %s",
                    innerUrl, mainUrl, Thread.currentThread().getName(), e.getStackTrace().toString())
            );
            return null;
        }
    }

    private boolean isEmpty(String urlStr) {
        if (urlStr == null || urlStr.isEmpty() || urlStr.trim() == ""){
            return true;
        } else {
            return false;
        }
    }

    private String removeBadEnding(String urlStr){
        if (urlStr.lastIndexOf("/javascript:") >= 0) {
            return urlStr.substring(0, urlStr.lastIndexOf("/javascript:")+1);
        } else if (urlStr.lastIndexOf("/mailto:") >= 0) {
            return urlStr.substring(0, urlStr.lastIndexOf("/mailto:")+1);
        }
        return urlStr;
    }

    private URL removeQuery(URL url) throws MalformedURLException {
        return new URL(url.getProtocol()+"://"+url.getAuthority()+url.getPath());
    }

    private URL removeForbiddenFiles(URL url) throws MalformedURLException {
        String filePath = url.getPath();
        if (!isEmpty(filePath) && !filePath.endsWith("/")) {
            // Extract file name
            String fileName;
            if (filePath.contains("/")) {
                fileName = filePath.substring(filePath.lastIndexOf("/")+1);
            } else {
                fileName = filePath;
            }
            //Extract file extension
            String fileExt = new String();
            if (fileName.contains(".") && fileName.length() > (fileName.indexOf('.') + 1)) {
                fileExt = fileName.substring(fileName.lastIndexOf(".") + 1);
                if (Arrays.asList(CORRECT_EXTENSIONS).contains(fileExt)){
                    return new URL(url.toExternalForm().substring(0,url.toExternalForm().lastIndexOf("/")));
                }
            }
        }
        return url;
    }

}
