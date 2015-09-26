package com.github.damianmcdonald.grandslamsim.simengine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.github.damianmcdonald.grandslamsim.dispatchers.WebSocketMessageDispatcher;
import com.github.damianmcdonald.grandslamsim.dispatchers.WebSocketMessageDispatcher.HEADLINE_TYPE;
import com.github.damianmcdonald.grandslamsim.domain.Player;

public class MatchUp {
	
	private WebSocketMessageDispatcher messageDispatcher;
	private String tournamentName;
	private Player playerA;
	private Player playerB;
	private Player winner;
	private int playerASets = 0;
	private int playerBSets = 0;
	private int totalSets;
	private String roundName;
	private int round;
	private int matchPosition;
	private List<String> setScore = new ArrayList<String>();
	public final static String[] PLAYERA_SET_SCORES = new String[]{"6-0", "6-1", "6-2", "6-3", "6-4", "7-5", "7-6"};
	public final static String[] PLAYERB_SET_SCORES = new String[]{"0-6", "1-6", "2-6", "3-6", "4-6", "5-7", "6-7"};
	private final long id = new Random().nextLong();
	
	public MatchUp(WebSocketMessageDispatcher messageDispatcher, String tournamentName, Player playerA, Player playerB, 
					int totalSets, String roundName, int round, int matchPosition) {
		this.messageDispatcher = messageDispatcher;
		this.tournamentName = tournamentName;
		this.playerA = playerA;
		this.playerB = playerB;
		this.totalSets = totalSets;
		this.roundName = roundName;
		this.round = round;
		this.matchPosition = matchPosition;
	}
	
	public void simulateMatchUp() {
		simulateSets();
		if(winner.equals(playerA)) {
			final String message = getMatchWinnerMessage(playerA.getFormattedName(), playerB.getFormattedName(), getFinalScoreAsHeadline(true), roundName);
			messageDispatcher.dispatchMatchStats(tournamentName, message, HEADLINE_TYPE.MATCH_WINNER.ordinal());
			return;
		}
		if(winner.equals(playerB)) {
			final String message = getMatchWinnerMessage(playerB.getFormattedName(), playerA.getFormattedName(), getFinalScoreAsHeadline(false), roundName);
			messageDispatcher.dispatchMatchStats(tournamentName, message, HEADLINE_TYPE.MATCH_WINNER.ordinal());
			return;
		}
	}
	
	private void simulateSets() {
		int luckFactorA = rand(1,10);
		int luckFactorB = rand(1,10);
		for(int i=0; i<totalSets; i++) {
			int playerAWeight = ((playerA.getSeed()+rand(1,5))*luckFactorA);
			int playerBWeight = ((playerB.getSeed()+rand(1,5))*luckFactorB);
			if(playerAWeight <= playerBWeight) { // player A is the set winner
				playerASets++;
				final String score = PLAYERA_SET_SCORES[rand(1, 6)];
				final String message = getSetWinnerMessage(playerA.getFormattedName(), i+1 , score, roundName);
				messageDispatcher.dispatchMatchStats(tournamentName, message, HEADLINE_TYPE.SET_RESULT.ordinal());
				setScore.add(score);
				if(playerASets > totalSets/2) {
					winner = playerA;
					break;
				}
			}
			// player B is the set winner
			playerBSets++;
			final String score = PLAYERB_SET_SCORES[rand(1, 6)];
			final String reverseScore =  new StringBuilder(score).reverse().toString();
			final String message = getSetWinnerMessage(playerB.getFormattedName(), i+1 , reverseScore, roundName);
			messageDispatcher.dispatchMatchStats(tournamentName, message, HEADLINE_TYPE.SET_RESULT.ordinal());
			setScore.add(score);
			if(playerBSets > totalSets/2) {
				winner = playerB;
				break;
			}
		}
	}
	
	private String getMatchWinnerMessage(final String winner, final String loser, final String score, final String roundName) {
		return String.format("%s has won the %s match against %s: %s", winner, roundName, loser, score);
	}
	
	private String getSetWinnerMessage(final String winner, final int set, final String score, final String roundName) {
		return String.format("%s won set %d: %s", winner, set, score);
	}
	
	private int rand(int min, int max) {
		Random r = new Random();
		return r.nextInt(max - min + 1) + min;
	}
	
	public String getFinalScore() {
		return setScore
				.stream()
				.map(s -> s)
				.collect(Collectors.joining(", "))
			    .toString();
	}
	
	public String getFinalScoreAsHeadline(final boolean isPlayerA) {
		return setScore
				.stream()
				.map(s -> isPlayerA ? s : new StringBuilder(s).reverse().toString())
				.collect(Collectors.joining(", "))
			    .toString();
	}
	
	public String getTournamentName() {
		return tournamentName;
	}

	public Player getPlayerA() {
		return playerA;
	}

	public Player getPlayerB() {
		return playerB;
	}

	public int getTotalSets() {
		return totalSets;
	}

	public List<String> getSetScore() {
		return setScore;
	}

	public Player getWinner() {
		return winner;
	}

	public int getRound() {
		return round;
	}

	public int getMatchPosition() {
		return matchPosition;
	}
	
	@Override
	public int hashCode(){
	    return new HashCodeBuilder()
    		.append(id)
	        .append(playerA)
			.append(playerB)
			.append(winner)
			.append(playerASets)
			.append(playerBSets)
			.append(totalSets)
			.append(roundName)
			.append(round)
			.append(matchPosition)
	        .toHashCode();
	}

	@Override
	public boolean equals(final Object obj){
	    if(obj instanceof MatchUp){
	        final MatchUp other = (MatchUp) obj;
	        return new EqualsBuilder()
        		.append(id, other.id)
	            .append(playerA, other.playerA)
	            .append(playerB, other.playerB)
				.append(winner, other.winner)
				.append(playerASets, other.playerASets)
				.append(playerBSets, other.playerBSets)
				.append(totalSets, other.totalSets)
				.append(roundName, other.roundName)
				.append(round, other.round)
				.append(matchPosition, other.matchPosition)
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
