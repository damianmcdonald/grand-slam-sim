package com.github.damianmcdonald.grandslamsim.controllers;

import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;

import javax.annotation.Resource;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.damianmcdonald.grandslamsim.Application;
import com.github.damianmcdonald.grandslamsim.service.TournamentService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AdminControllerTest {

  @Autowired
  private WebApplicationContext webApplicationContext;
  
  @Resource
  private FilterChainProxy springSecurityFilterChain;
  
  @Autowired
  private TournamentService tournamentService;

  private MockMvc mockMvc;
  
  private String playersJson;

  private void setAuthenticationAuthorizedUser() {
	  final Collection<GrantedAuthority> authorities = new HashSet<>();
      authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
      final Authentication authToken = new UsernamePasswordAuthenticationToken("admin", "admin", authorities);
      SecurityContextHolder.getContext().setAuthentication(authToken);
  }
  
  private void setAuthenticationUnAuthorizedUser() {
	  final Collection<GrantedAuthority> authorities = new HashSet<>();
      authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
      final Authentication authToken = new UsernamePasswordAuthenticationToken("user", "user", authorities);
      SecurityContextHolder.getContext().setAuthentication(authToken);
  }
  
  private File uriToFile() {
	  File f = null;
	  try {
	    f = new File(getClass().getResource("/upload.xlsx").toURI());
	  } catch (URISyntaxException e) {
	      try {
		    f = new File(getClass().getResource("/upload.xlsx").toURI());
		  } catch (URISyntaxException e1) {
		    e1.printStackTrace();
		    fail("Unable to parse file resource: upload.xlsx");
		  }
	  }
	  return f;
  }
  
  @Before
  public void setUp() throws Exception {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
    		.apply(springSecurity())
            .build();
    if(tournamentService.verifyUniqueTournamentName("Wimbledon")) {
    	tournamentService.createTournament("Wimbledon", false);
    }
    if(tournamentService.verifyUniqueTournamentName("Halle")) {
    	tournamentService.createTournament("Halle", false);
    }
    playersJson = new ObjectMapper().writeValueAsString(tournamentService.loadPlayers());
  }

  @Test
  public void aCheckAdminSuccessTest() throws Exception {
	  setAuthenticationAuthorizedUser();
      this.mockMvc.perform(get("/admin/checkAdmin"))
              .andExpect(status().isOk())
              .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
              .andDo(print())
              .andExpect(jsonPath("$", Matchers.equalTo(true)));
  }
  
  @Test
  public void aCheckAdminFailureTest() throws Exception {
	  setAuthenticationUnAuthorizedUser();
      this.mockMvc.perform(get("/admin/checkAdmin"))
              .andExpect(status().isForbidden());
  }
  
  @Test
  public void bListSuccessTest() throws Exception {
	  setAuthenticationAuthorizedUser();
	  this.mockMvc.perform(get("/admin/list"))
              .andExpect(status().isOk())
              .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
              .andDo(print())
              .andExpect(jsonPath("$", Matchers.hasSize(2)))
              .andExpect(jsonPath("$[0].name", Matchers.equalTo("Wimbledon")));
  }
  
  @Test
  public void bListFailureTest() throws Exception {
	  setAuthenticationUnAuthorizedUser();
	  this.mockMvc.perform(get("/admin/list"))
	  			.andExpect(status().isForbidden());
  }
  
  @Test
  public void cAddSuccessTest() throws Exception {
	  setAuthenticationAuthorizedUser();
      this.mockMvc.perform(post("/admin/add/Madrid")
    		  .contentType(MediaType.APPLICATION_JSON).content(playersJson)
    		  )
              .andExpect(status().isOk());
      
      this.mockMvc.perform(get("/admin/list"))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(jsonPath("$", Matchers.hasSize(3)))
      .andExpect(jsonPath("$[1].name", Matchers.equalTo("Madrid")));
  }
  
  @Test
  public void cAddFailureTest() throws Exception {
	  setAuthenticationUnAuthorizedUser();
      this.mockMvc.perform(post("/admin/add/Madrid")
		      .contentType(MediaType.APPLICATION_JSON).content(playersJson)
			  )
              .andExpect(status().isForbidden());
  }
  
  @Test
  public void dLoadPlayersSuccessTest() throws Exception {
	  setAuthenticationAuthorizedUser();
	  this.mockMvc.perform(get("/admin/loadPlayers"))
              .andExpect(status().isOk())
              .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
              .andDo(print())
              .andExpect(jsonPath("$", Matchers.hasSize(16)))
              .andExpect(jsonPath("$[0].surname", Matchers.equalTo("Djokovic")));
  }
  
  @Test
  public void dLoadPlayersFailureTest() throws Exception {
	  setAuthenticationUnAuthorizedUser();
	  this.mockMvc.perform(get("/admin/loadPlayers"))
  				.andExpect(status().isForbidden());
  }
  
  @Test
  public void eCheckUniqueSuccessTest() throws Exception {
	  setAuthenticationAuthorizedUser();
	  this.mockMvc.perform(get("/admin/checkUnique/Wimbledon"))
              .andExpect(status().isOk())
              .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
              .andDo(print())
              .andExpect(jsonPath("$", Matchers.equalTo(false)));
  }
  
  @Test
  public void eCheckUniqueFailureTest() throws Exception {
	  setAuthenticationUnAuthorizedUser();
	  this.mockMvc.perform(get("/admin/checkUnique/Wimbledon"))
	  			.andExpect(status().isForbidden());
  }
  
  @Test
  public void fDeleteSuccessTest() throws Exception {
	  setAuthenticationAuthorizedUser();
	  this.mockMvc.perform(delete("/admin/delete/Halle"))
              .andExpect(status().isOk());
	  
	  this.mockMvc.perform(get("/admin/list"))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(jsonPath("$", Matchers.hasSize(2)))
      .andExpect(jsonPath("$[0].name", Matchers.equalTo("Wimbledon")));
  }
  
  @Test
  public void fDeleteFailureTest() throws Exception {
	  setAuthenticationUnAuthorizedUser();
	  this.mockMvc.perform(delete("/admin/delete/Halle"))
	  			.andExpect(status().isForbidden());
  }
  
  @Test
  public void gHandleFileUploadSuccessTest() throws Exception {
	setAuthenticationAuthorizedUser();
	final File file = uriToFile();
	final Path path = Paths.get(file.getAbsolutePath());
	final byte[] data = Files.readAllBytes(path);
    final MockMultipartFile multipartFile =
        new MockMultipartFile("file", file.getName(), "text/plain", data);
    mockMvc.perform(
            MockMvcRequestBuilders.fileUpload("/admin/uploadPlayers")
            .file(multipartFile)
            .param("filename", path.getFileName().toString())
            .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(jsonPath("$", Matchers.hasSize(8)))
            .andExpect(jsonPath("$[0].surname", Matchers.equalTo("He-Man")));
  }
  
  @Test
  public void gHandleFileUploadFailureTest() throws Exception {
	setAuthenticationUnAuthorizedUser();
	final File file = uriToFile();
	final Path path = Paths.get(file.getAbsolutePath());
	final byte[] data = Files.readAllBytes(path);
    final MockMultipartFile multipartFile =
        new MockMultipartFile("file", file.getName(), "text/plain", data);
    mockMvc.perform(
            MockMvcRequestBuilders.fileUpload("/admin/uploadPlayers")
            .file(multipartFile)
            .param("filename", path.getFileName().toString())
            .accept(MediaType.APPLICATION_JSON)
            )
    	.andExpect(status().isForbidden());
  } 

}
