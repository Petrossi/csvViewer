package com.csvParser;

import com.csvParser.config.WebSocketConfig;
import com.csvParser.services.DBService;
import com.csvParser.services.TaskService;
import liquibase.util.csv.CSVReader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
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
@Import({WebSocketConfig.class})
public class Application {

	private static ConfigurableApplicationContext context;

	public static void main(String[] args) throws IOException {

		context = SpringApplication.run(Application.class, args);

//        read();
//        test();
	}

	private static void test(){
		try {
			context.getBean(DBService.class).importData(context.getBean(TaskService.class).findByToken("weibtuowcokgono"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void read(){
		String token = "bftjzvcdwtlqman";
		try {
			CSVReader reader = context.getBean(DBService.class).getReader(token);

			String [] nextLine = reader.readNext();
			System.out.println(nextLine);
		} catch (IOException e) {
			e.printStackTrace();
		}
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