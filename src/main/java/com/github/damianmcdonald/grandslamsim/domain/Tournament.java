package com.github.damianmcdonald.grandslamsim.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Tournament {

	private String name;
	private List<Player> players;
	private List<Match> matches;
	private Integer[] rounds;
	private int maxMatches;
	private final long id = new Random().nextLong();
	
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
	
	public String getName() { return name; }

	public List<Match> getMatches() { return matches; }
	
	public List<Player> getPlayers() { return players; }

	public Integer[] getRounds() { return rounds; }

	public int getMaxMatches() { return maxMatches;	}
	
	public void setMatches(List<Match> matches) {
		this.matches = matches;
		this.maxMatches = matches.size();
		this.rounds= getRounds(matches.size());
	}
	
	@Override
	public int hashCode(){
	    return new HashCodeBuilder()
    		.append(id)
	        .append(name)
			.append(players)
			.append(matches)
			.append(maxMatches)
			.append(rounds)
	        .toHashCode();
	}

	@Override
	public boolean equals(final Object obj){
	    if(obj instanceof Tournament){
	        final Tournament other = (Tournament) obj;
	        return new EqualsBuilder()
        		.append(id, other.id)
	            .append(name, other.name)
	            .append(players, other.players)
				.append(matches, other.matches)
				.append(maxMatches, other.maxMatches)
				.append(rounds, other.rounds)
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
