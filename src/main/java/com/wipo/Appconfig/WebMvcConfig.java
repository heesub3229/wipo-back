package com.wipo.Appconfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	private final long MAX_AGE_SECS = 3600;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
	    .allowedOriginPatterns("*") // HTTP 및 HTTPS 모두 허용
	    .allowedMethods("GET","POST")
	    .allowedHeaders("*")
	    .maxAge(MAX_AGE_SECS);
	}
}
