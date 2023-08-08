package cn.cjp.spider.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class Swagger2 {

    /**
     * dockerBean
     *
     * @return Docket
     */
    @Bean(value = "dockerBean")
    public Docket dockerBean() {
        //指定使用Swagger2规范
        return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(new ApiInfoBuilder()
                         //描述字段支持Markdown语法
                         .description("# Knife4j RESTful APIs")
                         .termsOfServiceUrl("https://doc.xiaominfo.com/")
                         .contact(new Contact("red", "http://rushgo.wiki/", "624498040@qq.com"))
                         .version("1.0")
                         .build())
            //分组名称
            .groupName("basic")
            .select()
            //这里指定Controller扫描包路径
            .apis(RequestHandlerSelectors.basePackage("cn.cjp.spider.web"))
            .paths(PathSelectors.any())
            .build();
    }

}
