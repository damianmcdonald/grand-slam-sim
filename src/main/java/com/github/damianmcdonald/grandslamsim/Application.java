package com.github.damianmcdonald.grandslamsim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import com.github.damianmcdonald.grandslamsim.service.TournamentService;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {
	
	@Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

	public static void main(final String[] args) {
		final ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
		final TournamentService tournamentService = ctx.getBean(TournamentService.class);
		
		System.out.println("++++++++++++++++++++++++++++++++++");
		System.out.println("Loading tournaments");
		tournamentService.createTournament("Australian Open", true);
		tournamentService.createTournament("French Open", true);
		tournamentService.createTournament("Wimbledon", true);
		tournamentService.createTournament("US Open", true);
		System.out.println("++++++++++++++++++++++++++++++++++");
		
	}
	
	
	
}
