package com.auth.userManagement.configuration;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

@EnableResourceServer
@Configuration
@EnableAsync
public class ResourcesServerConfiguration  extends ResourceServerConfigurerAdapter {

	 @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }

	
    @Bean
    @ConfigurationProperties(prefix="spring.datasource")
    public DataSource ouathDataSource(){return DataSourceBuilder.create().build();}

    @Override
    public void configure(ResourceServerSecurityConfigurer resources)throws Exception{

        TokenStore tokenStore=new JdbcTokenStore(ouathDataSource());
        resources.resourceId("user_management_api").tokenStore(tokenStore);

    }
    @Override

    public void configure(HttpSecurity http) throws Exception{


    			http.cors().and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/v1/account/confirmRegistration").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/account/forgotPassword").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/account/changePassword").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/account/verifyPasswordUpdateToken").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/account/retryAccountActivation").permitAll()
                .antMatchers(HttpMethod.GET, "/**").access("#oauth2.hasScope('read')")
                .antMatchers(HttpMethod.POST, "/**").access("#oauth2.hasScope('write')")
                .antMatchers(HttpMethod.PATCH, "/**").access("#oauth2.hasScope('write')")
                .antMatchers(HttpMethod.PUT, "/**").access("#oauth2.hasScope('write')")
                .antMatchers(HttpMethod.DELETE, "/**").access("#oauth2.hasScope('write')")
                .and()

                .headers().addHeaderWriter((request, response) -> {
		            if (request.getMethod().equals("OPTIONS")) {
		                response.setHeader("Access-Control-Allow-Methods", request.getHeader("Access-Control-Request-Method"));
		                response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
		            }
		        });
    }
}
