package com.nova.deliveries.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getInterceptors().add((request, body, execution) -> {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                HttpServletRequest servletRequest =
                        ((ServletRequestAttributes) requestAttributes).getRequest();
                String token = servletRequest.getHeader("Authorization");
                if (token != null) {
                    request.getHeaders().add("Authorization", token);
                }
            }
            return execution.execute(request, body);
        });

        return restTemplate;
    }
}