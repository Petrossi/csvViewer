package com.csvParser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.IOException;

@EnableScheduling
@EnableAsync
@EnableCaching
@SpringBootApplication
public class Application {

	private static ConfigurableApplicationContext context;

	public static void main(String[] args) throws IOException {

		context = SpringApplication.run(Application.class, args);
	}

	private static void test(){
//		try {
//			context.getBean(DBService.class).importData("bpdcbcmhkmfsrht");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**");
			}
		};
	}
}