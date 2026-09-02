package com.ambrosia.profile_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.ambrosia.profile_service.core.utils.AppConfiguration;


@SpringBootApplication
@EnableConfigurationProperties({AppConfiguration.class})
public class ProfileServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProfileServiceApplication.class, args);
	}
}
