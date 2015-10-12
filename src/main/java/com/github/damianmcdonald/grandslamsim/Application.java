package com.github.damianmcdonald.grandslamsim;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import com.github.damianmcdonald.grandslamsim.domain.Player;
import com.github.damianmcdonald.grandslamsim.service.ExcelReaderService;
import com.github.damianmcdonald.grandslamsim.service.TournamentService;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

	@Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
		 return application.sources(Application.class);
    }

	public static void main(final String[] args) throws InvalidFormatException, IOException {
		final ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
		populateTournaments(ctx);
	}
	
	private static void populateTournaments(final ApplicationContext ctx) throws InvalidFormatException, IOException {
		final TournamentService tournamentService = ctx.getBean(TournamentService.class);
		final ExcelReaderService excelReaderService = ctx.getBean(ExcelReaderService.class);
		System.out.println("************************************");
		System.out.println("Loading default tournaments");
		if(tournamentService.verifyUniqueTournamentName("Wimbledon")) {
			System.out.println("Creating Wimbledon tournament ....");
			tournamentService.createTournament("Wimbledon", true);
			System.out.println("Wimbledon tournament created.");
		}
		if(tournamentService.verifyUniqueTournamentName("Legends")) {
			final File file = createTempFileFromInputStream("legends-upload.xlsx");
			final List<Player> players = excelReaderService.readPlayersFromExcel(file);
			IntStream.range(0, players.size()).forEach(i -> players.get(i).setSeed(i+1));
			System.out.println("Creating Legends tournament ....");
			tournamentService.createTournament("Legends", players);
			System.out.println("Legends tournament created.");
			file.delete();
		}
		if(tournamentService.verifyUniqueTournamentName("MOTU")) {
			final File file = createTempFileFromInputStream("motu-upload.xlsx");
			final List<Player> players = excelReaderService.readPlayersFromExcel(file);
			IntStream.range(0, players.size()).forEach(i -> players.get(i).setSeed(i+1));
			System.out.println("Creating MOTU tournament ....");
			tournamentService.createTournament("MOTU", players);
			System.out.println("MOTU tournament created.");
			file.delete();
		}
		System.out.println("Default tournaments loaded.");
		System.out.println("************************************");
	}
	
	private static File createTempFileFromInputStream(final String fileName) throws IOException {
		final InputStream is = Application.class.getClassLoader().getResourceAsStream(fileName);
		final File tmpFile = File.createTempFile("file", "temp");
		FileUtils.copyInputStreamToFile(is, tmpFile); 
		return tmpFile;
	}

}
