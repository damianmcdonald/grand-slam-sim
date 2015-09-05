package com.github.damianmcdonald.grandslamsim.domain;

public class MatchResult {
	
	private String score;
	private int matchPosition;
	private int round;
	private Player winner;
	private int newRound;
	private int newPosition;
	private boolean playerA;
	
	public MatchResult(String score, int matchPosition, int round, Player winner) {
		this.score = score;
		this.matchPosition = matchPosition;
		this.round = round;
		this.winner = winner;
		this.newRound = round+1;
		this.newPosition = matchPosition/2;
		this.playerA = (matchPosition%2 == 0) ? true : false;
	}

	public String getScore() {
		return score;
	}

	public int getMatchPosition() {
		return matchPosition;
	}

	public int getRound() {
		return round;
	}

	public Player getWinner() {
		return winner;
	}

	public int getNewRound() {
		return newRound;
	}

	public int getNewPosition() {
		return newPosition;
	}

	public boolean isPlayerA() {
		return playerA;
	}


}
