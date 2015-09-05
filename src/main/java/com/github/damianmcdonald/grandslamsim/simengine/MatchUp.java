package com.github.damianmcdonald.grandslamsim.simengine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.github.damianmcdonald.grandslamsim.dispatchers.WebSocketMessageDispatcher;
import com.github.damianmcdonald.grandslamsim.domain.Player;

public class MatchUp {
	
	private WebSocketMessageDispatcher messageDispatcher;
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
	
	public MatchUp(WebSocketMessageDispatcher messageDispatcher, Player playerA, Player playerB, 
					int totalSets, String roundName, int round, int matchPosition) {
		this.messageDispatcher = messageDispatcher;
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
			final String message = getMatchWinnerMessage(playerA.getFormattedName(), playerB.getFormattedName(), getFinalScore(), roundName);
			messageDispatcher.dispatchMatchStats(message);
			return;
		}
		if(winner.equals(playerB)) {
			final String message = getMatchWinnerMessage(playerB.getFormattedName(), playerA.getFormattedName(), getFinalScore(), roundName);
			messageDispatcher.dispatchMatchStats(message);
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
				messageDispatcher.dispatchMatchStats(message);
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
			messageDispatcher.dispatchMatchStats(message);
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

}
