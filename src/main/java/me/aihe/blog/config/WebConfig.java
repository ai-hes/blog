package me.aihe.blog.config;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @author : aihe
 * @date : 2020-01-18
 * @Description:
 */
@Configuration
public class WebConfig implements WebFluxConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/**","/static/**")
                .addResourceLocations("classpath:/static/");
    }

    @Bean
    RouterFunction<ServerResponse> routerFunction() {
        return  route(GET("/"), req ->
                ServerResponse.temporaryRedirect(URI.create("/index.html"))
                        .build());
    }
}
