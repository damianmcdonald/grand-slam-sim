package com.github.damianmcdonald.grandslamsim.domain;

public class TournamentWinner {

	private String score;
	private int round;
	private Player winner;
	
	public TournamentWinner(final String score, final int round, final Player winner) {
		this.score = score;
		this.round = round;
		this.winner = winner;
	}

	public String getScore() {
		return score;
	}

	public int getRound() {
		return round;
	}

	public Player getWinner() {
		return winner;
	}
}
