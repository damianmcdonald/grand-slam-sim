package com.github.damianmcdonald.grandslamsim.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.damianmcdonald.grandslamsim.domain.Tournament;
import com.github.damianmcdonald.grandslamsim.service.TournamentService;

@RestController
@RequestMapping(value = "/select")
public class SelectController {
	
@Autowired private TournamentService tournamentService;
	
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public List<Tournament> listTournaments() {
		return tournamentService.listTournaments();
	}

}
