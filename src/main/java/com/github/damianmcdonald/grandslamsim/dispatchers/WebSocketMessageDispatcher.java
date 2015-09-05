package com.github.damianmcdonald.grandslamsim.dispatchers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.github.damianmcdonald.grandslamsim.domain.MatchResult;
import com.github.damianmcdonald.grandslamsim.domain.TournamentWinner;
import com.github.damianmcdonald.grandslamsim.simengine.MatchUp;

@Component
public class WebSocketMessageDispatcher {
	
	@Autowired private SimpMessagingTemplate simpMessagingTemplate;
	
	public void dispatchMatchStats(String msg) {
		// simpMessagingTemplate.convertAndSend("/topic/match.stats", new MatchStats(msg));
	}
	
	public void dispatchMatchResult(MatchUp matchUp) {
		MatchResult result = new MatchResult(
											matchUp.getFinalScore(), 
											matchUp.getMatchPosition(),
											matchUp.getRound(), 
											matchUp.getWinner()
											);
		simpMessagingTemplate.convertAndSend("/topic/match.results", result);
	}
	
	public void dispatchTournamentWinner(TournamentWinner winner) {
		simpMessagingTemplate.convertAndSend("/topic/match.winner", winner);
	}
	

}
