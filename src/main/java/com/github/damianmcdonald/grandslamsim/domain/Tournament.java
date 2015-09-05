package com.github.damianmcdonald.grandslamsim.domain;

import java.util.ArrayList;
import java.util.List;

public class Tournament {

	private String name;
	private List<Player> players;
	private List<Match> matches;
	private Integer[] rounds;
	private int maxMatches;
	
	public Tournament(final String name, final List<Player> players, final List<Match> matches) {
		this.name = name;
		this.players = players;
		this.matches = matches;
		this.maxMatches = matches.size();
		this.rounds= getRounds(matches.size());
	}

	private Integer[] getRounds(final int maxSize) {
		List<Integer> roundCnt = new ArrayList<Integer>();
		roundCnt.add(maxSize);
		int accum = maxSize;
		while(accum != 2) {
			if(accum%2 != 0) break;
			accum = accum /2;
			roundCnt.add(accum);
		}
		roundCnt.add(1);
		return roundCnt.toArray(new Integer[roundCnt.size()]);
	}
	
	public String getName() {
		return name;
	}

	public List<Match> getMatches() {
		return matches;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public Integer[] getRounds() {
		return rounds;
	}

	public int getMaxMatches() {
		return maxMatches;
	}
}
