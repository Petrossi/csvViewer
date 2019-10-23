package com.csvParser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAsync
@EnableCaching
@SpringBootApplication
public class Application {

	private static ConfigurableApplicationContext context;
	private static String[] args;

	public static void main(String[] args)  {
		Application.args = args;

		context = SpringApplication.run(Application.class, args);

	}
}
