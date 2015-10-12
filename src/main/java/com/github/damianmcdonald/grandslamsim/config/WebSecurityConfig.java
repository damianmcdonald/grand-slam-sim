package com.github.damianmcdonald.grandslamsim.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		http
		.csrf().disable()
		.authorizeRequests()
			.antMatchers("/assets/**", "/logon.html").permitAll()
			.anyRequest().authenticated()
		.and()
		.formLogin()
			.loginPage("/logon.html")
			.defaultSuccessUrl("/index.html")
			.permitAll()
			.and()
		.logout()
			.logoutSuccessUrl("/logon.html")
			.permitAll();
	}
	
    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
    	auth.userDetailsService(inMemoryUserDetailsManager());
    }
    
    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        final Properties users = new Properties();
        users.put("user","user,ROLE_USER,enabled");
        users.put("admin","admin,ROLE_ADMIN,enabled");
        return new InMemoryUserDetailsManager(users);
    }
    
}
