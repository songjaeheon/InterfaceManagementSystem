package com.ims.eims.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * The Web MVC Configuration.
 * <p>
 * "And here we have the web layer! MVC, REST, controllers, views.
 * We're using @EnableWebMvc to get started with sensible defaults,
 * but check out how easy it is to customize resource handlers and multipart resolvers!"
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.ims.eims.controller",
    includeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class),
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = RestController.class)
    },
    useDefaultFilters = false
)
public class WebConfig extends WebMvcConfigurerAdapter {

    // "Static resources? No problem! We'll serve them straight from the classpath.
    // This is where our beautiful Vue.js frontend will live."
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }

    // "Letting the container's default servlet handle the rest? Why not!
    // This is useful for serving static content if Spring's DispatcherServlet is mapped to /"
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    // "JSPs? Still rock solid for enterprise rendering!
    // Let's wire up a ViewResolver so we can return view names from our controllers."
    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    // "File uploads are a breeze with Spring's MultipartResolver.
    // Just a few lines of config and we're ready to handle those multipart requests."
    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setMaxUploadSize(5242880); // 5MB
        return resolver;
    }
}
