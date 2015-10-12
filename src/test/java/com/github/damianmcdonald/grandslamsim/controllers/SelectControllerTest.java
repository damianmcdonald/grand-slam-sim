package com.github.damianmcdonald.grandslamsim.controllers;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collection;
import java.util.HashSet;

import javax.annotation.Resource;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.github.damianmcdonald.grandslamsim.Application;
import com.github.damianmcdonald.grandslamsim.service.TournamentService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class SelectControllerTest {
	
	  @Autowired
	  private WebApplicationContext webApplicationContext;
	  
	  @Resource
	  private FilterChainProxy springSecurityFilterChain;
	  
	  @Autowired
	  private TournamentService tournamentService;
	  
	  private MockMvc mockMvc;
	  
	  private void setAuthenticationAuthorizedUser() {
		  final Collection<GrantedAuthority> authorities = new HashSet<>();
	      authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
	      final Authentication authToken = new UsernamePasswordAuthenticationToken("admin", "admin", authorities);
	      SecurityContextHolder.getContext().setAuthentication(authToken);
	  }
	  
	  @Before
	  public void setUp() throws Exception {
	    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
	    		.apply(springSecurity())
	            .build();
	    if(tournamentService.verifyUniqueTournamentName("Wimbledon")) {
	    	tournamentService.createTournament("Wimbledon", false);
	    }
	  }
	  
	  @Test
	  public void selectListSuccessTest() throws Exception {
		  setAuthenticationAuthorizedUser();
		  this.mockMvc.perform(get("/select/list"))
	              .andExpect(status().isOk())
	              .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
	              .andDo(print())
	              .andExpect(jsonPath("$[0].name", Matchers.equalTo("Wimbledon")));
	  }
	  
	  @Test
	  public void selectListFailureTest() throws Exception {
		  this.mockMvc.perform(get("/select/list"))
		  			.andExpect(status().isFound());
	  }

}
