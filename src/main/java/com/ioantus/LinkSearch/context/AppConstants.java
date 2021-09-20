package com.ioantus.LinkSearch.context;

import com.ioantus.LinkSearch.Application;
import com.ioantus.LinkSearch.converters.UrlConverter;
import com.ioantus.LinkSearch.converters.DomainConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class AppConstants {

    public static final Logger LOGGER = LogManager.getLogger(Application.class);
    public static final UrlConverter URL_CONVERTER = new UrlConverter();
    public static final DomainConverter DOMAIN_CONVERTER = new DomainConverter();
    public static final String [] CORRECT_EXTENSIONS = {"htm","html","php"};


}
