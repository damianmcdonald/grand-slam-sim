package com.github.damianmcdonald.grandslamsim.simengine;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.github.damianmcdonald.grandslamsim.dispatchers.WebSocketMessageDispatcher;
import com.github.damianmcdonald.grandslamsim.dispatchers.WebSocketMessageDispatcher.HEADLINE_TYPE;
import com.github.damianmcdonald.grandslamsim.domain.Match;
import com.github.damianmcdonald.grandslamsim.domain.Player;
import com.github.damianmcdonald.grandslamsim.domain.Tournament;

public class TournamentEngine {
	
	private WebSocketMessageDispatcher messageDispatcher;
	private final static List<MatchUp> matchups = new ArrayList<MatchUp>();
	private String tournamentName;
	private Tournament tournament;
	private boolean isStarted = false;
	private final long id = new Random().nextLong();
	
	public TournamentEngine(final String tournamentName, WebSocketMessageDispatcher messageDispatcher) {
		this.tournamentName = tournamentName;
		this.messageDispatcher = messageDispatcher;
	}
	
	public Tournament createTournament(final String name, final List<Player> players) {
		matchups.clear();
		final List<MatchUp> initialMatchups = getInitialMatchUps(players);
		final List<Match> matches = initialMatchups
		.stream()
		.map(m -> new Match(m.getPlayerA(), m.getPlayerB(),m.getRound(), m.getMatchPosition()))
		.collect(Collectors.toList()); 
		this.tournament = new Tournament(name, players, matches);
		return this.tournament;
	}
	
	private List<MatchUp> getInitialMatchUps(List<Player> roundPlayers) {
		final List<MatchUp> initialMatchups = new ArrayList<MatchUp>();
		for (int i=0; i<roundPlayers.size(); i+=2) {
			final int roundPos = (i==0) ? 0 : i/2;
			initialMatchups.add(new MatchUp(messageDispatcher, tournamentName, roundPlayers.get(i), roundPlayers.get(i+1), 5, getRoundName(roundPlayers.size()), 0, roundPos));
		}
		return initialMatchups;
	}
	
	public void simulateRounds(List<Player> roundPlayers, int round){
		this.isStarted = true;
		if(roundPlayers.size() == 1) {
			final String message = String.format("Tournament winner is: %s", roundPlayers.get(0).getFormattedName());
			messageDispatcher.dispatchMatchStats(tournamentName, message, HEADLINE_TYPE.MATCH_WINNER.ordinal());
			messageDispatcher.dispatchTournamentWinner(tournamentName, "", round, roundPlayers.get(0));
			return;
		}
		matchups.clear();

		for (int i=0; i<roundPlayers.size(); i+=2) {
		   final String roundName = getRoundName(roundPlayers.size());
		   final String message = String.format("%s match up: %s vs %s", getRoundName(roundPlayers.size()), roundPlayers.get(i).getFormattedName(), roundPlayers.get(i+1).getFormattedName());
		   messageDispatcher.dispatchMatchStats(tournamentName, message, HEADLINE_TYPE.MATCHUP.ordinal());
		   
		   final int roundPos = (i==0) ? 0 : i/2;
		   matchups.add(new MatchUp(messageDispatcher, tournamentName, roundPlayers.get(i), roundPlayers.get(i+1), 5, roundName, round, roundPos));
		}
		matchups.stream().forEach(m -> m.simulateMatchUp());
		matchups.stream().forEach(m -> messageDispatcher.dispatchMatchResult(tournamentName, m));
		List<Player> roundWinners = matchups.stream()
		        .map(p -> p.getWinner())
		        .collect(Collectors.toList()); 
		
		/*
		roundWinners.stream().forEach(w -> {
			   final String message = String.format("Winner %s", w.getFormattedName());
			   messageDispatcher.dispatchMatchStats(message, HEADLINE_TYPE.MATCH_WINNER.ordinal());
		});
		*/

		try {
			Thread.sleep(2000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		simulateRounds(roundWinners, round + 1);
	}
	
	private String getRoundName(final int round) {
		switch (round) {
			case 128:
				return "1st Round";
			case 64:
				return "2nd Round";
			case 32:
				return "3rd Round";
			case 16:
				return "Round of 16";
			case 8:
				return "Quarter Finals";
			case 4:
				return "Semi Finals";
			case 2:
				return "Finals";
			case 1:
				return "Winner";
			default:
				return "Unknown Round";
		}
	}

	public String getTournamentName() {
		return tournamentName;
	}
	
	public Tournament getTournament() {
		return tournament;
	}
	
	public boolean isStarted() {
		return isStarted;
	}
	
	@Override
	public int hashCode(){
	    return new HashCodeBuilder()
    		.append(id)
	        .append(tournament)
			.append(isStarted)
	        .toHashCode();
	}

	@Override
	public boolean equals(final Object obj){
	    if(obj instanceof TournamentEngine){
	        final TournamentEngine other = (TournamentEngine) obj;
	        return new EqualsBuilder()
        		.append(id, other.id)
	            .append(tournament, other.tournament)
				.append(isStarted, other.isStarted)
	            .isEquals();
	    } else{
	        return false;
	    }
	}
	
	@Override
	public String toString() {
	    return ToStringBuilder.reflectionToString(this);
	}
	
}
