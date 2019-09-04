package com.fiberhome.fp.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class ConfigSwagger2 {

    @Value("${swagger.enable}")
    private  boolean enable;
    @Bean
    public Docket createRestApi() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("FP数据库接口文档")
                        .description("FP数据库接口文档API")
                        .termsOfServiceUrl("http://localhost:8086//swagger-ui.html")
                        .contact("ZC")
                        .version("1.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.fiberhome.fp.controller"))
                .paths(PathSelectors.any())
                .build();
        docket.enable(enable);
        return docket;
    }

    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("FP数据库接口文档")
                .description("FP数据库接口文档API")
                .termsOfServiceUrl("http://localhost:8086//swagger-ui.html")
                .contact("ZC")
                .version("1.0")
                .build();
    }
}
