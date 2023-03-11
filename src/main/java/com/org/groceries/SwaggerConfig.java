package com.org.groceries;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig
{
	@Bean
	public Docket api()
	{
		return new Docket (DocumentationType.SWAGGER_2).groupName ("grocery-list").apiInfo (apiInfo ()).select ()
				.paths (PathSelectors.ant ("/*/**")).apis (RequestHandlerSelectors.basePackage ("com.org.groceries"))
				.build ();
	}

	private ApiInfo apiInfo()
	{
		return new ApiInfoBuilder ().title ("Grocery List API").description ("App deals with Grocery Listing")
				.licenseUrl ("taurus.vamsi@gmail.com").version ("1.0").build ();
	}
}
