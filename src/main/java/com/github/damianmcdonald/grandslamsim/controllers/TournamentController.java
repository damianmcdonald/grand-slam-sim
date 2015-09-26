package com.github.damianmcdonald.grandslamsim.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.damianmcdonald.grandslamsim.dispatchers.WebSocketMessageDispatcher;
import com.github.damianmcdonald.grandslamsim.domain.Tournament;
import com.github.damianmcdonald.grandslamsim.service.TournamentService;

@RestController
@RequestMapping(value = "/simulator")
public class TournamentController {
	
	@Autowired private WebSocketMessageDispatcher messageDispatcher;
	@Autowired private TournamentService tournamentService;

	@RequestMapping(value = "/details/{tournamentName}", method = RequestMethod.GET)
	public ResponseEntity<Tournament> getTournamentDetails(@PathVariable final String tournamentName) {
		final Tournament tournament = tournamentService.getTournament(tournamentName);
		if(tournament == null) {
			return new ResponseEntity<Tournament>(tournament, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Tournament>(tournament, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/start/{tournamentName}", method = RequestMethod.POST)
	public void startTournment(@PathVariable final String tournamentName) {
		messageDispatcher.dispatchTournamentStarted(tournamentName);
		final Tournament tournament = tournamentService.getTournament(tournamentName);
		tournamentService.simulateRounds(tournamentName, tournament.getPlayers());
	}

}
