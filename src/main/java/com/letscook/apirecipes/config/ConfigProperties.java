package com.letscook.apirecipes.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ConfigurationProperties(prefix = "apirecipes")
@PropertySource("classpath:application.properties")
@Getter
@NoArgsConstructor
public class ConfigProperties {

    // API RecipesPuppy
    @Value("${apirecipes.url_apirecipepuppy}")
    private String URL_APIRECIPEPUPPY;
    @Value("${apirecipes.limit_recipepuppy}")
    private String LIMIT_RECIPEPUPPY;

    // API GIPHY
    @Value("${apirecipes.url_apigiphy}")
    private String URL_APIGIPHY;
    @Value("${apirecipes.api_key}")
    private String API_KEY;
    @Value("${apirecipes.limit}")
    private String LIMIT;
    @Value("${apirecipes.offset}")
    private String OFFSET;
    @Value("${apirecipes.rating}")
    private String RATING;
    @Value("${apirecipes.lang}")
    private String LANG;

    @Value("${apirecipes.error_return_endpoint}")
    private String ERROR_RETURN_ENDPOINT;
    @Value("${apirecipes.error_invalid_parameter}")
    private String ERROR_INVALID_PARAMETER;
    @Value("${apirecipes.error_unavailable_service}")
    private String ERROR_UNAVAILABLE_SERVICE;
    @Value("${apirecipes.error_max_exceed_recipes}")
    private String ERROR_MAX_EXCEED_RECIPES;
}