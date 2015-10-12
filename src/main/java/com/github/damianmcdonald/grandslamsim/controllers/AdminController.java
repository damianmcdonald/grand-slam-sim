package com.github.damianmcdonald.grandslamsim.controllers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.github.damianmcdonald.grandslamsim.domain.Player;
import com.github.damianmcdonald.grandslamsim.domain.Tournament;
import com.github.damianmcdonald.grandslamsim.service.ExcelReaderService;
import com.github.damianmcdonald.grandslamsim.service.TournamentService;

@RestController
@RequestMapping(value = "/admin")
public class AdminController {
	
	@Value("${tmp.upload.dir}")
	private String uploadDir;
	
	@Autowired private TournamentService tournamentService;
	@Autowired private ExcelReaderService excelReaderService;
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/checkAdmin", method = RequestMethod.GET)
	public boolean validateUser() {
		for(GrantedAuthority auth : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
			if(auth.getAuthority().equalsIgnoreCase("ROLE_ADMIN")) {
				return true;
			}
		}
		return false;
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public List<Tournament> listTournaments() {
		return tournamentService.listTournaments();
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/add/{tournamentName}", method = RequestMethod.POST)
	public void addTournament(@PathVariable final String tournamentName, @RequestBody final List<Player> players) {
		tournamentService.createTournament(tournamentName, players);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/loadPlayers", method = RequestMethod.GET)
	public List<Player> loadPlayers() {
		return tournamentService.loadPlayers();
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/checkUnique/{tournamentName}", method = RequestMethod.GET)
	public boolean verifyUniqueTournamentName(@PathVariable final String tournamentName) {
		return tournamentService.verifyUniqueTournamentName(tournamentName);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/delete/{tournamentName}", method = RequestMethod.DELETE)
	public void deleteTournament(@PathVariable final String tournamentName) {
		tournamentService.removeTournament(tournamentName);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/uploadPlayers", method = RequestMethod.POST)
	public List<Player> handleFileUpload(
	    @RequestParam("filename") final String fileName,
	    @RequestParam("file") final MultipartFile file, 
	    HttpServletRequest request) throws IOException, InvalidFormatException {
	    if(!file.isEmpty()) {
	        final byte[] bytes = file.getBytes();
	        final BufferedOutputStream stream =
	            new BufferedOutputStream(new FileOutputStream(new File(uploadDir + File.separator + fileName)));
	        stream.write(bytes);
	        stream.close();
	        return excelReaderService.readPlayersFromExcel(new File(uploadDir + File.separator + fileName));
	    } else {
	      throw new IllegalArgumentException("File can not be empty");
	    }
	}

}
