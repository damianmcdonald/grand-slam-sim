package com.github.damianmcdonald.grandslamsim.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.damianmcdonald.grandslamsim.domain.Tournament;
import com.github.damianmcdonald.grandslamsim.simengine.TournamentEngine;

@RestController
@RequestMapping(value = "/simulator")
public class TournamentController {
	
	// @Autowired private SimpMessagingTemplate simpMessagingTemplate;
	@Autowired private TournamentEngine tournamentEngine;
	private Tournament tournament;

	@RequestMapping(value = "/details", method = RequestMethod.GET)
	public ResponseEntity<Tournament> getTournamentDetails() {
		setTournament(tournamentEngine.getTournament());
		if(getTournament() == null) {
			return new ResponseEntity<Tournament>(getTournament(), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<Tournament>(getTournament(), HttpStatus.OK);
	}
	
	@RequestMapping("/start")
	public void startTournment() {
		tournamentEngine.simulateRounds(getTournament().getPlayers(), 0);
	}
	
	/*
	private void sendStartTournament() {
		simpMessagingTemplate.convertAndSend("/topic/tournament.start", new Player("Roger Federer", 1, 1));
		simpMessagingTemplate.convertAndSend("/topic/tournament.start", new Player("Novak Djokovic", 2, 1));
		simpMessagingTemplate.convertAndSend("/topic/tournament.start", new Player("Rafael Nadal", 3, 1));
		simpMessagingTemplate.convertAndSend("/topic/tournament.start", new Player("Stan Wawrinka", 4, 1));
		simpMessagingTemplate.convertAndSend("/topic/tournament.start", new Player("Andy Murray", 5, 1));
		simpMessagingTemplate.convertAndSend("/topic/tournament.start", new Player("Richard Gasquet", 6, 1));
	}
	*/
	
	public Tournament getTournament() {
		return tournament;
	}

	public void setTournament(Tournament tournament) {
		this.tournament = tournament;
	}
	
}
