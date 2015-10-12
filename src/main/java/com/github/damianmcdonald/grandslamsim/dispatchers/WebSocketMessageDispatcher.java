package com.github.damianmcdonald.grandslamsim.dispatchers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.github.damianmcdonald.grandslamsim.domain.Player;
import com.github.damianmcdonald.grandslamsim.simengine.MatchUp;

@Component
public class WebSocketMessageDispatcher {
	
	@Autowired private SimpMessagingTemplate simpMessagingTemplate;
	
	public void dispatchTournamentStarted(final String tournamentName) {
		simpMessagingTemplate.convertAndSend("/topic/tournament.started."+tournamentName, "Tournament Started");
	}
	
	public void dispatchMatchStats(final String tournamentName, final String headline, final int headlineType) {
		simpMessagingTemplate.convertAndSend("/topic/match.stats."+tournamentName, new MatchStats(headline, headlineType));
	}
	
	public void dispatchMatchResult(final String tournamentName, final MatchUp matchUp, final boolean isFinalMatch) {
		final MatchResult result = new MatchResult(
											matchUp.getFinalScore(), 
											matchUp.getMatchPosition(),
											matchUp.getRound(), 
											matchUp.getWinner(),
											isFinalMatch
											);
		simpMessagingTemplate.convertAndSend("/topic/match.results."+tournamentName, result);
	}
	
	public void dispatchTournamentWinner(final String tournamentName, final String score, final int round, final Player winner) {
		simpMessagingTemplate.convertAndSend("/topic/match.winner."+tournamentName, new TournamentWinner(score, round, winner));
	}
	
	public enum HEADLINE_TYPE {
	    MATCHUP,
	    SET_RESULT,
	    MATCH_WINNER;
	}
	
	class MatchStats {
		
		private String headline;
		
		private int headlineType;
		
		public MatchStats(final String headline, final int headlineType) {
			this.headline = headline;
			this.headlineType = headlineType;
		}

		public String getHeadline() { return headline; }

		public int getHeadlineType() { return headlineType;	}

	}
	
	class MatchResult {
		
		private String score;
		private int matchPosition;
		private int round;
		private Player winner;
		private int newRound;
		private int newPosition;
		private boolean playerA;
		private boolean isFinalMatch;
		
		public MatchResult(final String score, final int matchPosition, final int round, 
								final Player winner, final boolean isFinalMatch) {
			this.score = score;
			this.matchPosition = matchPosition;
			this.round = round;
			this.winner = winner;
			this.newRound = round+1;
			this.newPosition = matchPosition/2;
			this.playerA = (matchPosition%2 == 0) ? true : false;
			this.isFinalMatch = isFinalMatch;
		}

		public String getScore() { return score; }

		public int getMatchPosition() {	return matchPosition; }

		public int getRound() {	return round; }

		public Player getWinner() { return winner; }

		public int getNewRound() { return newRound; }

		public int getNewPosition() { return newPosition; }

		public boolean isPlayerA() { return playerA; }

		public boolean isFinalMatch() { return isFinalMatch; }

	}
	
	class TournamentWinner {

		private String score;
		private int round;
		private Player winner;
		
		public TournamentWinner(final String score, final int round, final Player winner) {
			this.score = score;
			this.round = round;
			this.winner = winner;
		}

		public String getScore() { return score; }

		public int getRound() { return round; }

		public Player getWinner() { return winner; }
	}

}
