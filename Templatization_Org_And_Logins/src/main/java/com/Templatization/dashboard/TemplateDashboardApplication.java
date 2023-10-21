package com.Templatization.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.Templatization.dashboard"})
public class TemplateDashboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(TemplateDashboardApplication.class, args);
	}

}
