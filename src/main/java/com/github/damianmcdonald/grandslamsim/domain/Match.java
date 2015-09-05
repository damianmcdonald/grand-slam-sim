package com.github.damianmcdonald.grandslamsim.domain;

public class Match {
	
	private Player playerA;
	private Player playerB;
	private int round;
	private int matchPosition;
	
	public Match (final Player playerA, final Player playerB, final int round, final int matchPosition) {
		this.playerA = playerA;
		this.playerB = playerB;
		this.round = round;
		this.matchPosition = matchPosition;
	}

	public Player getPlayerA() {
		return playerA;
	}

	public Player getPlayerB() {
		return playerB;
	}

	public int getRound() {
		return round;
	}

	public int getMatchPosition() {
		return matchPosition;
	}

}
