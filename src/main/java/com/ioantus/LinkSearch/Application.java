package com.ioantus.LinkSearch;

import com.ioantus.LinkSearch.config.AppConstants;
import com.ioantus.LinkSearch.config.AppContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(AppContext.class);
		SpringApplication.run(Application.class, args);
		AppConstants.LOGGER.debug("Application start");
	}

}
